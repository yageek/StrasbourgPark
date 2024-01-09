package net.yageek.common;
import java.util.Comparator;


public class ParkingResult {
    public final Parking parking;
    public final ParkingState state;

    public ParkingResult(Parking parking, ParkingState state) {
        this.parking = parking;
        this.state = state;
    }

    int fillingRate() {
        return (int) ((double) state.libre / (double) state.total * 100.0);
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
                return t1.state.libre - parkingResult.state.libre;
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