package com.openclassrooms.mddapi.config;

import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class DevelopmentDataSeeder {

  @Bean
  CommandLineRunner seedData(
      UserRepository userRepository,
      TopicRepository topicRepository,
      SubscriptionRepository subscriptionRepository,
      PostRepository postRepository,
      CommentRepository commentRepository,
      PasswordEncoder passwordEncoder) {
    return args -> {
      if (userRepository.count() > 0 || topicRepository.count() > 0) {
        return;
      }

      LocalDateTime now = LocalDateTime.now();

      Topic javaTopic =
          topicRepository.save(
              Topic.builder()
                  .name("Java")
                  .description("Java ecosystem and backend development")
                  .createdAt(now)
                  .build());

      Topic angularTopic =
          topicRepository.save(
              Topic.builder()
                  .name("Angular")
                  .description("Frontend architecture and Angular best practices")
                  .createdAt(now)
                  .build());

      User alice =
          userRepository.save(
              User.builder()
                  .username("alice")
                  .email("alice@mdd.dev")
                  .password(passwordEncoder.encode("password"))
                  .createdAt(now)
                  .build());

      User bob =
          userRepository.save(
              User.builder()
                  .username("bob")
                  .email("bob@mdd.dev")
                  .password(passwordEncoder.encode("password"))
                  .createdAt(now)
                  .build());

      subscriptionRepository.save(
          Subscription.builder().user(alice).topic(javaTopic).createdAt(now).build());
      subscriptionRepository.save(
          Subscription.builder().user(alice).topic(angularTopic).createdAt(now).build());
      subscriptionRepository.save(
          Subscription.builder().user(bob).topic(javaTopic).createdAt(now).build());

      Post postOne =
          postRepository.save(
              Post.builder()
                  .title("Spring Boot 3 setup tips")
                  .content("A simple baseline for Spring Boot 3 + MySQL in an MVP.")
                  .author(alice)
                  .topic(javaTopic)
                  .createdAt(now.minusDays(1))
                  .build());

      Post postTwo =
          postRepository.save(
              Post.builder()
                  .title("Angular feature folder strategy")
                  .content("Start with core/shared/features and keep modules focused.")
                  .author(bob)
                  .topic(angularTopic)
                  .createdAt(now.minusHours(12))
                  .build());

      commentRepository.save(
          Comment.builder()
              .content("Clear and practical approach.")
              .author(bob)
              .post(postOne)
              .createdAt(now.minusHours(6))
              .build());

      commentRepository.save(
          Comment.builder()
              .content("Good base before adding business rules.")
              .author(alice)
              .post(postTwo)
              .createdAt(now.minusHours(3))
              .build());
    };
  }
}

