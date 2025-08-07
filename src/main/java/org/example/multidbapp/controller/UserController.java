package org.example.multidbapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.multidbapp.model.GlobalResponse;
import org.example.multidbapp.model.user.UserResponse;
import org.example.multidbapp.service.user.UserService;
import org.example.multidbapp.utils.swaggerAnnotation.OkResponse;
import org.example.multidbapp.validator.UserValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {
    private final UserService userService;
    private final UserValidator userValidator;

    @Operation(
            summary = "(Amir) Endpoint for get list of all users",
            description = "Get list of all users from all data bases <br>Not need authorize",
            operationId = "getAllUsers",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success")
            }
    )
    @OkResponse
    @GetMapping("/users")
    public ResponseEntity<GlobalResponse<List<UserResponse>>> getAllUsers(){
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(
            summary = "(Amir) Endpoint for get list of all users by filter",
            description = """
                    Get list of all users by filter from all data bases. 
                    <br> Filter: 
                    <br> 1) Filter by username 
                    <br> 2) Filter by name 
                    <br> 3) Filter by surname 
                    <br>Not need authorize""",
            operationId = "getAllUsersByFilter",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GlobalResponse.class),
                                    examples = @ExampleObject( value = """
                {
                                                  "isSuccess": true,
                                                  "data": [
                                                    {
                                                      "id": 1,
                                                      "username": "login@gmail.com",
                                                      "name": "Test",
                                                      "surname": "Testov"
                                                    }
                                                  ]
                                                }
            """))),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = GlobalResponse.class),
                                    examples = @ExampleObject(name = "VALIDATION_ERROR", description = "Validation error", value = """
                {
                               "isSuccess": false,
                               "data": "Filter cannot be more than 100 characters"
                             }
            """))),
            }
    )
    @GetMapping("/users-by-filter")
    public ResponseEntity<GlobalResponse<?>> getAllUsersByFilter(@RequestParam String filter){
        if (!userValidator.isValidFilterValue(filter)){
            return ResponseEntity.badRequest().body(new GlobalResponse<>(false, "Filter cannot be more than 100 characters"));
        }
        return ResponseEntity.ok(userService.getAllUsersWithFilter(filter));
    }
}
