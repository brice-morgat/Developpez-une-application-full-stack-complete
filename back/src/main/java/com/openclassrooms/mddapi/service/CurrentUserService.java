package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.model.User;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

  private final CurrentAuthenticatedUserProvider currentAuthenticatedUserProvider;

  public CurrentUserService(CurrentAuthenticatedUserProvider currentAuthenticatedUserProvider) {
    this.currentAuthenticatedUserProvider = currentAuthenticatedUserProvider;
  }

  public User getCurrentUser() {
    return currentAuthenticatedUserProvider.getCurrentUser();
  }
}
