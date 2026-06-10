# 🏨 Luxe Stay - Hotel Reservation System

A lightweight, robust console-based Hotel Reservation System built in Java. This application demonstrates Object-Oriented Programming (OOP) principles, clean encapsulation, and local data persistence using Java Serialization (File I/O) as a lightweight mock database.

---

## ✨ Features

*   **Room Categorization:** Supports tiered rooms (`Standard`, `Deluxe`, `Suite`), each configured with distinct pricing models.
*   **Dynamic Inventory Dashboard:** Displays real-time availability of rooms along with their current status metrics (`🟢 AVAILABLE` or `🔴 BOOKED`).
*   **Reservation Lifecycle Management:** Users can instantly search, allocate, book, or cancel room reservations.
*   **Integrated Payment Simulator:** Simulates an external transaction gateway with payload authentication logic before committing bookings.
*   **Local File I/O Persistence:** Saves state instantly to a binary file (`hotel_data.dat`). Data persists even if the console application is closed or restarted.

---

## 🏗️ Architecture & OOP Design

The system relies on clean separation of concerns:
1.  **Domain Models (`Room`, `Reservation`, `RoomCategory`):** Fully encapsulated entities maintaining strict data safety using `private` fields and structural models.
2.  **Database Layer (`HotelDatabase`):** Manages file streams (`ObjectInputStream` / `ObjectOutputStream`) to handle safe data reads and structural commits.
3.  **App Controller (`HotelReservationSystem`):** Powers the command-line console interface, validation pipelines, and transaction mapping.

---

## 🚀 Getting Started

### Prerequisites
*   Java Development Kit (JDK) 8 or higher installed.

### Compilation and Execution

1. **Clone or download** the repository containing `HotelReservationSystem.java`.
2. Open your terminal or command prompt and navigate to the project directory.
3. **Compile the program:**
```bash
   javac HotelReservationSystem.java
