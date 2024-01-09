package net.yageek.common;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the response of the API
 */
public class ParkingStateResponse {
    public final List<ParkingState> results;

    public ParkingStateResponse(List<ParkingState> results) {
        this.results = results;
    }
}