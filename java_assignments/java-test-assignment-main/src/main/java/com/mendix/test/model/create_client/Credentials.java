package com.mendix.test.model.create_client;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Credentials {
    @NotNull(message = "username must not be null")
    String username;

    @NotNull(message = "password must not be null")
    String password;

    @NotNull(message = "database must not be null")
    String database;

    @NotNull(message = "host must not be null")
    String host;

    @NotNull(message = "port must not be null")
    String port;
}
