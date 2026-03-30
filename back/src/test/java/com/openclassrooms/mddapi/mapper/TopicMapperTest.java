package com.openclassrooms.mddapi.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.openclassrooms.mddapi.model.Topic;
import org.junit.jupiter.api.Test;

class TopicMapperTest {

  private final TopicMapper topicMapper = new TopicMapper();

  @Test
  void toDto_shouldMapAllFields() {
    Topic topic = Topic.builder().id(1L).name("Java").description("desc").build();

    var result = topicMapper.toDto(topic);

    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.name()).isEqualTo("Java");
    assertThat(result.description()).isEqualTo("desc");
  }
}
