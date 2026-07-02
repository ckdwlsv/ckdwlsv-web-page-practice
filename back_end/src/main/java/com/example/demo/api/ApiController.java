package com.example.demo.api;

import com.example.demo.comment.dto.CommentRequestDto;
import com.example.demo.comment.dto.CommentResponseDto;
import com.example.demo.comment.service.CommentService;
import com.example.demo.jwt.JwtUtil;
import com.example.demo.mypage.dto.CommentDto;
import com.example.demo.mypage.dto.PostDto;
import com.example.demo.mypage.dto.UserProfileDto;
import com.example.demo.mypage.service.MyPageService;
import com.example.demo.post.dto.PostRequestDto;
import com.example.demo.post.dto.PostResponseDto;
import com.example.demo.post.post.Post;
import com.example.demo.post.service.PostService;
import com.example.demo.reaction.reaction.Reaction;
import com.example.demo.reaction.reaction.ReactionType;
import com.example.demo.reaction.reactionRepository.ReactionRepository;
import com.example.demo.reaction.reactionService.ReactionService;
import com.example.demo.user.domain.User;
import com.example.demo.user.dto.UserLoginRequestDto;
import com.example.demo.user.dto.UserResponseDto;
import com.example.demo.user.dto.UserSignupRequestDto;
import com.example.demo.user.dto.UserUpdateRequestDto;
import com.example.demo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestPart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final UserService userService;
    private final PostService postService;
    private final CommentService commentService;
    private final ReactionService reactionService;
    private final ReactionRepository reactionRepository;
    private final MyPageService myPageService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupRequestDto dto) {
        try {
            userService.signup(dto);
            return ResponseEntity.ok(Map.of("message", "회원가입이 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequestDto dto) {
        try {
            UserResponseDto user = userService.login(dto);
            String token = jwtUtil.generateToken(user.getUsername());
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", user
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "로그아웃되었습니다."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(Map.of("loggedIn", false));
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.ok(Map.of("loggedIn", false));
        }

        String username = jwtUtil.extractUsername(token);
        UserResponseDto user = userService.findByUsername(username);
        return ResponseEntity.ok(Map.of("loggedIn", true, "user", user));
    }

    @GetMapping("/posts")
    public ResponseEntity<?> posts(@RequestParam(defaultValue = "0") int page) {
        Page<PostResponseDto> result = postService.findAllPaged(page, 10);
        Map<String, Object> response = new HashMap<>();
        response.put("content", result.getContent());
        response.put("page", result.getNumber());
        response.put("size", result.getSize());
        response.put("totalPages", result.getTotalPages());
        response.put("totalElements", result.getTotalElements());
        response.put("hasNext", result.hasNext());
        response.put("hasPrevious", result.hasPrevious());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts")
    public ResponseEntity<?> createPost(@ModelAttribute PostRequestDto dto,
                                        @RequestPart(value = "files", required = false) List<MultipartFile> files,
                                        Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserResponseDto user)) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        User author = userService.findById(user.getId());
        postService.create(dto, author, files);

        return ResponseEntity.ok(Map.of("message", "게시글이 등록되었습니다."));
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> getPostDetail(@PathVariable Long postId, Authentication authentication) {
        Post post = postService.findEntityById(postId);
        List<CommentResponseDto> comments = commentService.getCommentsByPostId(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("post", PostResponseDto.form(post));
        response.put("comments", comments);

        if (authentication != null && authentication.getPrincipal() instanceof UserResponseDto user) {
            User currentUser = userService.findById(user.getId());
            commentService.setReactionSummary(comments, user.getId());
            ReactionType myReaction = reactionRepository.findByUserAndPost(currentUser, post)
                    .map(Reaction::getType)
                    .orElse(null);
            ReactionService.ReactionSummary summary = reactionService.summarizePost(post, myReaction);
            response.put("postReaction", Map.of(
                    "likeCount", summary.getLikeCount(),
                    "dislikeCount", summary.getDislikeCount(),
                    "myReaction", myReaction == null ? null : myReaction.name()
            ));
        } else {
            ReactionService.ReactionSummary summary = reactionService.summarizePost(post, null);
            response.put("postReaction", Map.of(
                    "likeCount", summary.getLikeCount(),
                    "dislikeCount", summary.getDislikeCount(),
                    "myReaction", null
            ));
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/posts/{postId}")
    public ResponseEntity<?> updatePost(@PathVariable Long postId,
                                        @RequestBody PostRequestDto dto,
                                        Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserResponseDto user)) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        postService.update(postId, dto, user.getId());
        return ResponseEntity.ok(Map.of("post", PostResponseDto.form(postService.findEntityById(postId))));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserResponseDto user)) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        postService.delete(postId, user.getId(), user.getRole());
        return ResponseEntity.ok(Map.of("message", "게시글이 삭제되었습니다."));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<?> createComment(@PathVariable Long postId,
                                            @RequestBody Map<String, String> payload,
                                            Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserResponseDto user)) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        CommentRequestDto dto = new CommentRequestDto();
        dto.setPostId(postId);
        dto.setContent(payload.getOrDefault("content", ""));

        CommentResponseDto created = commentService.createComment(dto, user);
        return ResponseEntity.ok(created);
    }

    @DeleteMapping("/posts/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserResponseDto user)) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        Long postId = commentService.deleteComment(commentId, user.getId(), user.getRole());
        return ResponseEntity.ok(Map.of("postId", postId));
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserResponseDto user)) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        User currentUser = userService.findById(user.getId());
        return ResponseEntity.ok(reactionService.togglePost(currentUser, postId, ReactionType.LIKE));
    }

    @PostMapping("/posts/{postId}/dislike")
    public ResponseEntity<?> dislikePost(@PathVariable Long postId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserResponseDto user)) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        User currentUser = userService.findById(user.getId());
        return ResponseEntity.ok(reactionService.togglePost(currentUser, postId, ReactionType.DISLIKE));
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable Long commentId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserResponseDto user)) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        User currentUser = userService.findById(user.getId());
        return ResponseEntity.ok(reactionService.toggleComment(currentUser, commentId, ReactionType.LIKE));
    }

    @PostMapping("/comments/{commentId}/dislike")
    public ResponseEntity<?> dislikeComment(@PathVariable Long commentId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserResponseDto user)) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        User currentUser = userService.findById(user.getId());
        return ResponseEntity.ok(reactionService.toggleComment(currentUser, commentId, ReactionType.DISLIKE));
    }

    @GetMapping("/mypage")
    public ResponseEntity<?> mypage(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserResponseDto user)) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        UserProfileDto profile = myPageService.getUserProfile(user.getId());
        List<PostDto> posts = myPageService.getMyPosts(user.getId());
        List<CommentDto> comments = myPageService.getMyComments(user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("profile", profile);
        response.put("myPosts", posts);
        response.put("myComments", comments);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/mypage")
    public ResponseEntity<?> updateMyPage(@RequestBody UserUpdateRequestDto dto, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserResponseDto user)) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        UserResponseDto updated = userService.updateProfile(user.getId(), dto);
        return ResponseEntity.ok(Map.of("user", updated));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserResponseDto user)) {
            return ResponseEntity.status(401).body(Map.of("error", "로그인이 필요합니다."));
        }

        if (user.getRole() != com.example.demo.user.domain.UserRole.ADMIN) {
            return ResponseEntity.status(403).body(Map.of("error", "관리자만 사용자 삭제가 가능합니다."));
        }

        userService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "사용자가 삭제되었습니다."));
    }
}
