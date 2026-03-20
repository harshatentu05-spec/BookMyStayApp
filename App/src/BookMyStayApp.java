import java.util.*;

/**
 * Use Case 6: Room Allocation & Reservation Confirmation
 *
 * Combines Queue (FIFO), HashMap (inventory), and Set (unique room IDs)
 * to prevent double booking.
 */
public class BookMyStayApp {

    // 🔹 Reservation (Booking Request)
    static class Reservation {
        private String guestName;
        private String roomType;

        public Reservation(String guestName, String roomType) {
            this.guestName = guestName;
            this.roomType = roomType;
        }

        public String getGuestName() {
            return guestName;
        }

        public String getRoomType() {
            return roomType;
        }
    }

    // 🔹 Booking Queue (FIFO)
    static class BookingQueue {
        private Queue<Reservation> queue = new LinkedList<>();

        public void addRequest(Reservation r) {
            queue.offer(r);
            System.out.println("Request added to queue.");
        }

        public Reservation getNextRequest() {
            return queue.poll(); // removes from queue
        }

        public boolean isEmpty() {
            return queue.isEmpty();
        }

        public void viewQueue() {
            System.out.println("\n--- Pending Requests ---");
            for (Reservation r : queue) {
                System.out.println(r.getGuestName() + " → " + r.getRoomType());
            }
        }
    }

    // 🔹 Inventory Service
    static class InventoryService {
        private HashMap<String, Integer> availability = new HashMap<>();

        public void addRoom(String type, int count) {
            availability.put(type, count);
        }

        public int getAvailability(String type) {
            return availability.getOrDefault(type, 0);
        }

        public void decrementRoom(String type) {
            availability.put(type, getAvailability(type) - 1);
        }

        public void displayInventory() {
            System.out.println("\n--- Current Inventory ---");
            for (String type : availability.keySet()) {
                System.out.println(type + " → " + availability.get(type));
            }
        }
    }

    // 🔹 Booking Service (CORE LOGIC)
    static class BookingService {

        private Set<String> allocatedRoomIds = new HashSet<>();
        private HashMap<String, Set<String>> roomAllocations = new HashMap<>();

        // Generate unique room ID
        private String generateRoomId(String roomType) {
            return roomType.substring(0, 2).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 5);
        }

        public void processNextBooking(BookingQueue queue, InventoryService inventory) {

            if (queue.isEmpty()) {
                System.out.println("No pending requests.");
                return;
            }

            // 🔥 Step 1: Get request (FIFO)
            Reservation r = queue.getNextRequest();

            String type = r.getRoomType();

            // 🔥 Step 2: Check availability
            if (inventory.getAvailability(type) <= 0) {
                System.out.println("Booking failed for " + r.getGuestName() + " (No rooms available)");
                return;
            }

            // 🔥 Step 3: Generate unique room ID
            String roomId;
            do {
                roomId = generateRoomId(type);
            } while (allocatedRoomIds.contains(roomId));

            // 🔥 Step 4: Store ID in Set (uniqueness)
            allocatedRoomIds.add(roomId);

            // 🔥 Step 5: Map room type → assigned rooms
            roomAllocations.putIfAbsent(type, new HashSet<>());
            roomAllocations.get(type).add(roomId);

            // 🔥 Step 6: Update inventory
            inventory.decrementRoom(type);

            // 🔥 Step 7: Confirm booking
            System.out.println("\n✅ Booking Confirmed!");
            System.out.println("Guest: " + r.getGuestName());
            System.out.println("Room Type: " + type);
            System.out.println("Room ID: " + roomId);
        }

        public void showAllocations() {
            System.out.println("\n--- Room Allocations ---");
            for (String type : roomAllocations.keySet()) {
                System.out.println(type + " → " + roomAllocations.get(type));
            }
        }
    }

    // 🔹 Main Method
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        BookingQueue queue = new BookingQueue();
        InventoryService inventory = new InventoryService();
        BookingService bookingService = new BookingService();

        // Initialize inventory
        inventory.addRoom("Single Room", 2);
        inventory.addRoom("Double Room", 1);
        inventory.addRoom("Suite Room", 1);

        System.out.println("===== Room Allocation System (UC6) =====");

        while (true) {
            System.out.println("\n1. Add Booking Request");
            System.out.println("2. Process Next Booking");
            System.out.println("3. View Pending Requests");
            System.out.println("4. View Inventory");
            System.out.println("5. View Allocations");
            System.out.println("6. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Enter guest name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter room type: ");
                    String type = sc.nextLine();

                    queue.addRequest(new Reservation(name, type));
                    break;

                case 2:
                    bookingService.processNextBooking(queue, inventory);
                    break;

                case 3:
                    queue.viewQueue();
                    break;

                case 4:
                    inventory.displayInventory();
                    break;

                case 5:
                    bookingService.showAllocations();
                    break;

                case 6:
                    System.out.println("Exiting system...");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}