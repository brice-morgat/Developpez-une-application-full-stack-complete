package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.TopicDto;
import com.openclassrooms.mddapi.model.Topic;
import org.springframework.stereotype.Component;

@Component
public class TopicMapper {

  public TopicDto toDto(Topic topic) {
    return new TopicDto(topic.getId(), topic.getName(), topic.getDescription());
  }
}

