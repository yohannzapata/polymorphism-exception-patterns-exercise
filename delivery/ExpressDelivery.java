package delivery;

public class ExpressDelivery implements DeliveryService {
    @Override
    public String getName() {
        return "Express";
    }

    @Override
    public double calculateFee(double baseFee) {
        return baseFee * 1.5;
    }
}
