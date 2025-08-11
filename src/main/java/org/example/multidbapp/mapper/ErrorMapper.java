package org.example.multidbapp.mapper;

import org.example.multidbapp.model.response.error.ErrorResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

public interface ErrorMapper {
    static List<ErrorResponse> mapBindingResult(BindingResult bindingResult) {
        List<ErrorResponse> errors = new ArrayList<>(bindingResult.getFieldErrorCount());
        for (int i = 0; i < bindingResult.getFieldErrorCount(); i++) {
            FieldError fieldError = bindingResult.getFieldErrors().get(i);
            errors.add(i, new ErrorResponse(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return errors;
    }
}
