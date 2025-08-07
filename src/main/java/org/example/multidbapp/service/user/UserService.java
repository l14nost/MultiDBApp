package org.example.multidbapp.service.user;

import org.example.multidbapp.model.GlobalResponse;
import org.example.multidbapp.model.user.UserResponse;

import java.util.List;

public interface UserService {
    GlobalResponse<List<UserResponse>> getAllUsers();
    GlobalResponse<List<UserResponse>> getAllUsersWithFilter(String filter);
}
