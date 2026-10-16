package model;

public class Box extends Parcel {
    public static final double MAX_WEIGHT = 50;

    private final int length;
    private final int width;
    private final int height;

    public Box(String trackingNumber, String receiverName, double weight, int length, int width, int height) {
        super(trackingNumber, receiverName, weight);
        this.length = length;
        this.width = width;
        this.height = height;
    }

    public double getVolumetricWeight() {
        return (double) length * width * height / 3500;
    }

    @Override
    public String getType() {
        return "Box";
    }

    /** The bigger of actual and volumetric weight, rounded up to the next whole kg. */
    @Override
    public double getChargeableWeight() {
        return Math.ceil(Math.max(getWeight(), getVolumetricWeight()));
    }

    @Override
    public double getBaseFee() {
        return 40 * getChargeableWeight();
    }
}
