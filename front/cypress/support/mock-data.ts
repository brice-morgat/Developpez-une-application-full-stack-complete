export const authenticatedUser = {
  id: 1,
  email: 'alice@mdd.dev',
  username: 'alice',
};

export const topicsResponse = [
  {
    id: 1,
    name: 'Java',
    description: 'Backend Spring Boot et architecture API.',
    subscribed: false,
  },
  {
    id: 2,
    name: 'Angular',
    description: 'Composants, RxJS et architecture front.',
    subscribed: true,
  },
];

export const descendingFeed = [
  {
    id: 20,
    title: 'Angular Signals pour un écran de feed',
    content: 'Une approche simple pour piloter le chargement et le tri.',
    author: { id: 2, username: 'bob' },
    topic: { id: 2, name: 'Angular' },
    createdAt: '2026-03-31T09:00:00Z',
  },
  {
    id: 10,
    title: 'Spring Boot 3 et JWT',
    content: 'Un guide de base pour sécuriser une API.',
    author: { id: 1, username: 'alice' },
    topic: { id: 1, name: 'Java' },
    createdAt: '2026-03-30T09:00:00Z',
  },
];

export const ascendingFeed = [...descendingFeed].reverse();

export const postDetailResponse = {
  id: 10,
  title: 'Spring Boot 3 et JWT',
  content: 'Un guide de base pour sécuriser une API.',
  author: { id: 1, username: 'alice' },
  topic: { id: 1, name: 'Java' },
  createdAt: '2026-03-30T09:00:00Z',
  comments: [
    {
      id: 101,
      content: 'Très clair, merci.',
      author: { id: 2, username: 'bob' },
      createdAt: '2026-03-30T10:00:00Z',
    },
  ],
};
