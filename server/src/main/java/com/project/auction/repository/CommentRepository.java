package com.project.auction.repository;

import com.project.auction.models.Comment;
import com.project.auction.models.User;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long>{

    // все комментарии о пользователе
    List<Comment> findByAddresseeId(User addressee);

    // все комментарии, написанные пользователем
    List<Comment> findByCommentatorId(User commentator);
}
