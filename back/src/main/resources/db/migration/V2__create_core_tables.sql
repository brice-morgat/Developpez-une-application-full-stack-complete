CREATE TABLE IF NOT EXISTS users (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL,
  password VARCHAR(255) NOT NULL,
  created_at DATETIME NOT NULL,
  CONSTRAINT uk_users_username UNIQUE (username),
  CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS topics (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(120) NOT NULL,
  description VARCHAR(1000) NULL,
  created_at DATETIME NOT NULL,
  CONSTRAINT uk_topics_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS posts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  content VARCHAR(5000) NOT NULL,
  author_id BIGINT NOT NULL,
  topic_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  CONSTRAINT fk_posts_author FOREIGN KEY (author_id) REFERENCES users(id),
  CONSTRAINT fk_posts_topic FOREIGN KEY (topic_id) REFERENCES topics(id)
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  content VARCHAR(2000) NOT NULL,
  post_id BIGINT NOT NULL,
  author_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  CONSTRAINT fk_comments_post FOREIGN KEY (post_id) REFERENCES posts(id),
  CONSTRAINT fk_comments_author FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS subscriptions (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  topic_id BIGINT NOT NULL,
  created_at DATETIME NOT NULL,
  CONSTRAINT uk_subscription_user_topic UNIQUE (user_id, topic_id),
  CONSTRAINT fk_subscriptions_user FOREIGN KEY (user_id) REFERENCES users(id),
  CONSTRAINT fk_subscriptions_topic FOREIGN KEY (topic_id) REFERENCES topics(id)
);
