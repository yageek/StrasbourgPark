package net.yageek.strasbourgparkapi;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Represents the response of the API
 */
public class ParkingStateResponse {
    public final List<ParkingState> states;
    public final String dataTime;

    ParkingStateResponse(List<ParkingState> states, String dataTime) {
        this.states = states;
        this.dataTime = dataTime;
    }

    static class JsonAdapter implements JsonDeserializer<ParkingStateResponse> {
        @Override
        public ParkingStateResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject remote = json.getAsJsonObject();

            String dataTime = remote.get("datatime").getAsString();

            Type tokenType = new TypeToken<List<ParkingState>>(){}.getType();
            List<ParkingState> states = context.deserialize(remote.getAsJsonArray("s"), tokenType);

            return new ParkingStateResponse(states, dataTime);
        }
    }
}