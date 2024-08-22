package com.mendix.test.service;

import com.mendix.test.exception.CreateBackupException;
import com.mendix.test.model.create_client.Credentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

import static com.mendix.test.exception.ExceptionErrorCodes.MNDX_BKP_008;
import static com.mendix.test.exception.ExceptionErrorCodes.MNDX_BKP_009;
import static com.mendix.test.service.constants.ServiceConstants.*;

/**
 * <p>ClientDatabaseBackupService is responsible for connecting to client DB and create the backup</p>
 * <ul>
 *     <li>This service requires Credentials to connect to DB</li>
 *     <li>Assumes pg_dump is on classpath</li>
 * </ul>
 */
@Service
@Slf4j
public class ClientDatabaseBackupService {

    public File performBackup(Credentials credentials) {
        String fileName = "backup_" + System.currentTimeMillis() + ".sql";

        String command = String.format(
                PG_DUMP_COMMAND,
                credentials.getHost(),
                credentials.getPort(),
                credentials.getUsername(),
                credentials.getDatabase(),
                fileName);
        log.info("PG_DUMP command :: " + command);
        String[] commandArray = command.split(SPACE);
        ProcessBuilder processBuilder = new ProcessBuilder(commandArray);
        processBuilder.redirectErrorStream(true);
        processBuilder.environment().put(PG_PASSWORD, credentials.getPassword());

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new CreateBackupException(MNDX_BKP_008.toString());
            }
            return new File(fileName);
        } catch (IOException | InterruptedException e) {
            log.error("error while creating backup :: {}", e.getMessage());
            throw new CreateBackupException(MNDX_BKP_009.toString());
        }
    }
}
