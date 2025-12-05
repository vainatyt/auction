package com.project.auction.repository;

import com.project.auction.models.Comment;
import com.project.auction.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // все комментарии о пользователе
    List<Comment> findByAddressee(User addressee);

    // все комментарии, написанные пользователем
    List<Comment> findByCommentator(User commentator);
}
