CREATE TABLE video_file(
  ID BIGSERIAL PRIMARY KEY,
  subtitles_id BIGINT NOT NULL,
  file_name VARCHAR(50),
  file_path VARCHAR(100),
  file_type INT NOT NULL
);

ALTER TABLE video_file ADD FOREIGN KEY (subtitles_id) REFERENCES subtitles(ID);