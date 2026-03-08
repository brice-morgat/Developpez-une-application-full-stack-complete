package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.AuthUserDto;
import com.openclassrooms.mddapi.dto.MeResponseDto;
import com.openclassrooms.mddapi.model.User;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public AuthUserDto toAuthUser(User user) {
    return new AuthUserDto(user.getId(), user.getEmail(), user.getUsername());
  }

  public MeResponseDto toMe(User user, List<Long> subscriptions) {
    return new MeResponseDto(user.getId(), user.getEmail(), user.getUsername(), subscriptions);
  }
}

