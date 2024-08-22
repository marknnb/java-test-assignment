package com.mendix.test.controller;

import com.mendix.test.controller.annotation.MendixController;
import com.mendix.test.model.backup.BackupRequest;
import com.mendix.test.model.backup.BackupResponse;
import com.mendix.test.service.BackupService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.mendix.test.controller.constants.ControllerConstants.*;


@Slf4j
@AllArgsConstructor
@MendixController
public class BackupController {
    private final BackupService backupService;

    @PostMapping(REQUEST_BACKUP)
    public ResponseEntity<BackupResponse> backup(@RequestBody @Valid BackupRequest backupRequest) {
        return ResponseEntity.ok(backupService.createBackup(backupRequest));
    }

    @GetMapping(GET_BACKUP)
    public ResponseEntity<BackupResponse> getBackup(@PathVariable(name = "client_id") long clientId,
                                                    @PathVariable(name = "backup_id") long backupId) {
        return ResponseEntity.ok(backupService.getBackupByClientAndBackupId(clientId,backupId));
    }

    @GetMapping(BACKUP_DOWNLOAD)
    public ResponseEntity<Resource> downloadBackup(@PathVariable(name = "client_id") long clientId,
                                                   @PathVariable(name = "backup_id") long backupId) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, ATTACHMENT_FILENAME_BACKUP_SQL)
                .body(backupService.downloadFileByClientAndBackupId(clientId,backupId));
    }
}
