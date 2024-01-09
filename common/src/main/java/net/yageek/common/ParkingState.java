package net.yageek.common;

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
    public final String nom_parking;
    public final int libre;
    public final int total;

    public final int etat;

    public ParkingState(String nom_parking, int libre, int total, int etat) {
        this.nom_parking = nom_parking;
        this.libre = libre;
        this.total = total;
        this.etat = etat;
    }
}
