export interface PostAuthor {
  id: number;
  username: string;
}

export interface PostTopic {
  id: number;
  name: string;
}

export interface FeedPost {
  id: number;
  title: string;
  content: string;
  author: PostAuthor;
  topic: PostTopic;
  createdAt: string;
}

export interface PostComment {
  id: number;
  content: string;
  author: PostAuthor;
  createdAt: string;
}

export interface PostDetail {
  id: number;
  title: string;
  content: string;
  author: PostAuthor;
  topic: PostTopic;
  createdAt: string;
  comments: PostComment[];
}

export interface CreatePostPayload {
  topicId: number;
  title: string;
  content: string;
}

export interface CreateCommentPayload {
  content: string;
}
