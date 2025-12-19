package com.project.auction.repository;

import com.project.auction.models.Comment;
import com.project.auction.models.User;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
public interface CommentRepository extends JpaRepository<Comment, Long>{

    // все комментарии о пользователе
    List<Comment> findByAddresseeId(User addressee);

    // все комментарии, написанные пользователем
    List<Comment> findByCommentatorId(User commentator);

    // все комментарии о пользователе
    Page<Comment> findByIdAddresseeId(Long addresseeId, Pageable pageable);

    // все комментарии, написанные пользователем
    Page<Comment> findByIdCommentatorId(Long commentatorId, Pageable pageable);
}
