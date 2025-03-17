package com.app.community.business.mapper;

import com.app.community.business.repository.model.DiscussionDAO;
import com.app.community.model.Discussion;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DiscussionMapper {

    Discussion discussionDAOToDiscussion(DiscussionDAO discussionDAO);

    DiscussionDAO discussionToDiscussionDAO(Discussion discussion);
}
