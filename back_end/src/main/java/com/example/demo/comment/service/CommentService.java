package com.example.demo.comment.service;

import com.example.demo.comment.comment.Comment;
import com.example.demo.comment.dto.CommentRequestDto;
import com.example.demo.comment.dto.CommentResponseDto;
import com.example.demo.comment.repository.CommentRepository;
import com.example.demo.post.post.Post;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.reaction.reaction.Reaction;
import com.example.demo.reaction.reaction.ReactionType;
import com.example.demo.reaction.reactionRepository.ReactionRepository;
import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserRole;
import com.example.demo.user.dto.UserResponseDto;
import com.example.demo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service // 서비스 계층임을 명시. 비즈니스 로직 담당
@Transactional // 메서드 실행 시 트랜잭션 처리
@RequiredArgsConstructor // final 필드 생성자 주입
public class CommentService {

    private final PostRepository postRepository; // 게시글 조회
    private final CommentRepository commentRepository; // 댓글 CRUD
    private final ReactionRepository reactionRepository; // 댓글 반응 조회/등록
    private final UserService userService; // 사용자 조회 등

    // ---------------------- 댓글 생성 ----------------------
    public CommentResponseDto createComment(CommentRequestDto dto, UserResponseDto userDto){
        // 게시글 존재 여부 확인
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));

        // 사용자 존재 여부 확인
        User user = userService.findById(userDto.getId());

        // 댓글 엔티티 생성
        Comment comment = Comment.createComment(dto.getContent(), post, user);

        // DB 저장
        commentRepository.save(comment);

        // DTO로 변환 후 반환
        return new CommentResponseDto(comment);
    }

    // ---------------------- 게시글별 댓글 조회 ----------------------
    public List<CommentResponseDto> getCommentsByPostId(Long postId){
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId) // 작성일 오름차순
                .stream()
                .map(CommentResponseDto::new) // DTO 변환
                .collect(Collectors.toList());
    }

    // ---------------------- 댓글 삭제 ----------------------
    public Long deleteComment(Long commentId, Long userId, UserRole role){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        // 본인 댓글이 아니거나 ADMIN 권한이 아니면 삭제 불가
        if(!comment.getUser().getId().equals(userId) && role != UserRole.ADMIN){
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        Long postId = comment.getPost().getId(); // 삭제 후 리다이렉트용 게시글 ID
        commentRepository.delete(comment); // DB에서 댓글 삭제

        return postId;
    }

    // ---------------------- 댓글 좋아요/싫어요 토글 ----------------------
    @Transactional
    public Map<String,Object> toggleLike(Long commentId, Long userId, boolean isLike) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

        // 현재는 임시 코드 (실제 Reaction 엔티티 처리 필요)
        boolean likedByUser = false;
        boolean dislikedByUser = false;

        Long likeCount = 0L;
        Long dislikeCount = 0L;

        Map<String, Object> result = new HashMap<>();
        result.put("likeCount", likeCount);
        result.put("dislikeCount", dislikeCount);
        result.put("likedByUser", likedByUser);
        result.put("dislikedByUser", dislikedByUser);
        return result;
    }

    // ---------------------- 댓글 반응 상태 조회 ----------------------
    public Map<String, Object> getReactionStatus(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));

        User user = userService.findById(userId);

        // 사용자가 누른 반응 여부 조회
        Optional<Reaction> myReactionOpt = reactionRepository.findByUserAndComment(user, comment);
        String myReaction = myReactionOpt.map(r -> r.getType().name()).orElse(null);

        // 전체 좋아요/싫어요 수 조회
        long likeCount = reactionRepository.countByCommentAndType(comment, ReactionType.LIKE);
        long dislikeCount = reactionRepository.countByCommentAndType(comment, ReactionType.DISLIKE);

        Map<String, Object> result = new HashMap<>();
        result.put("myReaction", myReaction);
        result.put("likeCount", likeCount);
        result.put("dislikeCount", dislikeCount);

        return result;
    }

    // ---------------------- 댓글 DTO에 반응 요약 세팅 ----------------------
    public void setReactionSummary(List<CommentResponseDto> comments, Long userId) {
        for (CommentResponseDto comment : comments) {
            Map<String, Object> summary = getReactionStatus(comment.getId(), userId); // 댓글별 상태 조회
            comment.setMyReaction((String) summary.get("myReaction"));
            comment.setLikeCount((Long) summary.get("likeCount"));
            comment.setDislikeCount((Long) summary.get("dislikeCount"));
        }
    }

}
