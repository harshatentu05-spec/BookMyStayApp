import java.util.*;

// ---------------------- Custom Exception ----------------------
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// ---------------------- Reservation ----------------------
class Reservation {
    private String id;
    private String name;
    private String roomType;

    public Reservation(String id, String name, String roomType) {
        this.id = id;
        this.name = name;
        this.roomType = roomType;
    }

    public String getId() {
        return id;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + id + ", Name: " + name + ", Room: " + roomType;
    }
}

// ---------------------- Add-On Service ----------------------
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
        serviceMap.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
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
        System.out.println("Add-On Services:");
        for (Service s : services) {
            System.out.println("- " + s);
        }
    }
}

// ---------------------- Booking History ----------------------
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    public void addReservation(Reservation reservation) {
        history.add(reservation);
    }

    public List<Reservation> getAllReservations() {
        return history;
    }
}

// ---------------------- Booking Report Service ----------------------
class BookingReportService {
    public void showAll(List<Reservation> list) {
        if (list.isEmpty()) {
            System.out.println("No bookings found.");
            return;
        }
        System.out.println("\n===== Booking History =====");
        for (Reservation r : list) {
            System.out.println(r);
        }
    }

    public void summary(List<Reservation> list) {
        System.out.println("\n===== Summary Report =====");
        System.out.println("Total Bookings: " + list.size());
        Map<String, Integer> roomCount = new HashMap<>();
        for (Reservation r : list) {
            roomCount.put(r.getRoomType(), roomCount.getOrDefault(r.getRoomType(), 0) + 1);
        }
        System.out.println("Room Type Distribution:");
        for (String room : roomCount.keySet()) {
            System.out.println(room + ": " + roomCount.get(room));
        }
    }
}

// ---------------------- Validator ----------------------
class InvalidBookingValidator {
    private static final List<String> VALID_ROOMS = Arrays.asList("Single", "Double", "Suite");

    public static void validate(String id, String name, String roomType, Map<String, Integer> inventory)
            throws InvalidBookingException {

        if (id == null || id.isEmpty())
            throw new InvalidBookingException("Reservation ID cannot be empty.");

        if (name == null || name.isEmpty())
            throw new InvalidBookingException("Guest name cannot be empty.");

        if (!VALID_ROOMS.contains(roomType))
            throw new InvalidBookingException("Invalid room type selected.");

        if (!inventory.containsKey(roomType))
            throw new InvalidBookingException("Room type not available.");

        if (inventory.get(roomType) <= 0)
            throw new InvalidBookingException("No rooms available for " + roomType);
    }
}

// ---------------------- Main App ----------------------
public class BookMyStayApp {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Map<String, Reservation> reservations = new HashMap<>();
        AddOnServiceManager serviceManager = new AddOnServiceManager();
        BookingHistory history = new BookingHistory();
        BookingReportService report = new BookingReportService();

        // Inventory
        Map<String, Integer> inventory = new HashMap<>();
        inventory.put("Single", 2);
        inventory.put("Double", 2);
        inventory.put("Suite", 1);

        // Services
        Service wifi = new Service("WiFi", 10);
        Service breakfast = new Service("Breakfast", 20);
        Service parking = new Service("Parking", 15);
        Service spa = new Service("Spa", 50);

        while (true) {
            try {
                System.out.println("\n===== Book My Stay App =====");
                System.out.println("1. Create Booking");
                System.out.println("2. Add Services");
                System.out.println("3. View Booking");
                System.out.println("4. View History");
                System.out.println("5. Generate Report");
                System.out.println("6. Exit");

                System.out.print("Enter choice: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Enter ID: ");
                        String id = sc.nextLine();
                        System.out.print("Enter Name: ");
                        String name = sc.nextLine();
                        System.out.print("Enter Room Type (Single/Double/Suite): ");
                        String room = sc.nextLine();

                        // Validate input
                        InvalidBookingValidator.validate(id, name, room, inventory);

                        // Deduct inventory
                        inventory.put(room, inventory.get(room) - 1);

                        Reservation r = new Reservation(id, name, room);
                        reservations.put(id, r);
                        history.addReservation(r);

                        System.out.println("Booking Confirmed!");
                        break;

                    case 2:
                        System.out.print("Enter Reservation ID: ");
                        String rid = sc.nextLine();

                        if (!reservations.containsKey(rid)) {
                            System.out.println("Invalid Reservation ID");
                            break;
                        }

                        while (true) {
                            System.out.println("1.WiFi  2.Breakfast  3.Parking  4.Spa  5.Done");
                            int c = sc.nextInt();
                            if (c == 5) break;
                            switch (c) {
                                case 1: serviceManager.addService(rid, wifi); break;
                                case 2: serviceManager.addService(rid, breakfast); break;
                                case 3: serviceManager.addService(rid, parking); break;
                                case 4: serviceManager.addService(rid, spa); break;
                                default: System.out.println("Invalid choice.");
                            }
                        }
                        sc.nextLine(); // clear buffer
                        break;

                    case 3:
                        System.out.print("Enter Reservation ID: ");
                        String vid = sc.nextLine();
                        Reservation res = reservations.get(vid);
                        if (res == null) {
                            System.out.println("Reservation not found.");
                            break;
                        }
                        System.out.println(res);
                        serviceManager.displayServices(vid);
                        System.out.println("Total Add-On Cost: $" + serviceManager.calculateTotalCost(vid));
                        break;

                    case 4:
                        report.showAll(history.getAllReservations());
                        break;

                    case 5:
                        report.summary(history.getAllReservations());
                        break;

                    case 6:
                        System.out.println("Thank you for using Book My Stay!");
                        sc.close();
                        return;

                    default:
                        System.out.println("Invalid menu choice.");
                }

            } catch (InvalidBookingException e) {
                System.out.println("❌ Booking Failed: " + e.getMessage());

            } catch (Exception e) {
                System.out.println("⚠ Unexpected error: " + e.getMessage());
                sc.nextLine(); // clear buffer
            }
        }
    }
}