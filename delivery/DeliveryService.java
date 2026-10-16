package delivery;

import model.Parcel;

/** Strategy: each delivery service calculates its own fee from the parcel's base fee. */
public interface DeliveryService {
    String getName();

    double calculateFee(double baseFee);

    default boolean isAllowedFor(Parcel parcel) {
        return true;
    }
}
