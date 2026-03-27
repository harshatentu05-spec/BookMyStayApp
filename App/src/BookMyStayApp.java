import java.util.*;
import java.util.concurrent.*;

// ---------------------- Custom Exception ----------------------
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) { super(message); }
}

// ---------------------- Reservation ----------------------
class Reservation {
    private String id, name, roomType;
    public Reservation(String id, String name, String roomType) {
        this.id = id; this.name = name; this.roomType = roomType;
    }
    public String getId() { return id; }
    public String getRoomType() { return roomType; }
    @Override
    public String toString() { return "Reservation ID: " + id + ", Name: " + name + ", Room: " + roomType; }
}

// ---------------------- Add-On Service ----------------------
class Service {
    private String name; private double cost;
    public Service(String name, double cost) { this.name = name; this.cost = cost; }
    public double getCost() { return cost; }
    @Override public String toString() { return name + " ($" + cost + ")"; }
}

// ---------------------- Add-On Service Manager ----------------------
class AddOnServiceManager {
    private Map<String, List<Service>> serviceMap = new ConcurrentHashMap<>();
    public void addService(String reservationId, Service service) {
        serviceMap.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
    }
    public List<Service> getServices(String reservationId) { return serviceMap.getOrDefault(reservationId, new ArrayList<>()); }
    public double calculateTotalCost(String reservationId) {
        double total = 0; for (Service s : getServices(reservationId)) total += s.getCost(); return total;
    }
    public void displayServices(String reservationId) {
        List<Service> services = getServices(reservationId);
        if (services.isEmpty()) { System.out.println("No add-on services selected."); return; }
        System.out.println("Add-On Services:");
        for (Service s : services) System.out.println("- " + s);
    }
    public void removeServices(String reservationId) { serviceMap.remove(reservationId); }
}

// ---------------------- Booking History ----------------------
class BookingHistory {
    private List<Reservation> history = Collections.synchronizedList(new ArrayList<>());
    public void addReservation(Reservation reservation) { history.add(reservation); }
    public void removeReservation(Reservation reservation) { history.remove(reservation); }
    public List<Reservation> getAllReservations() { return history; }
}

// ---------------------- Booking Report Service ----------------------
class BookingReportService {
    public void showAll(List<Reservation> list) {
        if (list.isEmpty()) { System.out.println("No bookings found."); return; }
        System.out.println("\n===== Booking History =====");
        for (Reservation r : list) System.out.println(r);
    }
    public void summary(List<Reservation> list) {
        System.out.println("\n===== Summary Report =====");
        System.out.println("Total Bookings: " + list.size());
        Map<String, Integer> roomCount = new HashMap<>();
        for (Reservation r : list) roomCount.put(r.getRoomType(), roomCount.getOrDefault(r.getRoomType(), 0) + 1);
        System.out.println("Room Type Distribution:");
        for (String room : roomCount.keySet()) System.out.println(room + ": " + roomCount.get(room));
    }
}

// ---------------------- Validator ----------------------
class InvalidBookingValidator {
    private static final List<String> VALID_ROOMS = Arrays.asList("Single", "Double", "Suite");
    public static void validate(String id, String name, String roomType, Map<String, Integer> inventory)
            throws InvalidBookingException {
        if (id == null || id.isEmpty()) throw new InvalidBookingException("Reservation ID cannot be empty.");
        if (name == null || name.isEmpty()) throw new InvalidBookingException("Guest name cannot be empty.");
        if (!VALID_ROOMS.contains(roomType)) throw new InvalidBookingException("Invalid room type selected.");
        if (!inventory.containsKey(roomType)) throw new InvalidBookingException("Room type not available.");
        if (inventory.get(roomType) <= 0) throw new InvalidBookingException("No rooms available for " + roomType);
    }
}

// ---------------------- Cancellation Service ----------------------
class CancellationService {
    private Stack<String> rollbackStack = new Stack<>();
    public synchronized void cancelBooking(String reservationId, Map<String, Reservation> reservations,
                                           BookingHistory history, Map<String, Integer> inventory,
                                           AddOnServiceManager serviceManager) throws InvalidBookingException {
        if (!reservations.containsKey(reservationId)) throw new InvalidBookingException("Reservation ID does not exist.");
        Reservation r = reservations.get(reservationId);
        rollbackStack.push(reservationId);
        reservations.remove(reservationId);
        serviceManager.removeServices(reservationId);
        history.removeReservation(r);
        String roomType = r.getRoomType();
        inventory.put(roomType, inventory.get(roomType) + 1);
        System.out.println("Booking " + reservationId + " cancelled successfully!");
    }
    public void showRollbackStack() {
        if (rollbackStack.isEmpty()) System.out.println("No recent cancellations.");
        else System.out.println("Rollback Stack (most recent cancellations): " + rollbackStack);
    }
}

// ---------------------- Booking System with Thread Safety ----------------------
class BookingSystem {
    private Map<String, Integer> inventory = new ConcurrentHashMap<>();
    private Map<String, Reservation> reservations = new ConcurrentHashMap<>();
    private BookingHistory history;
    public BookingSystem(BookingHistory history) {
        this.history = history;
        inventory.put("Single", 2);
        inventory.put("Double", 2);
        inventory.put("Suite", 1);
    }
    public synchronized void bookRoom(String id, String name, String roomType) throws InvalidBookingException {
        InvalidBookingValidator.validate(id, name, roomType, inventory);
        inventory.put(roomType, inventory.get(roomType) - 1);
        Reservation r = new Reservation(id, name, roomType);
        reservations.put(id, r);
        history.addReservation(r);
        System.out.println("✅ Booking confirmed: " + r);
    }
    public Map<String, Reservation> getReservations() { return reservations; }
    public Map<String, Integer> getInventory() { return inventory; }
    public void showAllBookings() { for (Reservation r : reservations.values()) System.out.println(r); }
    public void showInventory() { for (String room : inventory.keySet()) System.out.println(room + ": " + inventory.get(room)); }
}

// ---------------------- Booking Task ----------------------
class BookingTask implements Runnable {
    private BookingSystem system; private String id, name, roomType;
    public BookingTask(BookingSystem system, String id, String name, String roomType) {
        this.system = system; this.id = id; this.name = name; this.roomType = roomType;
    }
    @Override
    public void run() {
        try { system.bookRoom(id, name, roomType); }
        catch (InvalidBookingException e) { System.out.println("❌ Booking failed for " + id + ": " + e.getMessage()); }
    }
}

// ---------------------- Main Application ----------------------
public class BookMyStayApp {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);

        BookingHistory history = new BookingHistory();
        AddOnServiceManager serviceManager = new AddOnServiceManager();
        CancellationService cancellationService = new CancellationService();
        BookingReportService report = new BookingReportService();
        BookingSystem bookingSystem = new BookingSystem(history);

        // Predefined services
        Service wifi = new Service("WiFi", 10);
        Service breakfast = new Service("Breakfast", 20);
        Service parking = new Service("Parking", 15);
        Service spa = new Service("Spa", 50);

        ExecutorService executor = Executors.newFixedThreadPool(5); // For concurrent booking

        while (true) {
            try {
                System.out.println("\n===== Book My Stay App =====");
                System.out.println("1. Create Booking");
                System.out.println("2. Add Services");
                System.out.println("3. View Booking");
                System.out.println("4. Cancel Booking");
                System.out.println("5. View History");
                System.out.println("6. Generate Report");
                System.out.println("7. Show Rollback Stack");
                System.out.println("8. Concurrent Booking Simulation");
                System.out.println("9. Exit");
                System.out.print("Enter choice: ");
                int choice = sc.nextInt(); sc.nextLine();

                switch (choice) {
                    case 1:
                        System.out.print("Enter ID: "); String id = sc.nextLine();
                        System.out.print("Enter Name: "); String name = sc.nextLine();
                        System.out.print("Enter Room Type (Single/Double/Suite): "); String room = sc.nextLine();
                        bookingSystem.bookRoom(id, name, room);
                        break;

                    case 2:
                        System.out.print("Enter Reservation ID: "); String rid = sc.nextLine();
                        if (!bookingSystem.getReservations().containsKey(rid)) { System.out.println("Invalid Reservation ID"); break; }
                        while (true) {
                            System.out.println("1.WiFi 2.Breakfast 3.Parking 4.Spa 5.Done");
                            int c = sc.nextInt(); if (c == 5) break;
                            switch (c) {
                                case 1: serviceManager.addService(rid, wifi); break;
                                case 2: serviceManager.addService(rid, breakfast); break;
                                case 3: serviceManager.addService(rid, parking); break;
                                case 4: serviceManager.addService(rid, spa); break;
                                default: System.out.println("Invalid choice."); break;
                            }
                        } sc.nextLine(); break;

                    case 3:
                        System.out.print("Enter Reservation ID: "); String vid = sc.nextLine();
                        Reservation res = bookingSystem.getReservations().get(vid);
                        if (res == null) { System.out.println("Reservation not found."); break; }
                        System.out.println(res);
                        serviceManager.displayServices(vid);
                        System.out.println("Total Add-On Cost: $" + serviceManager.calculateTotalCost(vid));
                        break;

                    case 4:
                        System.out.print("Enter Reservation ID to cancel: "); String cancelId = sc.nextLine();
                        cancellationService.cancelBooking(cancelId, bookingSystem.getReservations(),
                                history, bookingSystem.getInventory(), serviceManager);
                        break;

                    case 5: report.showAll(history.getAllReservations()); break;
                    case 6: report.summary(history.getAllReservations()); break;
                    case 7: cancellationService.showRollbackStack(); break;

                    case 8: // Concurrent Booking Simulation
                        executor.submit(new BookingTask(bookingSystem, "R201", "Alice", "Single"));
                        executor.submit(new BookingTask(bookingSystem, "R202", "Bob", "Double"));
                        executor.submit(new BookingTask(bookingSystem, "R203", "Charlie", "Single"));
                        executor.submit(new BookingTask(bookingSystem, "R204", "David", "Suite"));
                        executor.submit(new BookingTask(bookingSystem, "R205", "Eve", "Single"));
                        executor.shutdown();
                        executor.awaitTermination(5, TimeUnit.SECONDS);
                        bookingSystem.showAllBookings();
                        bookingSystem.showInventory();
                        executor = Executors.newFixedThreadPool(5); // Reset executor for next use
                        break;

                    case 9: System.out.println("Exiting..."); sc.close(); executor.shutdownNow(); return;
                    default: System.out.println("Invalid choice.");
                }

            } catch (InvalidBookingException e) { System.out.println("❌ Operation Failed: " + e.getMessage()); }
            catch (Exception e) { System.out.println("⚠ Unexpected error: " + e.getMessage()); sc.nextLine(); }
        }
    }
}
