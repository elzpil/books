package com.app.users.business.mapper;

import com.app.users.business.repository.model.UserDAO;
import com.app.users.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User userDAOToUser(UserDAO userDAO);

    UserDAO userToUserDAO(User user);
}