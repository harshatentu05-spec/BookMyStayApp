import java.util.*;

// ---------------------- Reservation Class ----------------------
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId +
                ", Guest: " + guestName +
                ", Room Type: " + roomType;
    }
}

// ---------------------- Service Class ----------------------
class Service {
    private String name;
    private double cost;

    public Service(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return name + " ($" + cost + ")";
    }
}

// ---------------------- Add-On Service Manager ----------------------
class AddOnServiceManager {

    private Map<String, List<Service>> serviceMap = new HashMap<>();

    public void addService(String reservationId, Service service) {
        serviceMap
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    public List<Service> getServices(String reservationId) {
        return serviceMap.getOrDefault(reservationId, new ArrayList<>());
    }

    public double calculateTotalCost(String reservationId) {
        double total = 0;
        for (Service s : getServices(reservationId)) {
            total += s.getCost();
        }
        return total;
    }

    public void displayServices(String reservationId) {
        List<Service> services = getServices(reservationId);

        if (services.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        System.out.println("\nAdd-On Services:");
        for (Service s : services) {
            System.out.println("- " + s);
        }
    }
}

// ---------------------- Main App ----------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Map<String, Reservation> reservations = new HashMap<>();
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // Sample services
        Service wifi = new Service("WiFi", 10);
        Service breakfast = new Service("Breakfast", 20);
        Service parking = new Service("Parking", 15);
        Service spa = new Service("Spa", 50);

        System.out.println("===== Welcome to Book My Stay App =====");

        // Create Reservation
        System.out.print("Enter Reservation ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter Guest Name: ");
        String name = scanner.nextLine();

        System.out.print("Enter Room Type: ");
        String room = scanner.nextLine();

        Reservation reservation = new Reservation(id, name, room);
        reservations.put(id, reservation);

        System.out.println("\nReservation Created Successfully!");
        System.out.println(reservation);

        // Add-On Service Selection
        while (true) {
            System.out.println("\nSelect Add-On Services:");
            System.out.println("1. WiFi ($10)");
            System.out.println("2. Breakfast ($20)");
            System.out.println("3. Parking ($15)");
            System.out.println("4. Spa ($50)");
            System.out.println("5. Finish");

            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    serviceManager.addService(id, wifi);
                    System.out.println("WiFi added.");
                    break;
                case 2:
                    serviceManager.addService(id, breakfast);
                    System.out.println("Breakfast added.");
                    break;
                case 3:
                    serviceManager.addService(id, parking);
                    System.out.println("Parking added.");
                    break;
                case 4:
                    serviceManager.addService(id, spa);
                    System.out.println("Spa added.");
                    break;
                case 5:
                    System.out.println("Service selection completed.");
                    break;
                default:
                    System.out.println("Invalid choice.");
                    continue;
            }

            if (choice == 5) break;
        }

        // Display Final Details
        System.out.println("\n===== Final Booking Summary =====");
        System.out.println(reservations.get(id));

        serviceManager.displayServices(id);

        double totalCost = serviceManager.calculateTotalCost(id);
        System.out.println("Total Add-On Cost: $" + totalCost);

        scanner.close();
    }
}