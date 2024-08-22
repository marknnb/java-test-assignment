package com.mendix.test.service.impl;

import com.mendix.test.entity.Backups;
import com.mendix.test.entity.Client;
import com.mendix.test.entity.TariffPlan;
import com.mendix.test.exception.BackupNotFoundException;
import com.mendix.test.exception.ClientNotFoundException;
import com.mendix.test.exception.CreateClientException;
import com.mendix.test.exception.MinIOException;
import com.mendix.test.model.backup.BackupRequest;
import com.mendix.test.model.backup.BackupResponse;
import com.mendix.test.model.backup.Status;
import com.mendix.test.model.create_client.CreateClientRequest;
import com.mendix.test.model.create_client.CreateClientResponse;
import com.mendix.test.model.create_client.Credentials;
import com.mendix.test.model.get_client.BackupData;
import com.mendix.test.model.get_client.ClientData;
import com.mendix.test.model.get_client.ClientResponse;
import com.mendix.test.model.get_client.TariffData;
import com.mendix.test.repository.BackupRepository;
import com.mendix.test.repository.ClientRepository;
import com.mendix.test.repository.TariffPlanRepository;
import com.mendix.test.service.BackupService;
import com.mendix.test.service.MinIOService;
import com.mendix.test.util.ServiceUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mendix.test.exception.ExceptionErrorCodes.*;
import static com.mendix.test.service.constants.ServiceConstants.BACKUP_FILE_NAME;
import static com.mendix.test.service.constants.ServiceConstants.OK;

@RequiredArgsConstructor
@Slf4j
@Service
public class BackupServiceImpl implements BackupService {
    private final ClientRepository clientRepository;
    private final TariffPlanRepository tariffPlanRepository;
    private final BackupRepository backupRepository;
    private final MinIOService minIOService;

    /**
     *This method is responsible for creating client.
     * @param createClientRequest request from client
     * @return {@link  com.mendix.test.model.create_client.CreateClientResponse} contains client id
     */
    @Override
    public CreateClientResponse createClient(CreateClientRequest createClientRequest) {
        return Optional.of(saveClientRequest(createClientRequest))
                .map(savedClient -> CreateClientResponse
                        .builder()
                        .id(savedClient.getId().toString())
                        .status(OK)
                        .build()).orElseThrow(() -> new CreateClientException(MNDX_BKP_004.toString()));

    }

    /**
     * This method is responsible for providing client details.
     * Response consists of <br/>
     * <ul>
     *     <li>Client DB details</li>
     *     <li>Client Tariff data</li>
     *     <li>Client Backup details</li>
     * </ul>
     * @param clientId request from client
     * @return {@link com.mendix.test.model.get_client.ClientResponse}
     */

    @Override
    public ClientResponse getClientInfo(long clientId) {
        return clientRepository.findById(clientId)
                .map(client -> ClientResponse.
                        builder().
                        clientData(getClientData(client)).
                        backupDataList(getBackupsData(clientId)).
                        tariffData(getTariffData(client.getTariffPlan())).
                        build())
                .orElseThrow(() -> new ClientNotFoundException(MNDX_BKP_001.toString()));
    }


    /**
     * createBackup method is responsible for placing backup request in database for given client
     * @param backupRequest client request
     * @return {@link com.mendix.test.model.backup.BackupResponse} contains backup id
     */
    @Override
    public BackupResponse createBackup(BackupRequest backupRequest) {
        return clientRepository.findById(backupRequest.getClientId())
                .map(this::createAndUploadBackup)
                .map(backup -> BackupResponse
                        .builder()
                        .startDate(Optional.ofNullable(backup.getStartDate()).map(this::getLocalDate).orElse(null))
                        .id(Optional.ofNullable(backup.getId()).map(Object::toString).orElse(null))
                        .status(Optional.ofNullable(backup.getStatus()).map(Status::valueOf).orElse(null))
                        .build())
                .orElseThrow(() -> new ClientNotFoundException(MNDX_BKP_001.toString()));
    }

    /**
     *getBackupByClientAndBackupId provides details of backup request in database
     * @param clientId valid client id
     * @param backupId valid backupId
     * @return {@link com.mendix.test.model.backup.BackupResponse}
     */
    @Override
    public BackupResponse getBackupByClientAndBackupId(long clientId, long backupId) {
        return Optional.ofNullable(backupRepository.findBackupsByIdAndClient_Id(backupId, clientId))
                .map(backup -> BackupResponse
                        .builder()
                        .backupTime((Optional.ofNullable(backup.getBackupTime()).map(t -> t / 1000 + " sec").orElse(null)))
                        .databaseSize(Optional.ofNullable(backup.getDatabaseSize()).map(s -> ((s / 1024) / 1024) + " MB").orElse(null))
                        .startDate(Optional.ofNullable(backup.getStartDate()).map(this::getLocalDate).orElse(null))
                        .endDate(Optional.ofNullable(backup.getEndDate()).map(this::getLocalDate).orElse(null))
                        .id(backup.getId().toString())
                        .status(Status.valueOf(backup.getStatus()))
                        .build()).orElseThrow(() -> new BackupNotFoundException(MNDX_BKP_002.toString()));

    }

    /**
     * <p>downloadFileByClientAndBackupId method connects to minIO and download the file based on clientId and backupId</p>
     * @param clientId valid clientId
     * @param backupId valid backupId
     * @return InputStreamResource
     */
    @Override
    public InputStreamResource downloadFileByClientAndBackupId(long clientId, long backupId) {
        String fileName = getBackupFileName(clientId, backupId);
        try {
            InputStream inputStream = minIOService.downloadFile(fileName);
            return new InputStreamResource(inputStream);
        } catch (Exception e) {
            log.error("error while downloading file from minIO with exception : {} ", e.getMessage());
            throw new MinIOException(MNDX_BKP_003.toString());
        }
    }


    private ClientData getClientData(Client client) {
       return ClientData
                .builder()
                .id(client.getId().toString())
                .tariff(Optional.ofNullable(client.getTariffPlan()).map(TariffPlan::getName).orElse(null))
                .credentials(client.getCredentials())
                .build();
    }

    private TariffData getTariffData(TariffPlan tariffName) {
        return TariffData
                .builder()
                .id(tariffName.getId().toString())
                .description(tariffName.getDescription())
                .name(tariffName.getName())
                .price(tariffName.getPrice().toString())
                .build();
    }

    private List<BackupData> getBackupsData(long clientId) {
        return backupRepository.findBackupsByClient_Id(clientId)
                .stream()
                .map(backup -> BackupData
                        .builder()
                        .id(Optional.ofNullable(backup.getId()).map(Object::toString).orElse(null))
                        .status(Status.valueOf(backup.getStatus()))
                        .backupTime(Optional.ofNullable(backup.getBackupTime()).map(Object::toString).orElse(null))
                        .startDate(Optional.ofNullable(backup.getStartDate()).map(this::getLocalDate).orElse(null))
                        .endDate(Optional.ofNullable(backup.getEndDate()).map(this::getLocalDate).orElse(null))
                        .databaseSize(Optional.ofNullable(backup.getDatabaseSize()).map(Object::toString).orElse(null))
                        .build()
                ).collect(Collectors.toList());
    }

    private Backups createAndUploadBackup(Client savedClient) {
        Long startBackup = System.currentTimeMillis();
        //create backup
        Backups backupForRequest = Backups.builder()
                .client(savedClient)
                .startDate(startBackup)
                .status(Status.IN_PROGRESS.toString()).build();
        return backupRepository.save(backupForRequest);
    }

    private Client saveClientRequest(CreateClientRequest createClientRequest) {
        Credentials credentials = createClientRequest.getCredentials();
        String credentialsString = ServiceUtil.convertPojoToJsonString(credentials);
        TariffPlan tariffPlan = tariffPlanRepository.findByName(createClientRequest.getTariff().toString());
        Client createNewClient = Client
                .builder()
                .active(true)
                .credentials(credentialsString)
                .tariffPlan(tariffPlan)
                .build();
        return clientRepository.save(createNewClient);
    }

    private String getLocalDate(Long timeValue) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timeValue), ZoneId.systemDefault()
        ).toString();
    }

    @NotNull
    private static String getBackupFileName(long clientId, long backupId) {
        return clientId + "/" + backupId + "/" + BACKUP_FILE_NAME;
    }
}
