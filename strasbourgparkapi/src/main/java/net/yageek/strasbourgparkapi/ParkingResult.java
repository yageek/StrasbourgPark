package net.yageek.strasbourgparkapi;
import java.util.Comparator;


public class ParkingResult {
    public final String lastRefresh;
    public final Parking parking;
    public final ParkingState state;

    public ParkingResult(Parking parking, ParkingState state, String lastRefresh) {
        this.parking = parking;
        this.state = state;
        this.lastRefresh = lastRefresh;
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