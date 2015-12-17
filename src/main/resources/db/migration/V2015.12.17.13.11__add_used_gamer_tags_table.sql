CREATE TABLE used_gamer_tags (
  gamer_id BIGINT REFERENCES gamer(id),
  tag_id VARCHAR(255) REFERENCES tag(id),

  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT now() NOT NULL,

  PRIMARY KEY(gamer_id, tag_id)
);

CREATE TRIGGER update_user_updated_at BEFORE UPDATE ON used_gamer_tags
FOR EACH ROW EXECUTE PROCEDURE upd_datetime();