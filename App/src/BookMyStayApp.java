import java.util.HashMap;
import java.util.Scanner;

/**
 * Book My Stay Application
 * Use Case 3: Centralized Room Inventory using HashMap
 *
 * Demonstrates centralized state management using HashMap.
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

    // 🔹 Inventory Class (CORE OF UC3)
    static class RoomInventory {
        private HashMap<String, Integer> availability;

        // Constructor initializes inventory
        public RoomInventory() {
            availability = new HashMap<>();
        }

        // Add room type
        public void addRoom(String type, int count) {
            availability.put(type, count);
        }

        // Get availability
        public int getAvailability(String type) {
            return availability.getOrDefault(type, 0);
        }

        // Book room (update availability)
        public boolean bookRoom(String type) {
            int current = getAvailability(type);

            if (current > 0) {
                availability.put(type, current - 1);
                return true;
            }
            return false;
        }

        // Display all inventory
        public void displayInventory() {
            System.out.println("\n--- Current Inventory ---");
            for (String type : availability.keySet()) {
                System.out.println(type + " → Available: " + availability.get(type));
            }
        }
    }

    // 🔹 Main Method
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Room objects
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Centralized Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoom("Single Room", 5);
        inventory.addRoom("Double Room", 3);
        inventory.addRoom("Suite Room", 2);

        System.out.println("===== Welcome to Book My Stay =====");

        while (true) {
            System.out.println("\n1. View All Rooms");
            System.out.println("2. Check Availability");
            System.out.println("3. Book Room");
            System.out.println("4. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {

                case 1:
                    System.out.println("\n--- Room Details ---");
                    single.displayDetails();
                    doubleRoom.displayDetails();
                    suite.displayDetails();
                    break;

                case 2:
                    System.out.print("Enter room type: ");
                    String typeCheck = sc.nextLine();
                    System.out.println("Available: " + inventory.getAvailability(typeCheck));
                    break;

                case 3:
                    System.out.print("Enter room type to book: ");
                    String typeBook = sc.nextLine();

                    if (inventory.bookRoom(typeBook)) {
                        System.out.println("Booking successful!");
                    } else {
                        System.out.println("Room not available!");
                    }
                    break;

                case 4:
                    System.out.println("Thank you for using Book My Stay!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}