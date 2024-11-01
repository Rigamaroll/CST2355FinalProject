ALTER TABLE ImageEntry
    ADD COLUMN thumbnailUrl TEXT;

UPDATE ImageEntry
    SET mediaType = "IMAGE" WHERE mediaType IS NULL OR mediaType = "image";