package com.mendix.test.controller;

import com.mendix.test.model.ErrorResponse;
import com.mendix.test.model.create_client.CreateClientRequest;
import com.mendix.test.model.create_client.CreateClientResponse;
import com.mendix.test.model.create_client.Credentials;
import com.mendix.test.model.create_client.Tariff;
import com.mendix.test.model.get_client.ClientResponse;
import com.mendix.test.util.ServiceUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.io.support.ClassicRequestBuilder.post;
import static com.mendix.test.controller.constants.ControllerConstants.CREATE_CLIENT;
import static com.mendix.test.controller.constants.ControllerConstants.GET_CLIENT;
import static com.mendix.test.exception.ExceptionErrorCodes.MNDX_BKP_001;
import static com.mendix.test.exception.ExceptionErrorCodes.MNDX_BKP_010;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class ClientControllerTest extends AbstractIntegrationTest {
    public static final String CONTEXT = "/mendix";
    public static final String EMPTY_REQUEST = "{}";
    public static final int VALID_CLIENT_ID = 100;
    public static final int INVALID_CLIENT_ID = 5000;
    @Autowired
    private MockMvc mockMvc;

    @SneakyThrows
    @Test
    @Order(1)
    @DisplayName("SUCCESS ↣ GIVEN: CreatClient API  ↡ WHEN: valid createClientRequest ↡ THEN: return Valid createClientResponse")
    void when_valid_request_then_valid_response() {
        var createClientRequest = CreateClientRequest.builder().credentials(
                Credentials
                        .builder()
                        .database(postgres.getDatabaseName())
                        .host(postgres.getHost())
                        .password(postgres.getPassword())
                        .port(postgres.getFirstMappedPort().toString())
                        .username(postgres.getUsername())
                        .build()
        ).tariff(Tariff.FREE).build();
        var createClientRequestString = ServiceUtil.convertPojoToJsonString(createClientRequest);
        var responseString = mockMvc.perform(MockMvcRequestBuilders.post(CONTEXT + CREATE_CLIENT)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(createClientRequestString)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentType()).isEqualTo(APPLICATION_JSON_VALUE))
                .andExpect(result -> assertNotNull(result.getResponse().getContentAsString()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        var createClientResponse = ServiceUtil.convertJsonStringToPojo(responseString, CreateClientResponse.class);

        assertAll("verify create client Response",
                () -> assertNotNull(createClientRequestString),
                () -> assertNotNull(createClientResponse.getId()),
                () -> assertNotNull(createClientResponse.getStatus())
        );
    }

    @SneakyThrows
    @Test
    @Order(2)
    @DisplayName("ERROR ↣ GIVEN: CreatClient API  ↡ WHEN: Invalid createClientRequest ↡ THEN: return Error createClientResponse")
    void when_invalid_request_then_error_response() {
        var errorResponseString = mockMvc.perform(MockMvcRequestBuilders.post(CONTEXT + CREATE_CLIENT)
                        .contentType(APPLICATION_JSON_VALUE)
                        .content(EMPTY_REQUEST)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertThat(result.getResponse().getContentType()).isEqualTo(APPLICATION_JSON_VALUE))
                .andExpect(result -> assertNotNull(result.getResponse().getContentAsString()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        var errorResponse = ServiceUtil.convertJsonStringToPojo(errorResponseString, ErrorResponse.class);

        assertAll("verify create client Response",
                () -> assertNotNull(errorResponse),
                () -> assertEquals(errorResponse.getErrorCode(), MNDX_BKP_010.toString())
        );
    }

    @Test
    @SneakyThrows
    @Order(3)
    @DisplayName("Success ↣ GIVEN: get Client API  ↡ WHEN: Invalid get ClientRequest ↡ THEN: return Success get ClientResponse")
    void when_get_Client_invalid_request_then_error_response() {
        var responseString = mockMvc.perform(MockMvcRequestBuilders.get(CONTEXT + GET_CLIENT, VALID_CLIENT_ID)
                        .contentType(APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> assertThat(result.getResponse().getContentType()).isEqualTo(APPLICATION_JSON_VALUE))
                .andExpect(result -> assertNotNull(result.getResponse().getContentAsString()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var clientResponse = ServiceUtil.convertJsonStringToPojo(responseString, ClientResponse.class);

        assertAll("verify get client Response",
                () -> assertNotNull(clientResponse),
                () -> assertEquals(clientResponse.getClientData().getId(), "100"),
                () -> assertEquals(clientResponse.getClientData().getTariff(), "FREE"),
                ()-> assertNotNull(clientResponse.getClientData().getCredentials())
        );
    }

    @SneakyThrows
    @Test
    @Order(4)
    @DisplayName("ERROR ↣ GIVEN: getClient API  ↡ WHEN: Invalid getClientRequest ↡ THEN: return Error getClientResponse")
    void when_get_client_invalid_request_then_error_response() {
        var errorResponseString = mockMvc.perform(MockMvcRequestBuilders.get(CONTEXT + GET_CLIENT, INVALID_CLIENT_ID)
                        .contentType(APPLICATION_JSON_VALUE)
                )
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertThat(result.getResponse().getContentType()).isEqualTo(APPLICATION_JSON_VALUE))
                .andExpect(result -> assertNotNull(result.getResponse().getContentAsString()))
                .andReturn()
                .getResponse()
                .getContentAsString();
        var errorResponse = ServiceUtil.convertJsonStringToPojo(errorResponseString, ErrorResponse.class);

        assertAll("verify get client Response",
                () -> assertNotNull(errorResponse),
                () -> assertEquals(errorResponse.getErrorCode(), MNDX_BKP_001.toString())
        );
    }
}