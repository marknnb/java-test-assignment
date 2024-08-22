package com.mendix.test.controller;

import com.mendix.test.controller.annotation.MendixController;
import com.mendix.test.model.create_client.CreateClientRequest;
import com.mendix.test.model.create_client.CreateClientResponse;
import com.mendix.test.model.get_client.ClientResponse;
import com.mendix.test.service.BackupService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static com.mendix.test.controller.constants.ControllerConstants.CREATE_CLIENT;
import static com.mendix.test.controller.constants.ControllerConstants.GET_CLIENT;


@Slf4j
@AllArgsConstructor
@MendixController
public class ClientController {
    private final BackupService backupService;
    @PostMapping(CREATE_CLIENT)
    public ResponseEntity<CreateClientResponse> createClient(@RequestBody @Valid CreateClientRequest createClientRequest) {
        return ResponseEntity.ok(backupService.createClient(createClientRequest));
    }

    @GetMapping(GET_CLIENT)
    public ResponseEntity<ClientResponse> getClientInfo(@PathVariable(name = "client_id") long clientId) {
        return ResponseEntity.ok(backupService.getClientInfo(clientId));
    }
}
