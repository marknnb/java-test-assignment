package com.mendix.test.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    @JsonFormat(shape = STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private Date timestamp;
    private  String errorCode;
    private  String errorMessage;
    private  int status;
    private  String clientUrl;
}
