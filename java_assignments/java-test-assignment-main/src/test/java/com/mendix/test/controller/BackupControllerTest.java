package com.mendix.test.controller;

import com.mendix.test.model.backup.BackupRequest;
import com.mendix.test.model.backup.BackupResponse;
import com.mendix.test.util.ServiceUtil;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Objects;

import static com.mendix.test.controller.constants.ControllerConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class BackupControllerTest extends AbstractIntegrationTest {
    public static final String CONTEXT = "/mendix";
    public static final int VALID_CLIENT_ID = 100;
    public static final int VALID_BACK_UP_ID = 100;
    public static final String BUCKET_NAME = "db-backups";
    public static final String BACKUP_FILE = "backup.sql";

    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    @Order(1)
    @DisplayName("SUCCESS ↣ GIVEN: CreatClient API  ↡ WHEN: valid createClientRequest ↡ THEN: return Valid createClientResponse")
    void when_valid_request_then_valid_response() {
        var backupRequest = BackupRequest.builder().clientId(VALID_BACK_UP_ID).build();
        var backupRequestString = ServiceUtil.convertPojoToJsonString(backupRequest);
        var responseString = mockMvc.perform(MockMvcRequestBuilders.post(CONTEXT + REQUEST_BACKUP)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(backupRequestString)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentType()).isEqualTo(APPLICATION_JSON_VALUE))
                .andExpect(result -> assertNotNull(result.getResponse().getContentAsString()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        var backupResponse = ServiceUtil.convertJsonStringToPojo(responseString, BackupResponse.class);

        assertAll("verify backup Response",
                () -> assertNotNull(responseString),
                () -> assertNotNull(backupResponse)
        );
    }

    @Test
    @SneakyThrows
    @Order(3)
    @DisplayName("Success ↣ GIVEN: get backup API  ↡ WHEN: Invalid get backup Request ↡ THEN: return Success get backup Response")
    void when_get_backup_invalid_request_then_error_response() {
        var responseString = mockMvc.perform(MockMvcRequestBuilders.get(CONTEXT + GET_BACKUP, VALID_CLIENT_ID, VALID_BACK_UP_ID)
                        .contentType(APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentType()).isEqualTo(APPLICATION_JSON_VALUE))
                .andExpect(result -> assertNotNull(result.getResponse().getContentAsString()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var clientResponse = ServiceUtil.convertJsonStringToPojo(responseString, BackupResponse.class);

        assertAll("verify get backup Response",
                () -> assertNotNull(clientResponse)
        );
    }

    @Test
    @SneakyThrows
    void downloadBackup() {
        setupMinIOTestData();
        mockMvc.perform(MockMvcRequestBuilders.get(CONTEXT + BACKUP_DOWNLOAD, 200, 200)
                        .contentType(APPLICATION_JSON_VALUE)
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentType()).isEqualTo("application/octet-stream"))
                .andExpect(result -> assertNotNull(result.getResponse().getContentAsString()))
                .andDo(result -> {
                    byte[] content = result.getResponse().getContentAsByteArray();
                    assertTrue(content.length > 0, "content should not be empty");
                });
    }

    private void setupMinIOTestData() throws Exception {
        var minioClient = MinioClient.builder()
                .endpoint("http://" + minio.getHost() + ":" + minio.getFirstMappedPort())
                .credentials(minio.getUserName(), minio.getPassword())
                .build();
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
        String fileName = "200/200/backup.sql";
        ClassLoader classLoader = getClass().getClassLoader();
        String filePath = Objects.requireNonNull(classLoader.getResource(BACKUP_FILE)).getFile();
        File file = new File(filePath);
        InputStream inputStream = new FileInputStream(filePath);
        minioClient.putObject(
                PutObjectArgs.builder().bucket(BUCKET_NAME).object(fileName).stream(
                                inputStream, file.length(), -1)
                        .contentType("application/sql")
                        .build());
    }
}