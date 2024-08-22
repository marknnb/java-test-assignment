package com.mendix.test.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ExceptionErrorCodes {
    MNDX_BKP_001("Client not found"),
    MNDX_BKP_002("Backup not found"),
    MNDX_BKP_003("Error occurred when downloading file from minIO"),
    MNDX_BKP_004("Error Saving client request"),
    MNDX_BKP_005("Error while accessing minIO bucket"),
    MNDX_BKP_006("Error while access backup file to upload"),
    MNDX_BKP_007("Error while uploading backup file to minIO"),
    MNDX_BKP_008("Backup Process failed with non zero code"),
    MNDX_BKP_009("Backup Process failed"),
    MNDX_BKP_010("MethodArgumentNotValidException exception"),
    MNDX_BKP_011("MethodArgumentTypeMismatchException exception"),
    MNDX_BKP_013("Generic exception");

    private final String message;

}
