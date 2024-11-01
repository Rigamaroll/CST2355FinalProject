 Create Table ImageEntry (
    _id INTEGER PRIMARY KEY,
    title TEXT NOT NULL,
    date TEXT NOT NULL,
    explanation TEXT NOT NULL,
    mediaType TEXT NOT NULL,
    url TEXT,
    hdUrl TEXT,
    thumbnailUrl TEXT,
    copyright TEXT,
    imageFile BLOB);