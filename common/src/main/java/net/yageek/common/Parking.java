package net.yageek.common;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import net.yageek.common.utils.Position;

import java.lang.reflect.Type;

/**
 * Parking holds geographical and price information
 * about a parking station.
 */

public class Parking {

    /**
     * The latitude of the parking
     */
    public final Position position;
    /**
     * The name of the parking
     */
    public final String name;


    public Parking(Position position, String name) {
        this.position = position;
        this.name = name;
    }
}
