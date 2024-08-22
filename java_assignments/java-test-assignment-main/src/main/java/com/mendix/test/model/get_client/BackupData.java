package com.mendix.test.model.get_client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mendix.test.model.backup.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BackupData {
    String id;

    @JsonProperty("start_date")
    String startDate;

    @JsonProperty("end_date")
    String endDate;

    @JsonProperty("database_size")
    String databaseSize;

    @JsonProperty("backup_time")
    String backupTime;

    Status status;
}
