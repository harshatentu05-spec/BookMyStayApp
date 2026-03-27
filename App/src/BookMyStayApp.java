import java.util.*;

// ---------------------- Reservation ----------------------
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

// ---------------------- Service ----------------------
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

// ---------------------- Reporting ----------------------
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
            roomCount.put(
                    r.getRoomType(),
                    roomCount.getOrDefault(r.getRoomType(), 0) + 1
            );
        }

        System.out.println("Room Type Distribution:");
        for (String room : roomCount.keySet()) {
            System.out.println(room + ": " + roomCount.get(room));
        }
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

        // Services
        Service wifi = new Service("WiFi", 10);
        Service breakfast = new Service("Breakfast", 20);
        Service parking = new Service("Parking", 15);
        Service spa = new Service("Spa", 50);

        while (true) {
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

                    System.out.print("Enter Room Type: ");
                    String room = sc.nextLine();

                    Reservation r = new Reservation(id, name, room);
                    reservations.put(id, r);

                    // Add to history
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
                            default: System.out.println("Invalid");
                        }
                    }
                    break;

                case 3:
                    System.out.print("Enter Reservation ID: ");
                    String vid = sc.nextLine();

                    Reservation res = reservations.get(vid);

                    if (res == null) {
                        System.out.println("Not found.");
                        break;
                    }

                    System.out.println(res);
                    serviceManager.displayServices(vid);
                    System.out.println("Total Add-On Cost: $" +
                            serviceManager.calculateTotalCost(vid));
                    break;

                case 4:
                    report.showAll(history.getAllReservations());
                    break;

                case 5:
                    report.summary(history.getAllReservations());
                    break;

                case 6:
                    System.out.println("Thank you!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}