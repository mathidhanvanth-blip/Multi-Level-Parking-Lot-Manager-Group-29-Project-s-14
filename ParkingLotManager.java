import java.util.Scanner;

/*
 * MULTI-LEVEL PARKING LOT MANAGER
 * Concepts used (as per Modules 1, 2, 3):
 *  - Scanner input, data types, constants, ternary operator
 *  - if-else ladder, switch menu, do-while loop, for loops, nested loops
 *  - Methods, parameter passing, return values, method overloading
 *  - Recursion, 1-D arrays, 2-D arrays, enhanced for-each loop
 */

public class ParkingLotManager {
    // ------- Constants (final keyword - Module 1) -------
    static final int LEVELS = 3;          // number of floors
    static final int SLOTS  = 5;          // slots per floor
    static final double CAR_RATE  = 40.0; // per hour
    static final double BIKE_RATE = 20.0;
    static final double TRUCK_RATE = 60.0;

    // ------- 2-D Array: parking[level][slot] stores vehicle number plate -------
    static String[][] parking = new String[LEVELS][SLOTS];
    static int[] hoursParked = new int[LEVELS * SLOTS]; // hours for each slot (1-D array)

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {          // command-line style main
        int choice;
        do {                                          // do-while loop (Module 2)
            displayMenu();
            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {                         // switch (Module 2)
                case 1 -> parkVehicle();
                case 2 -> removeVehicle();
                case 3 -> searchVehicle();
                case 4 -> displayStatus();
                case 5 -> showSummary();
                case 6 -> System.out.println("Thank you! Exiting...");
                default -> System.out.println("Invalid choice! Try again.");
            }
        } while (choice != 6);                        // exit condition
    }

    // ------- Menu (simple method) -------
    static void displayMenu() {
        System.out.println("\n===== MULTI-LEVEL PARKING LOT =====");
        System.out.println("1. Park Vehicle");
        System.out.println("2. Remove Vehicle (calculate fee)");
        System.out.println("3. Search Vehicle");
        System.out.println("4. Display Parking Status");
        System.out.println("5. Parking Summary");
        System.out.println("6. Exit");
    }

    // ------- Park a vehicle (uses loops, operators, if-else) -------
    static void parkVehicle() {
        System.out.print("Enter vehicle plate number: ");
        String plate = sc.next();

        System.out.print("Enter vehicle type (1-Car, 2-Bike, 3-Truck): ");
        int type = sc.nextInt();
        String vType = (type == 1) ? "CAR" : (type == 2) ? "BIKE" : "TRUCK"; // ternary (Module 1)

        // Find first free slot using nested for + if (nested loops, Module 2)
        boolean parked = false;
        for (int level = 0; level < LEVELS; level++) {
            for (int slot = 0; slot < SLOTS; slot++) {
                if (parking[level][slot] == null) {   // relational + logical operators
                    parking[level][slot] = plate + "-" + vType;
                    System.out.print("Enter hours to park: ");
                    hoursParked[level * SLOTS + slot] = sc.nextInt();
                    System.out.println("Vehicle parked at Level " + (level + 1) + ", Slot " + (slot + 1));
                    parked = true;
                    break;   // break statement (Module 2)
                }
            }
            if (parked) break;
        }
        if (!parked) {
            System.out.println("Sorry! Parking is FULL on all levels.");
        }
    }

    // ------- Remove vehicle & calculate fee (method overloading used below) -------
    static void removeVehicle() {
        System.out.print("Enter vehicle plate number to remove: ");
        String plate = sc.next();

        for (int level = 0; level < LEVELS; level++) {
            for (int slot = 0; slot < SLOTS; slot++) {
                if (parking[level][slot] != null && parking[level][slot].startsWith(plate)) {
                    int hours = hoursParked[level * SLOTS + slot];
                    String vType = parking[level][slot].substring(plate.length() + 1);

                    double fee;
                    fee = switch (vType) {
                        case "CAR" -> calculateFee(hours, CAR_RATE);       // overload 1
                        case "BIKE" -> calculateFee(hours, BIKE_RATE);    // overload 1
                        default -> calculateFee(hours, TRUCK_RATE, 2.0);   // overload 2
                    };

                    System.out.println("Vehicle " + plate + " removed from Level " + (level + 1)
                                       + ", Slot " + (slot + 1));
                    System.out.println("Parking hours: " + hours + " | Fee: Rs." + fee);
                    parking[level][slot] = null;
                    hoursParked[level * SLOTS + slot] = 0;
                    return;   // exit method after removal
                }
            }
        }
        System.out.println("Vehicle not found in the parking lot!");
    }

    // ------- METHOD OVERLOADING (Module 3): two versions of calculateFee -------
    static double calculateFee(int hours, double rate) {          // version 1
        return hours * rate;
    }
    static double calculateFee(int hours, double rate, double extraCharge) { // version 2
        return (hours * rate) + extraCharge;
    }

    // ------- Search vehicle (enhanced for-each + tracking indices) -------
    static void searchVehicle() {
        System.out.print("Enter vehicle plate number to search: ");
        String plate = sc.next();

        for (int level = 0; level < LEVELS; level++) {
            for (int slot = 0; slot < SLOTS; slot++) {
                if (parking[level][slot] != null && parking[level][slot].startsWith(plate)) {
                    System.out.println("Found at Level " + (level + 1) + ", Slot " + (slot + 1));
                    return;
                }
            }
        }
        System.out.println("Vehicle not found!");
    }

    // ------- Display full status (2-D array traversal, formatted printing) -------
    static void displayStatus() {
        System.out.println("\nLevel | Slot | Vehicle");
        for (int level = 0; level < LEVELS; level++) {
            System.out.println("------ L E V E L  " + (level + 1) + " ------");
            for (int slot = 0; slot < SLOTS; slot++) {
                String v = parking[level][slot];
                System.out.printf("  %d   |  %d   | %s%n",
                        (level + 1), (slot + 1),
                        (v == null ? "[ FREE ]" : v));   // ternary operator
            }
        }
    }

    // ------- Summary: uses RECURSION (Module 3) to count occupied slots -------
    static void showSummary() {
        int occupied = countOccupied(0, 0);   // recursive call
        int free = (LEVELS * SLOTS) - occupied;
        System.out.println("Total slots : " + (LEVELS * SLOTS));
        System.out.println("Occupied    : " + occupied);
        System.out.println("Free        : " + free);

        // Enhanced for-each loop (Module 3) to list all parked plates in a 1-D copy
        System.out.print("Parked vehicles: ");
        boolean any = false;
        for (String[] row : parking) {
            for (String cell : row) {
                if (cell != null) {
                    System.out.print(cell + "  ");
                    any = true;
                }
            }
        }
        if (!any) System.out.print("None");
        System.out.println();
    }

    // RECURSION: base case = reached last slot; recursive case = check next slot
    static int countOccupied(int level, int slot) {
        if (level == LEVELS) return 0;                    // base case
        if (slot == SLOTS) return countOccupied(level + 1, 0); // recursive case (next row)
        int count = (parking[level][slot] != null) ? 1 : 0;
        return count + countOccupied(level, slot + 1);    // recursive case (next column)
    }
}
