import java.io.*;
import java.util.*;
import java.util.concurrent.*;

// ---------------------- Custom Exception ----------------------
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) { super(message); }
}

// ---------------------- Reservation ----------------------
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id, name, roomType;
    public Reservation(String id, String name, String roomType) {
        this.id = id; this.name = name; this.roomType = roomType;
    }
    public String getId() { return id; }
    public String getRoomType() { return roomType; }
    @Override
    public String toString() { return "Reservation ID: " + id + ", Name: " + name + ", Room: " + roomType; }
}

// ---------------------- Service ----------------------
class Service implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name; private double cost;
    public Service(String name, double cost) { this.name = name; this.cost = cost; }
    public double getCost() { return cost; }
    @Override public String toString() { return name + " ($" + cost + ")"; }
}

// ---------------------- Add-On Service Manager ----------------------
class AddOnServiceManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, List<Service>> serviceMap = new ConcurrentHashMap<>();
    public void addService(String reservationId, Service service) {
        serviceMap.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
    }
    public List<Service> getServices(String reservationId) { return serviceMap.getOrDefault(reservationId, new ArrayList<>()); }
    public double calculateTotalCost(String reservationId) {
        double total = 0; for (Service s : getServices(reservationId)) total += s.getCost(); return total;
    }
    public void removeServices(String reservationId) { serviceMap.remove(reservationId); }
}

// ---------------------- Booking History ----------------------
class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Reservation> history = Collections.synchronizedList(new ArrayList<>());
    public void addReservation(Reservation reservation) { history.add(reservation); }
    public void removeReservation(Reservation reservation) { history.remove(reservation); }
    public List<Reservation> getAllReservations() { return history; }
}

// ---------------------- Persistence Service ----------------------
class PersistenceService {
    private static final String FILENAME = "bookmystay.dat";

    public static void saveState(Map<String, Integer> inventory, Map<String, Reservation> reservations,
                                 AddOnServiceManager serviceManager, BookingHistory history) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILENAME))) {
            oos.writeObject(inventory);
            oos.writeObject(reservations);
            oos.writeObject(serviceManager);
            oos.writeObject(history);
            System.out.println("✅ System state saved successfully!");
        } catch (Exception e) {
            System.out.println("❌ Failed to save state: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static void restoreState(BookingSystem system, AddOnServiceManager serviceManager, BookingHistory history) {
        File file = new File(FILENAME);
        if (!file.exists()) return; // nothing to restore
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Map<String, Integer> inv = (Map<String, Integer>) ois.readObject();
            Map<String, Reservation> res = (Map<String, Reservation>) ois.readObject();
            AddOnServiceManager savedServiceManager = (AddOnServiceManager) ois.readObject();
            BookingHistory savedHistory = (BookingHistory) ois.readObject();

            system.setInventory(inv);
            system.setReservations(res);
            serviceManager.serviceMap = savedServiceManager.serviceMap;
            history.getAllReservations().clear();
            history.getAllReservations().addAll(savedHistory.getAllReservations());

            System.out.println("✅ System state restored successfully!");
        } catch (Exception e) {
            System.out.println("⚠ Failed to restore state: " + e.getMessage());
        }
    }
}

// ---------------------- Booking System ----------------------
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
        if (!inventory.containsKey(roomType)) throw new InvalidBookingException("Invalid room type.");
        if (inventory.get(roomType) <= 0) throw new InvalidBookingException("No rooms available for " + roomType);
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

    // Used for persistence restoration
    public void setInventory(Map<String, Integer> inv) { this.inventory = inv; }
    public void setReservations(Map<String, Reservation> res) { this.reservations = res; }
}

// ---------------------- Main Application ----------------------
public class BookMyStayApp {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        BookingHistory history = new BookingHistory();
        AddOnServiceManager serviceManager = new AddOnServiceManager();
        BookingSystem bookingSystem = new BookingSystem(history);

        // Restore previous state if exists
        PersistenceService.restoreState(bookingSystem, serviceManager, history);

        Service wifi = new Service("WiFi", 10);
        Service breakfast = new Service("Breakfast", 20);
        Service parking = new Service("Parking", 15);
        Service spa = new Service("Spa", 50);

        ExecutorService executor = Executors.newFixedThreadPool(5);

        while (true) {
            try {
                System.out.println("\n===== Book My Stay App =====");
                System.out.println("1. Create Booking");
                System.out.println("2. Add Services");
                System.out.println("3. View Booking");
                System.out.println("4. Exit & Save State");
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
                        System.out.println("1.WiFi 2.Breakfast 3.Parking 4.Spa");
                        int c = sc.nextInt(); sc.nextLine();
                        switch (c) {
                            case 1: serviceManager.addService(rid, wifi); break;
                            case 2: serviceManager.addService(rid, breakfast); break;
                            case 3: serviceManager.addService(rid, parking); break;
                            case 4: serviceManager.addService(rid, spa); break;
                            default: System.out.println("Invalid choice."); break;
                        }
                        break;
                    case 3:
                        bookingSystem.showAllBookings();
                        bookingSystem.showInventory();
                        break;
                    case 4:
                        PersistenceService.saveState(bookingSystem.getInventory(), bookingSystem.getReservations(),
                                serviceManager, history);
                        executor.shutdownNow();
                        sc.close();
                        System.out.println("Exiting...");
                        return;
                    default: System.out.println("Invalid choice.");
                }
            } catch (InvalidBookingException e) { System.out.println("❌ " + e.getMessage()); }
            catch (Exception e) { System.out.println("⚠ Unexpected error: " + e.getMessage()); }
        }
    }
}