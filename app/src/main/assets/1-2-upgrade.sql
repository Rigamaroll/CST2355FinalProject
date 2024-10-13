 Create Table ImageEntry (
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    date TEXT NOT NULL,
    explanation TEXT NOT NULL,
    mediaType TEXT NOT NULL,
    url TEXT,
    hdUrl TEXT,
    copyright TEXT,
    imageFile BLOB);