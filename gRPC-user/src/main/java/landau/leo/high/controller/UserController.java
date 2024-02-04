package landau.leo.high.controller;

import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import landau.leo.high.dto.DialogMessageRequest;
import landau.leo.high.dto.DialogMessageResponse;
import landau.leo.high.dto.GetUserResponse;
import landau.leo.high.dto.GetUserShortInfoResponse;
import landau.leo.high.dto.LoginUserRequest;
import landau.leo.high.dto.PostResponse;
import landau.leo.high.dto.RegisterUserRequest;
import landau.leo.high.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "User Controller", description = "Endpoints for user authentication and registration")
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "User authentication", description = "Authenticate user and return access token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    public ResponseEntity<String> login(
            @Parameter(name = "User credentials", required = true)
            @RequestBody LoginUserRequest loginUserRequest) {

        String accessToken = userService.authenticateUser(loginUserRequest);
        return ResponseEntity.ok(accessToken);
    }

    @PostMapping("/user/register")
    @Operation(summary = "User registration", description = "Register a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    public ResponseEntity<String> register(
            @Parameter(name = "New user details", required = true)
            @RequestBody RegisterUserRequest registerUserRequest) {

        String userId = userService.registerUser(registerUserRequest);
        return ResponseEntity.ok(userId);
    }

    @GetMapping("/user/get/{id}")
    @Operation(summary = "Get user profile", description = "Get user profile by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    public ResponseEntity<GetUserResponse> getUserProfile(
            @Parameter(name = "User ID", required = true)
            @PathVariable String id) {

        GetUserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user/search")
    @Operation(summary = "User profile search", description = "Get user profile short info by first name and second name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    public ResponseEntity<List<GetUserShortInfoResponse>> searchUserProfile(
            @Parameter(name = "firstName", required = true)
            @RequestParam String firstName, @Parameter(name = "secondName", required = true)
            @RequestParam String secondName) {
        return ResponseEntity.ok(userService.getUserByFirstAndSecondName(firstName, secondName));
    }

    @PostMapping("/user/load")
    @Operation(summary = "Load default users")
    public void loadUsers() {
        userService.loadDefaultUsers();
    }

    @PostMapping("/count")
    @Operation(summary = "Count users")
    public Integer countUsers() {
        return userService.countUsers();
    }

    @GetMapping("/post/feed")
    @Operation(summary = "Users post", description = "Get users posts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user posts"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    public ResponseEntity<List<PostResponse>> getFriendsPosts(
            @Parameter(name = "offset", example = "0") @RequestParam @Min(0) @Max(100) int offset,
            @Parameter(name = "limit", example = "10") @Min(1) @RequestParam long limit) {
        return ResponseEntity.ok(userService.getUsersPosts(offset, limit));
    }

    @PostMapping("/post/load")
    @Operation(summary = "Load default posts")
    public void loadPosts() {
        userService.loadDefaultPosts();
    }

    @GetMapping("/dialog/{user_id}/list")
    @Operation(summary = "Get user dialogs", description = "Get user dialogs by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user dialogs"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    public ResponseEntity<List<DialogMessageResponse>> getUsersDialogs(
            @Parameter(name = "User ID", required = true)
            @PathVariable("user_id") String userId) {

        List<DialogMessageResponse> list = userService.getUsersDialogs(userId);
        return ResponseEntity.ok(list);
    }

    @PostMapping("/dialog/{user_id}/send")
    @Operation(summary = "Send user message", description = "Send user message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user dialogs"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    public void sendUserMessage(
            @Parameter(name = "User ID", required = true)
            @PathVariable("user_id") UUID userId,
            @RequestBody DialogMessageRequest messageRequest) {

        userService.send(userId, messageRequest.getTo(), messageRequest.getText());
    }

}
