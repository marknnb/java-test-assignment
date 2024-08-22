package com.mendix.test.service;

import com.mendix.test.entity.Backups;
import com.mendix.test.entity.Client;
import com.mendix.test.model.backup.Status;
import com.mendix.test.model.create_client.Credentials;
import com.mendix.test.repository.BackupRepository;
import com.mendix.test.util.ServiceUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * <p>BackupTaskService is responsible for uploading backUp to minIO</p>
 * <ul>
 *     <li>BackupTaskService runs after periodically to check data available records to process</li>
 *     <li>BackupTaskService connects to client dbs and take backup</li>
 *     <li>in final step BackupTaskService will upload back up to minIO</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BackupTaskService {
    private final BackupRepository backupRepository;
    private final ClientDatabaseBackupService clientDatabaseBackupService;
    private final MinIOService minIOService;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void takeBackups() {
        log.info("starting backup process ... ");
        List<Backups> backupsByStatus = backupRepository.findBackupsByStatus(Status.IN_PROGRESS.toString());
        backupsByStatus.forEach(this::createBackupAndUpload);
    }

    private void createBackupAndUpload(Backups savedBackup) {
        Long endBackup;
        try {
            Client savedClient = savedBackup.getClient();
            String credentials = savedClient.getCredentials();
            Credentials savedCredentials = ServiceUtil.convertJsonStringToPojo(credentials, Credentials.class);
            File file = clientDatabaseBackupService.performBackup(savedCredentials);

            //upload backup
            String fileName = savedClient.getId() + "/" + savedBackup.getId() + "/" + "backup.sql";
            minIOService.uploadFile(file, fileName);

            //save backup
            endBackup = System.currentTimeMillis();
            savedBackup.setEndDate(endBackup);
            savedBackup.setDatabaseSize(file.length());
            savedBackup.setBackupTime(endBackup - savedBackup.getStartDate());
            savedBackup.setStatus(Status.DONE.toString());
            file.delete();
            backupRepository.save(savedBackup);
        } catch (Exception e) {
            log.error("error in processing files");
            endBackup = System.currentTimeMillis();
            savedBackup.setEndDate(endBackup);
            savedBackup.setDatabaseSize(0L);
            savedBackup.setBackupTime(endBackup - savedBackup.getStartDate());
            savedBackup.setStatus(Status.FAILED.toString());
        }
    }
}
