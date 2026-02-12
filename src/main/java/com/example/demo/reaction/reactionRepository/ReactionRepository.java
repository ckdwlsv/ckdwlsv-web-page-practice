package com.example.demo.reaction.reactionRepository;

import com.example.demo.comment.comment.Comment;
import com.example.demo.post.post.Post;
import com.example.demo.reaction.reaction.Reaction;
import com.example.demo.reaction.reaction.ReactionType;
import com.example.demo.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * ReactionRepository
 *
 * 게시글(Post)과 댓글(Comment)에 대한 사용자의 좋아요/싫어요 반응(Reaction) 정보를
 * DB에서 조회, 저장, 삭제하는 JPA Repository 인터페이스
 *
 * JpaRepository 상속 → CRUD, 페이징, 정렬 기능 기본 제공
 */
public interface ReactionRepository extends JpaRepository<Reaction, Long> {

    /**
     * 특정 사용자가 특정 게시글에 남긴 반응 조회
     *
     * @param user User 엔티티
     * @param post Post 엔티티
     * @return Optional<Reaction> 해당 반응이 존재하면 반환, 없으면 Optional.empty()
     */
    Optional<Reaction> findByUserAndPost(User user, Post post);

    /**
     * 특정 사용자가 특정 댓글에 남긴 반응 조회
     *
     * @param user User 엔티티
     * @param comment Comment 엔티티
     * @return Optional<Reaction> 해당 반응이 존재하면 반환, 없으면 Optional.empty()
     */
    Optional<Reaction> findByUserAndComment(User user, Comment comment);

    /**
     * 특정 게시글에 대한 특정 타입(LIKE/DISLIKE)의 반응 개수 조회
     *
     * @param post Post 엔티티
     * @param type ReactionType (LIKE 또는 DISLIKE)
     * @return long 해당 타입의 반응 개수
     */
    long countByPostAndType(Post post, ReactionType type);

    /**
     * 특정 댓글에 대한 특정 타입(LIKE/DISLIKE)의 반응 개수 조회
     *
     * @param comment Comment 엔티티
     * @param type ReactionType (LIKE 또는 DISLIKE)
     * @return long 해당 타입의 반응 개수
     */
    long countByCommentAndType(Comment comment, ReactionType type);

    /**
     * 여러 댓글 ID에 대해 좋아요/싫어요 집계 조회 (댓글별 총합)
     *
     * JPQL Query 사용:
     *  - comment.id 별로 그룹화
     *  - LIKE, DISLIKE 각각의 합계를 계산
     *
     * @param commentIds 조회할 댓글 ID 컬렉션
     * @return List<Object[]> 각 Object[]:
     *      [0] → commentId
     *      [1] → LIKE 개수
     *      [2] → DISLIKE 개수
     */
    @Query("""
        select r.comment.id,
               sum(case when r.type = 'LIKE' then 1 else 0 end),
               sum(case when r.type = 'DISLIKE' then 1 else 0 end)
        from Reaction r
        where r.comment.id in :commentIds
        group by r.comment.id
    """)
    List<Object[]> aggregateCountsForComments(Collection<Long> commentIds);

    /**
     * 특정 사용자가 남긴 모든 반응 조회
     *
     * @param userId User 엔티티 ID
     * @return List<Reaction> 해당 사용자가 남긴 모든 게시글/댓글 반응 리스트
     */
    List<Reaction> findAllByUserId(Long userId);

}
