UPDATE ImageEntry
    SET mediaType = "IMAGE" WHERE mediaType IS NULL OR mediaType = "image";