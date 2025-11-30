-- Create categories table
CREATE TABLE categories (
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT
);

-- Create tasks table
CREATE TABLE tasks (
    id          UUID PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    status      VARCHAR(50)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL,
    updated_at  TIMESTAMP    NOT NULL,
    category_id UUID,
    CONSTRAINT fk_task_category
        FOREIGN KEY (category_id)
        REFERENCES categories(id)
);
