package com.mendix.test.service;

import com.mendix.test.exception.MinIOException;
import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.mendix.test.exception.ExceptionErrorCodes.*;
import static com.mendix.test.service.constants.ServiceConstants.APPLICATION_SQL;

/**
 * <p>MinIOService is responsible for connecting to minIO bucket and uploading the backup</p>
 * <ul>
 *     <li>If bucket is not present this service will create bucket first</li>
 *     <li>Folder structure for this service is client/backup id </li>
 *     <li>This service also responsible for downloading file from bucket</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class MinIOService {

    @Value("${minio.bucketname:db-backups}")
    private String bucketName;

    private final MinioClient minioClient;

    public void uploadFile(File file, String fileName) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException |
                 ServerException | XmlParserException e) {
            throw new MinIOException(MNDX_BKP_005.toString());
        }

        // Upload the file
        try (InputStream inputStream = new FileInputStream(file)) {
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                                    inputStream, file.length(), -1)
                            .contentType(APPLICATION_SQL)
                            .build());
        } catch (IOException e) {
            throw new MinIOException(MNDX_BKP_006.toString());
        } catch (ServerException | InsufficientDataException | ErrorResponseException |
                 NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                 InternalException e) {
            throw new MinIOException(MNDX_BKP_007.toString());
        }
    }

    public InputStream downloadFile(String fileName) throws Exception {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(fileName)
                .build());
    }
}
