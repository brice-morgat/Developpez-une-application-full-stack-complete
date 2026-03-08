package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.TopicResponseDto;
import com.openclassrooms.mddapi.service.TopicService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/topics")
public class TopicController {

  private final TopicService topicService;

  public TopicController(TopicService topicService) {
    this.topicService = topicService;
  }

  @GetMapping
  public List<TopicResponseDto> listTopics() {
    return topicService.getTopicsForCurrentUser();
  }

  @PostMapping("/{topicId}/subscribe")
  public ResponseEntity<Void> subscribe(@PathVariable Long topicId) {
    topicService.subscribe(topicId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{topicId}/subscribe")
  public ResponseEntity<Void> unsubscribe(@PathVariable Long topicId) {
    topicService.unsubscribe(topicId);
    return ResponseEntity.noContent().build();
  }
}

