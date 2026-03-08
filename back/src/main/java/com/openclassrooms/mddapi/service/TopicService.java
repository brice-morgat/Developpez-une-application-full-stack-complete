package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.TopicDto;
import com.openclassrooms.mddapi.repository.TopicRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TopicService {

  private final TopicRepository topicRepository;

  public TopicService(TopicRepository topicRepository) {
    this.topicRepository = topicRepository;
  }

  public List<TopicDto> getAllTopics() {
    return topicRepository.findAll().stream()
        .map(topic -> new TopicDto(topic.getId(), topic.getName(), topic.getDescription()))
        .toList();
  }
}

