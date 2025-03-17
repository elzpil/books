package com.app.community.business.mapper;

import com.app.community.business.repository.model.CommentDAO;
import com.app.community.model.Comment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment commentDAOToComment(CommentDAO commentDAO);

    CommentDAO commentToCommentDAO(Comment comment);
}
