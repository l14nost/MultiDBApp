package org.example.multidbapp.service.user;

import org.example.multidbapp.model.GlobalResponse;
import org.example.multidbapp.model.response.user.UserResponse;

import java.util.List;

/**
 * Service for get information about users from all databases
 * @author AmirB
 */
public interface UserService {
    /**
     * Get list of users from all databases
     * @return a response containing list of all users
     */
    GlobalResponse<List<UserResponse>> getAllUsers();

    /**
     * Get list of users from all databases filtered by name, surname or username
     * @return a response containing list of all users matching the filters.
     */
    GlobalResponse<List<UserResponse>> getAllUsersWithFilter(String filter);
}
