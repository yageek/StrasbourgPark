package net.yageek.strasbourgparkcommon;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Represents the response from the API
 */
public class ParkingLocationResponse {
    public final List<Parking> parkings;

    ParkingLocationResponse(List<Parking> parkings) {
        this.parkings = parkings;
    }

    protected static class JsonAdapter implements JsonDeserializer<ParkingLocationResponse> {
        @Override
        public ParkingLocationResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject remote = json.getAsJsonObject();
            JsonArray array = remote.getAsJsonArray("s");

            Type tokenType = new TypeToken<List<Parking>>(){}.getType();
            List<Parking> parkings = context.deserialize(array, tokenType);
            return new ParkingLocationResponse(parkings);
        }
    }
}