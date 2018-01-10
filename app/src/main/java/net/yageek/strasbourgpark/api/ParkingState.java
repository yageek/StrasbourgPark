package net.yageek.strasbourgpark.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Represents the state of the parking
 */
public class ParkingState {
    public final String parkingIdentifier;
    public final int free;
    public final int total;
    public final Status status;

    ParkingState(String parkingIdentifier, int free, int total, Status status) {
        this.parkingIdentifier = parkingIdentifier;
        this.free = free;
        this.total = total;
        this.status = status;
    }

    /**
     * The status of the parking
     */
    public enum Status {
        Open,
        Full,
        NotAvailable,
        Closed;

        static Status fromIdentifier(String id) {
            switch (id) {
                case "status_1": return Open;
                case "status_2": return Full;
                case "status_3": return NotAvailable;
                case "status_4": return Closed;
                default:
                    return NotAvailable;
            }
        }
    }

    static class JsonAdapter implements JsonDeserializer<ParkingState> {
        @Override
        public ParkingState deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject remote = json.getAsJsonObject();
            String identifier = remote.get("id").getAsString();
            int free = remote.get("df").getAsInt();
            int total = remote.get("dt").getAsInt();
            Status status = Status.fromIdentifier(remote.get("ds").getAsString());

            return new ParkingState(identifier, free, total, status);
        }
    }
}
