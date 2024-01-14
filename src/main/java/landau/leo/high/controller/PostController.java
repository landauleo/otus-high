package landau.leo.high.controller;

import java.util.UUID;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import landau.leo.high.dto.PostRequest;
import landau.leo.high.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Post Controller", description = "Endpoints for post")
public class PostController {

    private final PostService postService;

    @PostMapping("/post/create")
    @Operation(summary = "Create post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "503", description = "Service unavailable")
    })
    public ResponseEntity<UUID> create(
            @Parameter(required = true)
            @RequestBody PostRequest postRequest) {

        UUID postId = postService.createPost(postRequest);
        return ResponseEntity.ok(postId);
    }


}
