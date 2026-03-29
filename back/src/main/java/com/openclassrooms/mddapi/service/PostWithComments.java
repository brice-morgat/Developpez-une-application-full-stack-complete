package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.Post;
import java.util.List;

public record PostWithComments(Post post, List<Comment> comments) {}
