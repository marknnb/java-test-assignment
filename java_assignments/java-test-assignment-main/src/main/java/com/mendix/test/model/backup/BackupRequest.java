package com.mendix.test.model.backup;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackupRequest {
    @JsonProperty("client_id")
    @NotNull(message = "clientId can not be null")
    long clientId;
}
