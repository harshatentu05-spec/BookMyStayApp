import java.util.*;

/**
 * Use Case 5: Booking Request Queue (First-Come-First-Served)
 *
 * Demonstrates how booking requests are handled fairly using Queue (FIFO).
 */
public class BookMyStayApp {

    // 🔹 Reservation Class (Represents a booking request)
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

        @Override
        public String toString() {
            return "Guest: " + guestName + " | Room: " + roomType;
        }
    }

    // 🔹 Booking Queue (FIFO)
    static class BookingQueue {
        private Queue<Reservation> queue;

        public BookingQueue() {
            queue = new LinkedList<>();
        }

        // Add request
        public void addRequest(Reservation reservation) {
            queue.offer(reservation);
            System.out.println("Request added to queue.");
        }

        // View all requests
        public void viewQueue() {
            if (queue.isEmpty()) {
                System.out.println("No pending booking requests.");
                return;
            }

            System.out.println("\n--- Booking Request Queue ---");
            for (Reservation r : queue) {
                System.out.println(r);
            }
        }

        // Peek next request (without removing)
        public void peekNext() {
            if (queue.isEmpty()) {
                System.out.println("No requests in queue.");
            } else {
                System.out.println("Next request: " + queue.peek());
            }
        }
    }

    // 🔹 Main Method
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        BookingQueue bookingQueue = new BookingQueue();

        System.out.println("===== Booking Request System (UC5) =====");

        while (true) {
            System.out.println("\n1. Add Booking Request");
            System.out.println("2. View All Requests");
            System.out.println("3. View Next Request");
            System.out.println("4. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {

                case 1:
                    System.out.print("Enter guest name: ");
                    String name = sc.nextLine();

                    System.out.print("Enter room type: ");
                    String room = sc.nextLine();

                    Reservation reservation = new Reservation(name, room);
                    bookingQueue.addRequest(reservation);
                    break;

                case 2:
                    bookingQueue.viewQueue();
                    break;

                case 3:
                    bookingQueue.peekNext();
                    break;

                case 4:
                    System.out.println("Exiting system...");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}