CREATE TABLE subtitles(
  ID BIGSERIAL PRIMARY KEY,
  width INT,
  height INT,
  bottom_margin INT,
  font_name VARCHAR(50),
  text_chunks JSONB,
  status INT NOT NULL,
  error_message TEXT,
  creation_date TIMESTAMP
);