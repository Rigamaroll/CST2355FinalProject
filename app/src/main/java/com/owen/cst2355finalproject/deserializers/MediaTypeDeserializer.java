package com.owen.cst2355finalproject.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.owen.cst2355finalproject.enums.MediaType;

import java.lang.reflect.Type;

public class MediaTypeDeserializer implements JsonDeserializer<MediaType> {
    @Override
    public MediaType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return MediaType.valueOf(json.getAsString().toUpperCase());
    }
}
