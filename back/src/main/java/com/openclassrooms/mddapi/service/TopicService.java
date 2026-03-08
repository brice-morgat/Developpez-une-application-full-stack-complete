package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.TopicDto;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.mapper.TopicMapper;
import com.openclassrooms.mddapi.repository.TopicRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TopicService {

  private final TopicRepository topicRepository;
  private final TopicMapper topicMapper;

  public TopicService(TopicRepository topicRepository, TopicMapper topicMapper) {
    this.topicRepository = topicRepository;
    this.topicMapper = topicMapper;
  }

  public List<TopicDto> getAllTopics() {
    return topicRepository.findAll().stream().map(topicMapper::toDto).toList();
  }

  public TopicDto getTopicById(Long id) {
    return topicRepository
        .findById(id)
        .map(topicMapper::toDto)
        .orElseThrow(() -> new ResourceNotFoundException("Topic not found for id " + id));
  }
}

