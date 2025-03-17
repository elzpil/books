package com.app.community.business.repository;

import com.app.community.business.repository.model.CommentDAO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentDAO, Long> {

    List<CommentDAO> findByDiscussionId(Long discussionId);
}
