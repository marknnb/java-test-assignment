package com.mendix.test.service;

import com.mendix.test.model.backup.BackupRequest;
import com.mendix.test.model.backup.BackupResponse;
import com.mendix.test.model.create_client.CreateClientRequest;
import com.mendix.test.model.create_client.CreateClientResponse;
import com.mendix.test.model.get_client.ClientResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;

@Service
public interface BackupService {
    CreateClientResponse createClient(CreateClientRequest createClientRequest);

    ClientResponse getClientInfo(long clientId);

    BackupResponse createBackup(BackupRequest backupRequest);

    BackupResponse getBackupByClientAndBackupId(long clientId,long backupId);

    InputStreamResource downloadFileByClientAndBackupId(long clientId,long backupId);
}
