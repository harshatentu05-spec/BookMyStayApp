
public class BookMyStayApp {

    // 🔹 Abstract Class
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

    // 🔹 Single Room
    static class SingleRoom extends Room {
        public SingleRoom() {
            super("Single Room", 1, 1500);
        }

        @Override
        public void displayDetails() {
            System.out.println(roomType + " | Beds: " + beds + " | Price: ₹" + price);
        }
    }

    // 🔹 Double Room
    static class DoubleRoom extends Room {
        public DoubleRoom() {
            super("Double Room", 2, 2500);
        }

        @Override
        public void displayDetails() {
            System.out.println(roomType + " | Beds: " + beds + " | Price: ₹" + price);
        }
    }

    // 🔹 Suite Room
    static class SuiteRoom extends Room {
        public SuiteRoom() {
            super("Suite Room", 3, 5000);
        }

        @Override
        public void displayDetails() {
            System.out.println(roomType + " | Beds: " + beds + " | Price: ₹" + price);
        }
    }

    // 🔹 Main Method (Entry Point)
    public static void main(String[] args) {

        System.out.println("===== Available Room Types =====");

        // Polymorphism
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Static availability
        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        // Display
        single.displayDetails();
        System.out.println("Available: " + singleAvailable + "\n");

        doubleRoom.displayDetails();
        System.out.println("Available: " + doubleAvailable + "\n");

        suite.displayDetails();
        System.out.println("Available: " + suiteAvailable);

        System.out.println("\nApplication finished.");
    }
}
