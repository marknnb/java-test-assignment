package com.mendix.test.controller;

import io.minio.BucketArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public abstract class AbstractIntegrationTest {
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:13"))
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("create_tables.sql");

    static final MinIOContainer minio = new MinIOContainer("minio/minio:RELEASE.2023-09-04T19-57-37Z")
            .withEnv("MINIO_ACCESS_KEY", "minioadmin")
            .withEnv("MINIO_SECRET_KEY", "minioadmin");

    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        postgres.start();
        minio.start();

        System.setProperty("MINIO_PORT", String.valueOf(minio.getFirstMappedPort()));
        System.setProperty("MINIO_URL", String.valueOf(minio.getS3URL()));
        System.setProperty("MINIO_ACCESS_KEY", String.valueOf(minio.getUserName()));
        System.setProperty("MINIO_SECRET_KEY", String.valueOf(minio.getPassword()));
        System.setProperty("SPRING_DATASOURCE_URL", String.valueOf(postgres.getJdbcUrl()));
        System.setProperty("SPRING_DATASOURCE_USERNAME", String.valueOf(postgres.getUsername()));
        System.setProperty("SPRING_DATASOURCE_PASSWORD", String.valueOf(postgres.getPassword()));
    }
}
