import java.util.*;

/**
 * Book My Stay Application
 * Use Case 4: Room Search (Read-Only Access)
 */
public class BookMyStayApp {

    // 🔹 Abstract Room Class
    static abstract class Room {
        protected String roomType;
        protected int beds;
        protected double price;

        public Room(String roomType, int beds, double price) {
            this.roomType = roomType;
            this.beds = beds;
            this.price = price;
        }

        public String getRoomType() {
            return roomType;
        }

        public abstract void displayDetails();
    }

    // 🔹 Room Types
    static class SingleRoom extends Room {
        public SingleRoom() {
            super("Single Room", 1, 1500);
        }

        public void displayDetails() {
            System.out.println(roomType + " | Beds: " + beds + " | Price: ₹" + price);
        }
    }

    static class DoubleRoom extends Room {
        public DoubleRoom() {
            super("Double Room", 2, 2500);
        }

        public void displayDetails() {
            System.out.println(roomType + " | Beds: " + beds + " | Price: ₹" + price);
        }
    }

    static class SuiteRoom extends Room {
        public SuiteRoom() {
            super("Suite Room", 3, 5000);
        }

        public void displayDetails() {
            System.out.println(roomType + " | Beds: " + beds + " | Price: ₹" + price);
        }
    }

    // 🔹 Inventory (Same as UC3)
    static class RoomInventory {
        private HashMap<String, Integer> availability = new HashMap<>();

        public void addRoom(String type, int count) {
            availability.put(type, count);
        }

        public int getAvailability(String type) {
            return availability.getOrDefault(type, 0);
        }

        // Booking modifies state (NOT used in search)
        public boolean bookRoom(String type) {
            int current = getAvailability(type);
            if (current > 0) {
                availability.put(type, current - 1);
                return true;
            }
            return false;
        }

        // Read-only access to all data
        public HashMap<String, Integer> getAllAvailability() {
            return availability;
        }
    }

    // 🔹 Search Service (READ-ONLY)
    static class SearchService {

        public void searchAvailableRooms(RoomInventory inventory, List<Room> rooms) {

            System.out.println("\n--- Available Rooms ---");

            boolean found = false;

            for (Room room : rooms) {

                int available = inventory.getAvailability(room.getRoomType());

                // Defensive check: only show available rooms
                if (available > 0) {
                    room.displayDetails();
                    System.out.println("Available: " + available + "\n");
                    found = true;
                }
            }

            if (!found) {
                System.out.println("No rooms available at the moment.");
            }
        }
    }

    // 🔹 Main Method
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Room objects
        List<Room> rooms = new ArrayList<>();
        rooms.add(new SingleRoom());
        rooms.add(new DoubleRoom());
        rooms.add(new SuiteRoom());

        // Inventory setup
        RoomInventory inventory = new RoomInventory();
        inventory.addRoom("Single Room", 5);
        inventory.addRoom("Double Room", 0); // to test filtering
        inventory.addRoom("Suite Room", 2);

        // Search Service
        SearchService searchService = new SearchService();

        System.out.println("===== Welcome to Book My Stay =====");

        while (true) {
            System.out.println("\n1. Search Available Rooms");
            System.out.println("2. Book Room");
            System.out.println("3. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    // 🔥 READ-ONLY OPERATION
                    searchService.searchAvailableRooms(inventory, rooms);
                    break;

                case 2:
                    System.out.print("Enter room type to book: ");
                    String type = sc.nextLine();

                    if (inventory.bookRoom(type)) {
                        System.out.println("Booking successful!");
                    } else {
                        System.out.println("Room not available!");
                    }
                    break;

                case 3:
                    System.out.println("Thank you!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}