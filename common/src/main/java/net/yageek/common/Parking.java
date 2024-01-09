package net.yageek.common;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Parking holds geographical and price information
 * about a parking station.
 */
public class Parking {
    /**
     * The parking identifier
     */
    public final String identifier;

    /**
     * The latitude of the parking
     */
    public final double lat;

    /**
     * The longitude of the parking
     */
    public final double lon;

    /**
     * The name of the parking
     */
    public final String name;

    /**
     * The company managing the parking
     */
    public final Manager manager;

    /**
     * The price's description in french
     */
    public final String price_fr;

    /**
     * The price's description in english
     */
    public final String price_en;

    /**
     * The price's description in german
     */
    public final String price_de;

    Parking(String identifier, double lat, double lon, String name, Manager manager, String price_fr, String price_en, String price_de) {
        this.identifier = identifier;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.manager = manager;
        this.price_fr = price_fr;
        this.price_en = price_en;
        this.price_de = price_de;
    }

    static class JsonAdapter implements JsonDeserializer<Parking> {
        @Override
        public Parking deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            JsonObject remote = json.getAsJsonObject();

            String identifier = remote.get("id").getAsString();

            String parkingRaw = remote.get("gest_id").getAsString();
            Manager manager = Manager.fromIdentifier(parkingRaw);

            JsonObject coord = remote.get("go").getAsJsonObject();
            double lat = coord.get("y").getAsDouble();
            double lon = coord.get("x").getAsDouble();

            String name = remote.get("ln").getAsString();
            String price_de = remote.get("price_de").getAsString();
            String price_fr = remote.get("price_fr").getAsString();
            String price_en = remote.get("price_en").getAsString();

            return new Parking(identifier,
                    lat,
                    lon,
                    name,
                    manager, price_fr, price_en, price_de);
        }
    }

    /**
     * Represents a company managing the parking
     */
    public enum Manager {
        Parcus,
        Vinci,
        Cus,
        Unknown;

        static Manager fromIdentifier(String identifier) {
            switch(identifier) {
                case "parcus":
                    return Parcus;
                case "vinci":
                    return Vinci;
                case "cus":
                    return Cus;
                default:
                    return Unknown;
            }
        }
    }
}
