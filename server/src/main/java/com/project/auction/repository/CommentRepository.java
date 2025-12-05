package com.project.auction.repository;

import com.project.auction.models.Comment;
import com.project.auction.models.User;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface CommentRepository extends CrudRepository<Comment, Long>{

    // все комментарии о пользователе
    List<Comment> findByAddressee(User addressee);

    // все комментарии, написанные пользователем
    List<Comment> findByCommentator(User commentator);
}
