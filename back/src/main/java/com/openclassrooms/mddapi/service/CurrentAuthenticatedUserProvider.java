package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.model.User;

public interface CurrentAuthenticatedUserProvider {

  User getCurrentUser();
}
