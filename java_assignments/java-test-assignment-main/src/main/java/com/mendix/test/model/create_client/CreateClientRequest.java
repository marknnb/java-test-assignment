package com.mendix.test.model.create_client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateClientRequest {

    @NotNull(message = "Please provide credentials as it is mandatory field")
    @Valid
    Credentials credentials;

    @NotNull(message = "Please provide tariff as it is mandatory field")
    Tariff tariff;
}
