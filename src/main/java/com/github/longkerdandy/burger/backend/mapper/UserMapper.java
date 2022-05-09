package com.github.longkerdandy.burger.backend.mapper;

import com.github.longkerdandy.burger.backend.dto.request.UserRequest;
import com.github.longkerdandy.burger.backend.dto.response.UserResponse;
import com.github.longkerdandy.burger.backend.model.User;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for {@link User}.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

  /**
   * Mapping from {@link UserRequest} to for {@link User}.
   *
   * @param request {@link UserRequest}
   * @return Mapped {@link User}
   */
  User requestToUser(UserRequest request);

  /**
   * Mapping from {@link User} to for {@link UserResponse}.
   *
   * @param user {@link User}
   * @return Mapped {@link UserResponse}
   */
  UserResponse userToResponse(User user);
}
