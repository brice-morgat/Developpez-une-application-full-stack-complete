package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.MeResponseDto;
import com.openclassrooms.mddapi.dto.UpdateMeRequestDto;
import com.openclassrooms.mddapi.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/me")
  public MeResponseDto me() {
    return userService.getCurrentUserProfile();
  }

  @PutMapping("/me")
  public MeResponseDto updateMe(@Valid @RequestBody UpdateMeRequestDto request) {
    return userService.updateCurrentUser(request);
  }
}

