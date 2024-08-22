package com.mendix.test.service.constants;

import io.minio.MinioClient;

public class ServiceConstants {
    public static final String PG_PASSWORD = "PGPASSWORD";
    public static final String PG_DUMP_COMMAND = "pg_dump -h %s -p %s -U %s -d %s -F c -b -v -f %s";
    public static final String SPACE = " ";
    public static final String APPLICATION_SQL = "application/sql";

    public static final String OK = "OK";
    public static final String BACKUP_FILE_NAME = "backup.sql";

}
