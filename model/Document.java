package model;

public class Document extends Parcel {
    public static final double MAX_WEIGHT = 0.5;

    private final int numberOfPages;

    public Document(String trackingNumber, String receiverName, double weight, int numberOfPages) {
        super(trackingNumber, receiverName, weight);
        this.numberOfPages = numberOfPages;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    @Override
    public String getType() {
        return "Document";
    }

    @Override
    public double getChargeableWeight() {
        return getWeight();
    }

    @Override
    public double getBaseFee() {
        return 50;
    }
}
