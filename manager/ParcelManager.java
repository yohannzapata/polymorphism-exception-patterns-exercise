package manager;

import java.util.ArrayList;
import java.util.List;

import exception.DuplicateTrackingNumberException;
import exception.ParcelNotFoundException;
import model.Parcel;

/** Singleton: the one place where all parcels are stored. */
public class ParcelManager {
    private static ParcelManager instance;

    private final List<Parcel> parcels = new ArrayList<>();

    private ParcelManager() {
    }

    public static ParcelManager getInstance() {
        if (instance == null) {
            instance = new ParcelManager();
        }
        return instance;
    }

    public void checkDuplicate(String trackingNumber) throws DuplicateTrackingNumberException {
        for (Parcel parcel : parcels) {
            if (parcel.getTrackingNumber().equals(trackingNumber)) {
                throw new DuplicateTrackingNumberException("Tracking number already exists!");
            }
        }
    }

    public void addParcel(Parcel parcel) throws DuplicateTrackingNumberException {
        checkDuplicate(parcel.getTrackingNumber());
        parcels.add(parcel);
    }

    public Parcel findParcel(String trackingNumber) throws ParcelNotFoundException {
        for (Parcel parcel : parcels) {
            if (parcel.getTrackingNumber().equals(trackingNumber)) {
                return parcel;
            }
        }
        throw new ParcelNotFoundException("Parcel not found!");
    }

    public List<Parcel> getAllParcels() {
        return parcels;
    }
}
