// 패키지 선언: 이 클래스가 속한 패키지를 지정
package com.example.demo.mypage.service;

// 필요한 클래스 import
import com.example.demo.comment.comment.Comment;
import com.example.demo.comment.repository.CommentRepository;
import com.example.demo.mypage.dto.CommentDto;
import com.example.demo.mypage.dto.PostDto;
import com.example.demo.mypage.dto.UserProfileDto;
import com.example.demo.post.post.Post;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.reaction.reaction.Reaction;
import com.example.demo.reaction.reactionRepository.ReactionRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 마이페이지 관련 비즈니스 로직을 처리하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor // final 필드(userRepository 등)에 대한 생성자 자동 생성
public class MyPageService {

    // Repository 의존성 주입
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ReactionRepository reactionRepository;

    /**
     * 사용자 ID로 프로필 조회
     * @param userId 조회할 사용자 ID
     * @return UserProfileDto
     */
    public UserProfileDto getUserProfile(Long userId){
        // DB에서 사용자 조회, 없으면 예외 발생
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // UserProfileDto 생성 후 반환
        return new UserProfileDto(user);
    }

    /**
     * 사용자 ID로 작성한 게시글 조회
     * @param userId 조회할 사용자 ID
     * @return List<PostDto> DTO 리스트
     */
    public List<PostDto> getMyPosts(Long userId) {
        // 사용자 작성 게시글 조회
        List<Post> posts = postRepository.findAllByAuthorId(userId);

        return posts.stream()
                .map(post -> {
                    // 각 게시글에 대한 사용자의 내 반응 조회
                    Reaction myReaction = reactionRepository.findAllByUserId(userId).stream()
                            .filter(r -> r.getPost() != null && r.getPost().getId().equals(post.getId()))
                            .findFirst()
                            .orElse(null);

                    // 내 반응 타입 문자열: "LIKE", "DISLIKE" 또는 null
                    String reactionType = myReaction != null ? myReaction.getType().name() : null;

                    // PostDto로 변환
                    return PostDto.from(post, reactionType);
                })
                .collect(Collectors.toList()); // 리스트로 반환
    }

    /**
     * 사용자 ID로 작성한 댓글 조회
     * @param userId 조회할 사용자 ID
     * @return List<CommentDto> DTO 리스트
     */
    public List<CommentDto> getMyComments(Long userId) {
        // 사용자 작성 댓글 조회
        List<Comment> comments = commentRepository.findAllByUserId(userId);

        return comments.stream()
                .map(comment -> {
                    // 각 댓글에 대한 사용자의 내 반응 조회
                    Reaction myReaction = reactionRepository.findAllByUserId(userId).stream()
                            .filter(r -> r.getComment() != null && r.getComment().getId().equals(comment.getId()))
                            .findFirst()
                            .orElse(null);

                    // 내 반응 타입 문자열: "LIKE", "DISLIKE" 또는 null
                    String reactionType = myReaction != null ? myReaction.getType().name() : null;

                    // CommentDto로 변환
                    return CommentDto.from(comment, reactionType);
                })
                .collect(Collectors.toList()); // 리스트로 반환
    }
}
