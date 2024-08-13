package com.jetcab.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse implements Serializable {
    private final String type = "error";
    private String message;
    private List<String> messages;

    public ErrorResponse(String message) {
        this.message = message;
    }

    public ErrorResponse(List<String> messages) {
        this.messages = messages;
    }
}
