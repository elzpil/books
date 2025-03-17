package com.app.users.business.mapper;

import com.app.users.business.repository.model.FollowerDAO;
import com.app.users.model.Follower;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FollowerMapper {
    Follower followerDAOtoFollower(FollowerDAO followerDAO);
    FollowerDAO foollowerToFollowerDAO(Follower follower);
}
