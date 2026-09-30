CREATE TABLE password_reset_token (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_password_reset_user
        FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE,
    CONSTRAINT uq_password_reset_user UNIQUE (user_id),
    CONSTRAINT uq_password_reset_token_hash UNIQUE (token_hash)
);

CREATE INDEX idx_password_reset_expires_at ON password_reset_token (expires_at);
