CREATE TABLE IF NOT EXISTS flyway_migration_marker (
  id BIGINT PRIMARY KEY,
  description VARCHAR(255) NOT NULL
);

