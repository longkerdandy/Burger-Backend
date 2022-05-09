package com.github.longkerdandy.burger.backend.mapper;

import com.github.longkerdandy.burger.backend.dto.request.CommentRequest;
import com.github.longkerdandy.burger.backend.model.Comment;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for {@link Comment}.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

  /**
   * Mapping from {@link CommentRequest} to for {@link Comment}.
   *
   * @param request {@link CommentRequest}
   * @return Mapped {@link Comment}
   */
  Comment requestToComment(CommentRequest request);
}
