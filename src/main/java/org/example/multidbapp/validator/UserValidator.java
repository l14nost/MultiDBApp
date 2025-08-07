package org.example.multidbapp.validator;

import org.springframework.stereotype.Component;

@Component
public class UserValidator {
    public boolean isValidFilterValue(String filter) {
        if (filter == null) return true;
        return filter.length() < 100;
    }
}
