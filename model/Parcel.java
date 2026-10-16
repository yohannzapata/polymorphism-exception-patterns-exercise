package model;

import delivery.DeliveryService;
import exception.ParcelAlreadyDeliveredException;
import exception.SameDayNotAllowedException;

/** Base class of all parcels. Each parcel type overrides how its weight and base fee are computed. */
public abstract class Parcel {
    private final String trackingNumber;
    private final String receiverName;
    private final double weight;
    private DeliveryService deliveryService;
    private boolean delivered;

    public Parcel(String trackingNumber, String receiverName, double weight) {
        this.trackingNumber = trackingNumber;
        this.receiverName = receiverName;
        this.weight = weight;
    }

    public abstract String getType();

    public abstract double getChargeableWeight();

    public abstract double getBaseFee();

    public double getDeliveryFee() {
        return deliveryService.calculateFee(getBaseFee());
    }

    public void setDeliveryService(DeliveryService service) throws SameDayNotAllowedException {
        if (!service.isAllowedFor(this)) {
            throw new SameDayNotAllowedException("Same Day delivery is only for parcels up to 10 kg!");
        }
        this.deliveryService = service;
    }

    public void markAsDelivered() throws ParcelAlreadyDeliveredException {
        if (delivered) {
            throw new ParcelAlreadyDeliveredException("Parcel was already delivered.");
        }
        delivered = true;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public String getStatus() {
        return delivered ? "Delivered" : "Pending";
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public double getWeight() {
        return weight;
    }

    public DeliveryService getDeliveryService() {
        return deliveryService;
    }
}
