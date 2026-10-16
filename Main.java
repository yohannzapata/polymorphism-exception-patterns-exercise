import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import delivery.DeliveryService;
import delivery.ExpressDelivery;
import delivery.SameDayDelivery;
import delivery.StandardDelivery;
import exception.DuplicateTrackingNumberException;
import exception.ParcelAlreadyDeliveredException;
import exception.ParcelNotFoundException;
import exception.SameDayNotAllowedException;
import manager.ParcelManager;
import model.Box;
import model.Document;
import model.Fragile;
import model.Parcel;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ParcelManager manager = ParcelManager.getInstance();

    public static void main(String[] args) {
        try {
            runMenu();
        } catch (NoSuchElementException e) {
            // Input was closed (Ctrl+Z / Ctrl+D), so there is nothing left to read.
            System.out.println();
        }
        System.out.println("Thank you for using the Parcel Delivery System. Goodbye!");
    }

    private static void runMenu() {
        while (true) {
            System.out.println();
            System.out.println("===== PARCEL DELIVERY SYSTEM =====");
            System.out.println("1. Add Parcel");
            System.out.println("2. View All Parcels");
            System.out.println("3. Change Delivery Service");
            System.out.println("4. Mark as Delivered");
            System.out.println("5. Exit");
            int choice = readInt("Enter choice: ", 1, 5, "Invalid choice! Enter a number from 1 to 5.");

            switch (choice) {
                case 1:
                    addParcel();
                    break;
                case 2:
                    viewAllParcels();
                    break;
                case 3:
                    changeDeliveryService();
                    break;
                case 4:
                    markAsDelivered();
                    break;
                default:
                    return;
            }
        }
    }

    private static void addParcel() {
        System.out.println("Parcel type: 1. Document  2. Box  3. Fragile");
        int type = readInt("Select parcel type: ", 1, 3, "Invalid choice! Enter a number from 1 to 3.");
        String trackingNumber = readNewTrackingNumber();
        String receiverName = readReceiverName();

        Parcel parcel;
        if (type == 1) {
            double weight = readWeight("Document", Document.MAX_WEIGHT);
            int pages = readInt("Number of Pages (1-100): ", 1, 100,
                    "Invalid number of pages! Enter a whole number from 1 to 100.");
            parcel = new Document(trackingNumber, receiverName, weight, pages);
        } else if (type == 2) {
            double weight = readWeight("Box", Box.MAX_WEIGHT);
            int length = readDimension("Length");
            int width = readDimension("Width");
            int height = readDimension("Height");
            parcel = new Box(trackingNumber, receiverName, weight, length, width, height);
        } else {
            double weight = readWeight("Fragile", Fragile.MAX_WEIGHT);
            int length = readDimension("Length");
            int width = readDimension("Width");
            int height = readDimension("Height");
            String note = readHandlingNote();
            parcel = new Fragile(trackingNumber, receiverName, weight, length, width, height, note);
        }

        // Keep asking until the parcel accepts the service (Same Day is refused over 10 kg).
        while (true) {
            try {
                parcel.setDeliveryService(readDeliveryService());
                break;
            } catch (SameDayNotAllowedException e) {
                System.out.println(e.getMessage());
            }
        }

        try {
            manager.addParcel(parcel);
            System.out.printf("Parcel added successfully! Delivery fee: Php %.2f%n", parcel.getDeliveryFee());
        } catch (DuplicateTrackingNumberException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewAllParcels() {
        List<Parcel> parcels = manager.getAllParcels();
        if (parcels.isEmpty()) {
            System.out.println("No parcels found.");
            return;
        }
        String format = "%-12s %-10s %-20s %-12s %-10s %-14s %-10s%n";
        System.out.printf(format, "Tracking No", "Type", "Receiver", "Chg. Weight", "Service", "Delivery Fee", "Status");
        System.out.println("----------------------------------------------------------------------------------------------");
        for (Parcel parcel : parcels) {
            System.out.printf(format,
                    parcel.getTrackingNumber(),
                    parcel.getType(),
                    shorten(parcel.getReceiverName(), 20),
                    kg(parcel.getChargeableWeight()) + " kg",
                    parcel.getDeliveryService().getName(),
                    String.format("Php %.2f", parcel.getDeliveryFee()),
                    parcel.getStatus());
        }
        System.out.println("Total parcels: " + parcels.size());
    }

    private static void changeDeliveryService() {
        String trackingNumber = readTrackingNumber("Enter Tracking Number: ");
        try {
            Parcel parcel = manager.findParcel(trackingNumber);
            if (parcel.isDelivered()) {
                System.out.println("Parcel already delivered!");
                return;
            }
            DeliveryService current = parcel.getDeliveryService();
            System.out.printf("Current service: %s (Php %.2f)%n", current.getName(), parcel.getDeliveryFee());

            DeliveryService newService = readDeliveryService();
            if (newService.getName().equals(current.getName())) {
                System.out.println("Parcel already uses " + current.getName() + " delivery. No changes made.");
                return;
            }
            parcel.setDeliveryService(newService);
            System.out.printf("Delivery service changed! New fee: Php %.2f%n", parcel.getDeliveryFee());
        } catch (ParcelNotFoundException | SameDayNotAllowedException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void markAsDelivered() {
        String trackingNumber = readTrackingNumber("Enter Tracking Number: ");
        try {
            manager.findParcel(trackingNumber).markAsDelivered();
            System.out.println("Parcel delivered successfully!");
        } catch (ParcelNotFoundException | ParcelAlreadyDeliveredException e) {
            System.out.println(e.getMessage());
        }
    }

    // ----- Input helpers: each one keeps asking until the value is valid -----

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /** Whole numbers only (digits, no signs or decimals) within min..max. */
    private static int readInt(String prompt, int min, int max, String errorMessage) {
        while (true) {
            String input = readLine(prompt);
            try {
                if (input.matches("\\d+")) {
                    int value = Integer.parseInt(input);
                    if (value >= min && value <= max) {
                        return value;
                    }
                }
            } catch (NumberFormatException e) {
                // too many digits to be an int: fall through to the error message
            }
            System.out.println(errorMessage);
        }
    }

    private static int readDimension(String name) {
        return readInt(name + " (cm): ", 1, Integer.MAX_VALUE,
                "Invalid " + name.toLowerCase() + "! Enter a whole number of cm greater than 0.");
    }

    private static double readWeight(String type, double maxWeight) {
        while (true) {
            String input = readLine("Weight in kg (max " + kg(maxWeight) + "): ");
            if (!input.matches("\\d+(\\.\\d+)?")) {
                System.out.println("Invalid weight! Enter a number in kg, e.g. 1.5");
            } else if (!input.matches("\\d+(\\.\\d{1,3})?")) {
                System.out.println("Invalid weight! Use at most 3 decimal places.");
            } else {
                double weight = Double.parseDouble(input);
                if (weight <= 0) {
                    System.out.println("Invalid weight! It must be greater than 0 kg.");
                } else if (weight > maxWeight) {
                    System.out.println("Invalid weight! A " + type + " parcel cannot exceed " + kg(maxWeight) + " kg.");
                } else {
                    return weight;
                }
            }
        }
    }

    private static String readTrackingNumber(String prompt) {
        while (true) {
            String trackingNumber = readLine(prompt);
            if (trackingNumber.matches("ABC\\d{4}")) {
                return trackingNumber;
            }
            System.out.println("Invalid tracking number! Use ABC followed by 4 digits (e.g. ABC0001).");
        }
    }

    private static String readNewTrackingNumber() {
        while (true) {
            String trackingNumber = readTrackingNumber("Tracking Number (e.g. ABC0001): ");
            try {
                manager.checkDuplicate(trackingNumber);
                return trackingNumber;
            } catch (DuplicateTrackingNumberException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static String readReceiverName() {
        while (true) {
            String name = readLine("Receiver Name: ").replaceAll("\\s+", " ");
            if (name.isEmpty()) {
                System.out.println("Invalid receiver name! It cannot be empty.");
            } else if (name.length() < 2 || name.length() > 50) {
                System.out.println("Invalid receiver name! It must be 2 to 50 characters long.");
            } else if (!name.matches("\\p{L}[\\p{L} .'-]*")) {
                System.out.println("Invalid receiver name! Use letters only (spaces, . ' - allowed), e.g. Juan Dela Cruz.");
            } else {
                return name;
            }
        }
    }

    private static String readHandlingNote() {
        while (true) {
            String note = readLine("Handling Note: ");
            if (note.isEmpty()) {
                System.out.println("Invalid handling note! It cannot be empty.");
            } else if (note.length() > 100) {
                System.out.println("Invalid handling note! Keep it to 100 characters or less.");
            } else if (!note.matches(".*\\p{L}.*")) {
                System.out.println("Invalid handling note! Describe the handling, e.g. Handle with care.");
            } else {
                return note;
            }
        }
    }

    private static DeliveryService readDeliveryService() {
        System.out.println("Delivery service: 1. Standard  2. Express  3. Same Day");
        int choice = readInt("Select delivery service: ", 1, 3, "Invalid choice! Enter a number from 1 to 3.");
        if (choice == 1) {
            return new StandardDelivery();
        } else if (choice == 2) {
            return new ExpressDelivery();
        }
        return new SameDayDelivery();
    }

    /** Shows 0.5 as "0.5" and 50.0 as "50". */
    private static String kg(double weight) {
        return weight == Math.floor(weight) ? String.valueOf((long) weight) : String.valueOf(weight);
    }

    private static String shorten(String text, int maxLength) {
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 3) + "...";
    }
}
