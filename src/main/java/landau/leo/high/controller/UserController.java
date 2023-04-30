package landau.leo.high.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import landau.leo.high.dto.LoginUserRequest;
import landau.leo.high.dto.RegisterUserRequest;
import landau.leo.high.dto.GetUserResponse;
import landau.leo.high.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Api(tags = "User Controller", description = "Endpoints for user authentication and registration")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    @ApiOperation(value = "User authentication", notes = "Authenticate user and return access token")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully authenticated", response = String.class),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 503, message = "Service unavailable")
    })
    public ResponseEntity<String> login(
            @ApiParam(value = "User credentials", required = true)
            @RequestBody LoginUserRequest loginUserRequest) {

        String accessToken = userService.authenticateUser(loginUserRequest);
        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/user/register")
    @ApiOperation(value = "User registration", notes = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully registered", response = String.class),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 503, message = "Service unavailable")
    })
    public ResponseEntity<String> register(
            @ApiParam(value = "New user details", required = true)
            @RequestBody RegisterUserRequest registerUserRequest) {

        String userId = userService.registerUser(registerUserRequest);
        return ResponseEntity.ok(userId);
    }

    @GetMapping("/user/get/{id}")
    @ApiOperation(value = "Get user profile", notes = "Get user profile by ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved user profile", response = GetUserResponse.class),
            @ApiResponse(code = 400, message = "Invalid request"),
            @ApiResponse(code = 404, message = "User not found"),
            @ApiResponse(code = 500, message = "Internal server error"),
            @ApiResponse(code = 503, message = "Service unavailable")
    })
    public ResponseEntity<GetUserResponse> getUserProfile(
            @ApiParam(value = "User ID", required = true)
            @PathVariable String id) {

        GetUserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

}
