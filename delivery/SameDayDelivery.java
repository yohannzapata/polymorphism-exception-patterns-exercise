package delivery;

import model.Parcel;

public class SameDayDelivery implements DeliveryService {
    @Override
    public String getName() {
        return "Same Day";
    }

    @Override
    public double calculateFee(double baseFee) {
        return baseFee * 2 + 100;
    }

    /** Same Day only takes parcels with a chargeable weight of 10 kg or less. */
    @Override
    public boolean isAllowedFor(Parcel parcel) {
        return parcel.getChargeableWeight() <= 10;
    }
}
