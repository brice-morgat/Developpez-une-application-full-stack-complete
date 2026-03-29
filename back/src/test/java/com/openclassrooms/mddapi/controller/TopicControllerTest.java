package com.openclassrooms.mddapi.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openclassrooms.mddapi.dto.TopicResponseDto;
import com.openclassrooms.mddapi.service.TopicService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

@ExtendWith(MockitoExtension.class)
class TopicControllerTest {

  @Mock private TopicService topicService;

  @InjectMocks private TopicController topicController;

  @Test
  void listTopics_shouldReturnServiceValue() {
    when(topicService.getTopicsForCurrentUser())
        .thenReturn(List.of(new TopicResponseDto(1L, "Java", "Java desc", true)));

    List<TopicResponseDto> result = topicController.listTopics();

    verify(topicService).getTopicsForCurrentUser();
    assertThat(result).hasSize(1);
  }

  @Test
  void subscribe_shouldReturnNoContent() {
    var response = topicController.subscribe(1L);

    verify(topicService).subscribe(1L);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
  }

  @Test
  void unsubscribe_shouldReturnNoContent() {
    var response = topicController.unsubscribe(1L);

    verify(topicService).unsubscribe(1L);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(204));
  }
}
