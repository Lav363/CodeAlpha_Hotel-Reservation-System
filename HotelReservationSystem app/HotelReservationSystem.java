import java.io.*;
import java.util.*;

// ==========================================
// 1. DOMAIN MODELS (Serializable Data)
// ==========================================

enum RoomCategory {
    STANDARD(1200.0), DELUXE(2500.0), SUITE(5000.0);

    private final double pricePerNight;

    RoomCategory(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }
}

class Room implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int roomNumber;
    private final RoomCategory category;
    private boolean isAvailable;

    public Room(int roomNumber, RoomCategory category) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.isAvailable = true;
    }

    public int getRoomNumber() { return roomNumber; }
    public RoomCategory getCategory() { return category; }
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    @Override
    public String toString() {
        return String.format("Room #%03d | %-8s | ₹%.2f/night | %s", 
                roomNumber, category, category.getPricePerNight(), 
                isAvailable ? "🟢 AVAILABLE" : "🔴 BOOKED");
    }
}

class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String bookingId;
    private final String guestName;
    private final Room room;
    private final int nights;
    private final double totalAmount;
    private boolean isCancelled;

    public Reservation(String guestName, Room room, int nights) {
        this.bookingId = "RES" + (10000 + new Random().nextInt(90000));
        this.guestName = guestName;
        this.room = room;
        this.nights = nights;
        this.totalAmount = room.getCategory().getPricePerNight() * nights;
        this.isCancelled = false;
    }

    public String getBookingId() { return bookingId; }
    public String getGuestName() { return guestName; }
    public Room getRoom() { return room; }
    public double getTotalAmount() { return totalAmount; }
    public boolean isCancelled() { return isCancelled; }
    
    public void cancel() { 
        this.isCancelled = true; 
        this.room.setAvailable(true);
    }

    public void displayReceipt() {
        System.out.println("\n==========================================");
        System.out.println("            BOOKING RECEIPT               ");
        System.out.println("==========================================");
        System.out.println(" Booking ID    : " + bookingId);
        System.out.println(" Guest Name    : " + guestName);
        System.out.println(" Room Assigned : Room #" + room.getRoomNumber() + " (" + room.getCategory() + ")");
        System.out.println(" Duration      : " + nights + " Night(s)");
        System.out.println(" Total Paid    : ₹" + totalAmount);
        System.out.println(" Status        : " + (isCancelled ? "❌ CANCELLED" : "✅ CONFIRMED"));
        System.out.println("==========================================");
    }
}

// ==========================================
// 2. DATABASE / FILE I/O MANAGEMENT
// ==========================================

class HotelDatabase {
    private static final String FILE_NAME = "hotel_data.dat";
    private List<Room> rooms;
    private Map<String, Reservation> bookings;

    @SuppressWarnings("unchecked")
    public HotelDatabase() {
        rooms = new ArrayList<>();
        bookings = new HashMap<>();
        
        File file = new File(FILE_NAME);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                rooms = (List<Room>) ois.readObject();
                bookings = (Map<String, Reservation>) ois.readObject();
                System.out.println(">> Database loaded successfully. Synchronized " + rooms.size() + " rooms.");
            } catch (Exception e) {
                System.out.println(">> Error loading database. Initializing default layout.");
                initializeDefaultRooms();
            }
        } else {
            initializeDefaultRooms();
            saveData();
        }
    }

    private void initializeDefaultRooms() {
        // Initialize 9 rooms across various tiers
        rooms.add(new Room(101, RoomCategory.STANDARD));
        rooms.add(new Room(102, RoomCategory.STANDARD));
        rooms.add(new Room(103, RoomCategory.STANDARD));
        
        rooms.add(new Room(201, RoomCategory.DELUXE));
        rooms.add(new Room(202, RoomCategory.DELUXE));
        rooms.add(new Room(203, RoomCategory.DELUXE));
        
        rooms.add(new Room(301, RoomCategory.SUITE));
        rooms.add(new Room(302, RoomCategory.SUITE));
        rooms.add(new Room(303, RoomCategory.SUITE));
    }

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(rooms);
            oos.writeObject(bookings);
        } catch (IOException e) {
            System.out.println("❌ Critical Error: Unable to save database updates!");
        }
    }

    public List<Room> getRooms() { return rooms; }
    public Map<String, Reservation> getBookings() { return bookings; }
}

// ==========================================
// 3. MAIN APP CONTROLLER
// ==========================================

public class HotelReservationSystem {
    private static final HotelDatabase db = new HotelDatabase();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n🏨--- LUXE STAY HOTEL MANAGEMENT ---🏨");
            System.out.println("1. View Room Dashboard & Availability");
            System.out.println("2. Book a Room");
            System.out.println("3. View Booking Details");
            System.out.println("4. Cancel a Reservation");
            System.out.println("5. Exit Ecosystem");
            System.out.print("Select operational node [1-5]: ");
            
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1": showDashboard(); break;
                case "2": makeBooking(); break;
                case "3": viewBooking(); break;
                case "4": cancelBooking(); break;
                case "5": 
                    db.saveData();
                    System.out.println("\n💾 Systems saved state safely. Exiting application. Goodbye!");
                    return;
                default: System.out.println("⚠️ Invalid option choice. Try again.");
            }
        }
    }

    private static void showDashboard() {
        System.out.println("\n--- CURRENT ROOM INVENTORY ---");
        for (Room r : db.getRooms()) {
            System.out.println(r);
        }
    }

    private static void makeBooking() {
        System.out.println("\n--- INITIATE RESERVATION CONTEXT ---");
        System.out.println("Select Desired Room Category:");
        System.out.println("1. Standard (₹1200/night)");
        System.out.println("2. Deluxe   (₹2500/night)");
        System.out.println("3. Suite    (₹5000/night)");
        System.out.print("Selection: ");
        
        RoomCategory categoryChosen;
        String catChoice = scanner.nextLine().trim();
        switch (catChoice) {
            case "1": categoryChosen = RoomCategory.STANDARD; break;
            case "2": categoryChosen = RoomCategory.DELUXE; break;
            case "3": categoryChosen = RoomCategory.SUITE; break;
            default: System.out.println("⚠️ Unknown category. Aborting booking."); return;
        }

        // Find available room in that category
        Room allocatedRoom = null;
        for (Room r : db.getRooms()) {
            if (r.getCategory() == categoryChosen && r.isAvailable()) {
                allocatedRoom = r;
                break;
            }
        }

        if (allocatedRoom == null) {
            System.out.println("❌ Request Denied: No vacant rooms left in " + categoryChosen + " tier.");
            return;
        }

        System.out.print("Enter Guest Primary Name: ");
        String name = scanner.nextLine().trim();
        if(name.isEmpty()) {
            System.out.println("⚠️ Name cannot be empty.");
            return;
        }

        System.out.print("Enter Booking Duration (Nights): ");
        int nights;
        try {
            nights = Integer.parseInt(scanner.nextLine().trim());
            if (nights <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            System.out.println("⚠️ Invalid timeline metrics tracking. Aborting.");
            return;
        }

        double finalFee = allocatedRoom.getCategory().getPricePerNight() * nights;
        System.out.printf("Total calculated payload amount: ₹%.2f\n", finalFee);
        
        // Payment Simulation Gateway Interface
        System.out.println("\n--- GATEWAY PAYMENT SIMULATION ---");
        System.out.print("Enter simulated 16-digit Card Number: ");
        String card = scanner.nextLine().trim();
        System.out.print("Enter UPI/Card Secure Pin: ");
        String pin = scanner.nextLine().trim();
        
        System.out.print("Processing transaction payload...");
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}

        if(card.length() < 4 || pin.isEmpty()) {
            System.out.println("\n❌ Payment Terminal Exception: Authentication Failed.");
            return;
        }

        System.out.println("\n💰 Transaction Verified! Settlement completed successfully.");

        // Commit modifications to state
        allocatedRoom.setAvailable(false);
        Reservation reservation = new Reservation(name, allocatedRoom, nights);
        db.getBookings().put(reservation.getBookingId(), reservation);
        db.saveData(); // Persistent Commit

        reservation.displayReceipt();
    }

    private static void viewBooking() {
        System.out.print("\nEnter Alphanumeric Booking ID reference: ");
        String id = scanner.nextLine().trim().toUpperCase();
        Reservation res = db.getBookings().get(id);

        if (res != null) {
            res.displayReceipt();
        } else {
            System.out.println("❌ Record Not Found: Invalid Booking Code reference identifier.");
        }
    }

    private static void cancelBooking() {
        System.out.print("\nEnter Alphanumeric Booking ID reference to void: ");
        String id = scanner.nextLine().trim().toUpperCase();
        Reservation res = db.getBookings().get(id);

        if (res == null) {
            System.out.println("❌ Record Not Found: Impossible to clear entry parameters.");
            return;
        }

        if (res.isCancelled()) {
            System.out.println("⚠️ Redundant Request Flag: Target reservation has already been cancelled.");
            return;
        }

        res.cancel();
        db.saveData(); // Save updated state
        System.out.println("✅ Lifecycle Update: Booking structural parameters voided. Room #" 
                + res.getRoom().getRoomNumber() + " recycled into available pool inventory.");
    }
}