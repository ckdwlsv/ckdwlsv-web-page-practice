package com.example.demo.reaction.reactionService;

import com.example.demo.comment.comment.Comment;
import com.example.demo.comment.repository.CommentRepository;
import com.example.demo.post.post.Post;
import com.example.demo.post.repository.PostRepository;
import com.example.demo.reaction.reaction.Reaction;
import com.example.demo.reaction.reaction.ReactionType;
import com.example.demo.reaction.reactionRepository.ReactionRepository;
import com.example.demo.user.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ReactionService
 *
 * 게시글(Post)와 댓글(Comment)에 대한 사용자의 좋아요/싫어요 반응(Reaction)을
 * 생성, 수정, 삭제, 집계하는 비즈니스 로직 서비스 클래스
 *
 * 핵심 기능:
 * 1. 토글 기능: 이미 좋아요/싫어요가 존재하면 삭제 또는 변경
 * 2. 집계 기능: 현재 반응 상태와 총 좋아요/싫어요 개수를 조회
 */
@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    /**
     * 특정 게시글에 대한 사용자의 좋아요/싫어요 토글
     *
     * @param user 사용자 엔티티
     * @param postId 게시글 ID
     * @param newType 새 반응 타입 (LIKE / DISLIKE)
     * @return ReactionSummary 현재 게시글의 반응 집계 및 내 반응 상태
     */
    @Transactional
    public ReactionSummary togglePost(User user, Long postId, ReactionType newType) {
        // 게시글 존재 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. id=" + postId));

        // 이미 존재하는 사용자의 반응 조회
        return reactionRepository.findByUserAndPost(user, post)
                .map(existing -> {
                    if (existing.getType() == newType) {
                        // 기존 반응과 동일하면 삭제 → 토글 OFF
                        reactionRepository.delete(existing);
                        return summarizePost(post, null);
                    } else {
                        // 기존 반응과 다르면 타입 변경
                        existing.changeType(newType);
                        return summarizePost(post, newType);
                    }
                })
                .orElseGet(() -> {
                    // 사용자의 반응이 없으면 새로 생성
                    reactionRepository.save(Reaction.forPost(user, post, newType));
                    return summarizePost(post, newType);
                });
    }

    /**
     * 특정 댓글에 대한 사용자의 좋아요/싫어요 토글
     *
     * @param user 사용자 엔티티
     * @param commentId 댓글 ID
     * @param newType 새 반응 타입 (LIKE / DISLIKE)
     * @return ReactionSummary 현재 댓글의 반응 집계 및 내 반응 상태
     */
    @Transactional
    public ReactionSummary toggleComment(User user, Long commentId, ReactionType newType) {
        // 댓글 존재 확인
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. id=" + commentId));

        // 이미 존재하는 사용자의 반응 조회
        return reactionRepository.findByUserAndComment(user, comment)
                .map(existing -> {
                    if (existing.getType() == newType) {
                        // 기존 반응과 동일하면 삭제 → 토글 OFF
                        reactionRepository.delete(existing);
                        return summarizeComment(comment, null);
                    } else {
                        // 기존 반응과 다르면 타입 변경
                        existing.changeType(newType);
                        return summarizeComment(comment, newType);
                    }
                })
                .orElseGet(() -> {
                    // 사용자의 반응이 없으면 새로 생성
                    reactionRepository.save(Reaction.forComment(user, comment, newType));
                    return summarizeComment(comment, newType);
                });
    }

    /**
     * 게시글의 반응 총합 및 특정 사용자의 내 반응 요약
     *
     * @param post 게시글 엔티티
     * @param my 내 반응 타입 (LIKE / DISLIKE) 또는 null
     * @return ReactionSummary 게시글 좋아요/싫어요 개수 및 내 반응
     */
    @Transactional(readOnly = true)
    public ReactionSummary summarizePost(Post post, ReactionType my) {
        long likes = reactionRepository.countByPostAndType(post, ReactionType.LIKE);
        long dislikes = reactionRepository.countByPostAndType(post, ReactionType.DISLIKE);
        return ReactionSummary.of(likes, dislikes, my);
    }

    /**
     * 댓글의 반응 총합 및 특정 사용자의 내 반응 요약
     *
     * @param comment 댓글 엔티티
     * @param my 내 반응 타입 (LIKE / DISLIKE) 또는 null
     * @return ReactionSummary 댓글 좋아요/싫어요 개수 및 내 반응
     */
    @Transactional(readOnly = true)
    public ReactionSummary summarizeComment(Comment comment, ReactionType my) {
        long likes = reactionRepository.countByCommentAndType(comment, ReactionType.LIKE);
        long dislikes = reactionRepository.countByCommentAndType(comment, ReactionType.DISLIKE);
        return ReactionSummary.of(likes, dislikes, my);
    }

    /**
     * 반응 집계 DTO
     *
     * likeCount    : 좋아요 총합
     * dislikeCount : 싫어요 총합
     * myReaction   : 로그인 사용자의 내 반응 상태
     */
    @Getter
    @RequiredArgsConstructor(staticName = "of")
    public static class ReactionSummary {
        private final long likeCount;
        private final long dislikeCount;
        private final ReactionType myReaction;
    }

}
