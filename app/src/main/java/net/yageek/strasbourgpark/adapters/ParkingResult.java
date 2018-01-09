package net.yageek.strasbourgpark.adapters;

import net.yageek.strasbourgparkapi.Parking;
import net.yageek.strasbourgparkapi.ParkingState;

import java.util.Comparator;

/**
 * Created by yheinrich on 09.01.18.
 */

public class ParkingResult {
    Parking parking;
    ParkingState state;

    ParkingResult(Parking parking, ParkingState state) {
        this.parking = parking;
        this.state = state;
    }

    int fillingRate() {
        return (int) ((double) state.free / (double) state.total * 100.0);
    }

    public enum Comparators {
        ByName,
        ByFreePlaces,
        ByFillingRate;

        Comparator<ParkingResult> nameComparator = new Comparator<ParkingResult>() {
            @Override
            public int compare(ParkingResult parkingResult, ParkingResult t1) {
                return parkingResult.parking.name.compareTo(t1.parking.name);
            }
        };

        Comparator<ParkingResult> freePlaces = new Comparator<ParkingResult>() {
            @Override
            public int compare(ParkingResult parkingResult, ParkingResult t1) {
                return t1.state.free - parkingResult.state.free;
            }
        };

        Comparator<ParkingResult> fillingRate = new Comparator<ParkingResult>() {
            @Override
            public int compare(ParkingResult parkingResult, ParkingResult t1) {
                return t1.fillingRate() - parkingResult.fillingRate();
            }
        };

        public Comparator<ParkingResult> getComparator() {
            switch (this) {
                case ByFillingRate: return fillingRate;
                case ByFreePlaces: return freePlaces;
                default: return nameComparator;
            }

        }
    }
}
