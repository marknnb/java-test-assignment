package com.mendix.test.model.get_client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ClientData {
    private String id;
    private String credentials;
    private String tariff;

}
