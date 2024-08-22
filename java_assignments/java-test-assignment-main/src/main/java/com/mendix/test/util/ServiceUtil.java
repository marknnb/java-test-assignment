package com.mendix.test.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mendix.test.exception.GenericException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import static com.mendix.test.exception.ExceptionErrorCodes.MNDX_BKP_013;

@UtilityClass
@Slf4j
public class ServiceUtil {
    private ObjectMapper objectMapper;

    private  ObjectMapper newMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.registerModule(new Jdk8Module());
        }
        return objectMapper;
    }
    public  static <T> T convertJsonStringToPojo(final String json, final Class<T> pojo) {
        try {
            return newMapper().readValue(json, pojo);
        } catch (JsonProcessingException ex) {
            log.error(ex.getMessage());
            throw new GenericException(MNDX_BKP_013.toString());
        }
    }

    public  static String convertPojoToJsonString(Object pojo) {
        try {
            ObjectWriter ow = newMapper().writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(pojo);
        } catch (JsonProcessingException ex) {
            log.error(ex.getMessage());
            throw new GenericException(MNDX_BKP_013.toString());
        }
    }
}
