// # 🚗 Smart Parking Management System

// ## 📌 About the Project
// A Java-based Smart Parking Management System designed to manage vehicle parking, monitor slot availability, calculate parking fees, and provide fuel services.

// ## ✨ Features
// -  Vehicle entry and exit management
// - 🅿 Automatic parking slot assignment
// -  Parking fee calculation and bill generation
// -  Fuel service and price management
// -  Vehicle information updates
// -  Parking status and active vehicle display
// -  Transaction history
// -  Exception handling and input validation

// ##  Technologies Used
// - Java
// - Object-Oriented Programming (OOP)
// - VS Code

// ## ▶ How to Run

// 1. Clone the repository.
// 2. Open the project folder in VS Code.
// 3. Open the terminal.
// 4. Compile and run:

// ```bash
// javac SmartParkingSystem.java
// java SmartParkingSystem
// ```

// ## 💾 Data Storage
// The system stores data in memory during execution. Data is not permanently saved after the program closes.

// ## 🎯 Project Goal
// To simplify parking operations, improve parking space management, and automate parking fee calculations.

// ## 👨‍💻 Author
// Developed as a Java programming project.
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.Duration;

// ==================== CUSTOM EXCEPTIONS ====================

class ParkingFullException extends Exception {
    public ParkingFullException(String message) {
        super(message);
    }
}

class VehicleNotFoundException extends Exception {
    public VehicleNotFoundException(String message) {
        super(message);
    }
}

class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}

// ==================== FUEL TRANSACTION CLASS ====================

class FuelTransaction {
    private String fuelType;
    private double liters;
    private double pricePerLiter;
    private LocalDateTime transactionTime;

    public FuelTransaction(String fuelType, double liters, double pricePerLiter) {
        this.fuelType = fuelType;
        this.liters = liters;
        this.pricePerLiter = pricePerLiter;
        this.transactionTime = LocalDateTime.now();
    }

    public String getFuelType() {
        return fuelType;
    }

    public double getLiters() {
        return liters;
    }

    public double getPricePerLiter() {
        return pricePerLiter;
    }

    public double getTotalCost() {
        return liters * pricePerLiter;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    @Override
    public String toString() {
        return String.format("%s: %.2f L @ Rs.%.2f = Rs.%.2f",
                fuelType, liters, pricePerLiter, getTotalCost());
    }
}

// ==================== VEHICLE ABSTRACT CLASS ====================

abstract class Vehicle {
    protected String plateNumber;
    protected String vehicleType;
    protected LocalDateTime entryTime;
    protected LocalDateTime exitTime;
    protected List<FuelTransaction> fuelTransactions;

    public Vehicle(String plateNumber, String vehicleType) {
        this.plateNumber = plateNumber;
        this.vehicleType = vehicleType;
        this.entryTime = LocalDateTime.now();
        this.exitTime = null;
        this.fuelTransactions = new ArrayList<>();
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public void addFuelTransaction(FuelTransaction transaction) {
        fuelTransactions.add(transaction);
    }

    public List<FuelTransaction> getFuelTransactions() {
        return fuelTransactions;
    }

    public double getTotalFuelCost() {
        return fuelTransactions.stream().mapToDouble(FuelTransaction::getTotalCost).sum();
    }

    public abstract double calculateParkingFee();

    public long getParkingDurationMinutes() {
        LocalDateTime end = (exitTime != null) ? exitTime : LocalDateTime.now();
        return Duration.between(entryTime, end).toMinutes();
    }

    public String getFormattedEntryTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return entryTime.format(formatter);
    }

    @Override
    public String toString() {
        return String.format("| %-12s | %-12s | %-20s | %-25s |",
                vehicleType, plateNumber, getFormattedEntryTime(),
                fuelTransactions.isEmpty() ? "No fuel" : fuelTransactions.get(0).getFuelType());
    }
}

// ==================== CONCRETE VEHICLE CLASSES ====================

class Car extends Vehicle {
    private static final double PARKING_RATE_PER_HOUR = 50.0;

    public Car(String plateNumber) {
        super(plateNumber, "Car");
    }

    @Override
    public double calculateParkingFee() {
        long minutes = getParkingDurationMinutes();
        double hours = Math.ceil(minutes / 60.0);
        return hours * PARKING_RATE_PER_HOUR;
    }
}

class Motorcycle extends Vehicle {
    private static final double PARKING_RATE_PER_HOUR = 20.0;

    public Motorcycle(String plateNumber) {
        super(plateNumber, "Motorcycle");
    }

    @Override
    public double calculateParkingFee() {
        long minutes = getParkingDurationMinutes();
        double hours = Math.ceil(minutes / 60.0);
        return hours * PARKING_RATE_PER_HOUR;
    }
}

class Bajaj extends Vehicle {
    private static final double PARKING_RATE_PER_HOUR = 30.0;

    public Bajaj(String plateNumber) {
        super(plateNumber, "Bajaj");
    }

    @Override
    public double calculateParkingFee() {
        long minutes = getParkingDurationMinutes();
        double hours = Math.ceil(minutes / 60.0);
        return hours * PARKING_RATE_PER_HOUR;
    }
}

// ==================== PARKING SLOT CLASS ====================

class ParkingSlot {
    private int slotNumber;
    private boolean isOccupied;
    private Vehicle vehicle;

    public ParkingSlot(int slotNumber) {
        this.slotNumber = slotNumber;
        this.isOccupied = false;
        this.vehicle = null;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void assignVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
        this.isOccupied = true;
    }

    public Vehicle removeVehicle() {
        Vehicle leavingVehicle = this.vehicle;
        this.vehicle = null;
        this.isOccupied = false;
        return leavingVehicle;
    }

    public String getSlotStatus() {
        if (isOccupied) {
            return String.format("Slot %d: OCCUPIED - %s (%s)",
                    slotNumber, vehicle.getVehicleType(), vehicle.getPlateNumber());
        } else {
            return String.format("Slot %d: AVAILABLE", slotNumber);
        }
    }
}

// ==================== FUEL SERVICE CLASS ====================

class FuelService {
    private Map<String, Double> fuelPrices;

    public FuelService() {
        fuelPrices = new HashMap<>();
        fuelPrices.put("Petrol", 105.0);
        fuelPrices.put("Diesel", 94.0);
        fuelPrices.put("Oil", 120.0);
    }

    public double getFuelPrice(String fuelType) {
        return fuelPrices.getOrDefault(fuelType, 0.0);
    }

    public void updateFuelPrice(String fuelType, double newPrice) {
        if (fuelPrices.containsKey(fuelType)) {
            fuelPrices.put(fuelType, newPrice);
            System.out.printf(" Updated %s price to Rs. %.2f%n", fuelType, newPrice);
        } else {
            System.out.println(" Invalid fuel type! Use Petrol, Diesel, or Oil");
        }
    }

    public void displayFuelPrices() {
        System.out.println("\n--- Current Fuel Prices ---");
        for (Map.Entry<String, Double> entry : fuelPrices.entrySet()) {
            System.out.printf("%s: Rs. %.2f per liter%n", entry.getKey(), entry.getValue());
        }
    }
}

// ==================== PAYMENT CALCULATOR CLASS ====================

class PaymentCalculator {

    public static void generateBill(Vehicle vehicle, double parkingFee,
            double totalFuelCost, List<FuelTransaction> fuelTransactions) {

        double totalPayment = parkingFee + totalFuelCost;

        System.out.println("\n" + "=".repeat(60));
        System.out.println("                    PARKING BILL");
        System.out.println("=".repeat(60));
        System.out.printf("Vehicle Type: %s%n", vehicle.getVehicleType());
        System.out.printf("Plate Number: %s%n", vehicle.getPlateNumber());
        System.out.printf("Entry Time: %s%n", vehicle.getFormattedEntryTime());
        System.out.printf("Exit Time: %s%n", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.printf("Parking Duration: %d minutes (%.2f hours)%n",
                vehicle.getParkingDurationMinutes(),
                vehicle.getParkingDurationMinutes() / 60.0);
        System.out.printf("Parking Fee: Rs. %.2f%n", parkingFee);

        if (!fuelTransactions.isEmpty()) {
            System.out.println("-".repeat(60));
            System.out.println("FUEL SERVICES:");
            for (FuelTransaction ft : fuelTransactions) {
                System.out.printf("  • %s: %.2f L @ Rs.%.2f = Rs.%.2f%n",
                        ft.getFuelType(), ft.getLiters(),
                        ft.getPricePerLiter(), ft.getTotalCost());
            }
            System.out.printf("Total Fuel Cost: Rs. %.2f%n", totalFuelCost);
        }

        System.out.println("-".repeat(60));
        System.out.printf("TOTAL PAYMENT: Rs. %.2f%n", totalPayment);
        System.out.println("=".repeat(60));
        System.out.println("Thank you for using Smart Parking System!\n");
    }
}

// ==================== PARKING MANAGER CLASS ====================

class ParkingManager {
    private List<ParkingSlot> slots;
    private Map<String, Vehicle> activeVehicles;
    private FuelService fuelService;
    private int totalSlots;
    private List<String> transactionLog; // In-memory transaction log

    public ParkingManager(int totalSlots) {
        this.totalSlots = totalSlots;
        this.slots = new ArrayList<>();
        this.activeVehicles = new HashMap<>();
        this.fuelService = new FuelService();
        this.transactionLog = new ArrayList<>();

        for (int i = 1; i <= totalSlots; i++) {
            slots.add(new ParkingSlot(i));
        }
    }

    private ParkingSlot findAvailableSlot() throws ParkingFullException {
        for (ParkingSlot slot : slots) {
            if (!slot.isOccupied()) {
                return slot;
            }
        }
        throw new ParkingFullException("Parking is full! No available slots.");
    }

    private void addToTransactionLog(String logEntry) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        transactionLog.add(timestamp + " | " + logEntry);
    }

    // VEHICLE ENTRY - Register vehicle and assign parking slot
    public void vehicleEntry(String plateNumber, String vehicleType)
            throws ParkingFullException, InvalidInputException {

        if (plateNumber == null || plateNumber.trim().isEmpty()) {
            throw new InvalidInputException("Plate number cannot be empty!");
        }

        if (activeVehicles.containsKey(plateNumber)) {
            throw new InvalidInputException("Vehicle with this plate number is already in parking!");
        }

        Vehicle vehicle;
        switch (vehicleType.toLowerCase()) {
            case "car":
                vehicle = new Car(plateNumber);
                break;
            case "motorcycle":
                vehicle = new Motorcycle(plateNumber);
                break;
            case "bajaj":
                vehicle = new Bajaj(plateNumber);
                break;
            default:
                throw new InvalidInputException("Invalid vehicle type! Choose: Car, Motorcycle, or Bajaj");
        }

        ParkingSlot slot = findAvailableSlot();
        slot.assignVehicle(vehicle);
        activeVehicles.put(plateNumber, vehicle);

        System.out.printf("%n Vehicle %s (%s) entered. Assigned to Slot %d at %s%n",
                plateNumber, vehicleType, slot.getSlotNumber(), vehicle.getFormattedEntryTime());

        addToTransactionLog(String.format("ENTRY | %s | %s | Slot %d",
                vehicleType, plateNumber, slot.getSlotNumber()));
    }

    // PROVIDE FUEL SERVICE - Add fuel to a parked vehicle
    public double provideFuelService(String plateNumber, String fuelType, double liters)
            throws VehicleNotFoundException, InvalidInputException {

        Vehicle vehicle = activeVehicles.get(plateNumber);
        if (vehicle == null) {
            throw new VehicleNotFoundException("Vehicle with plate number " + plateNumber + " not found!");
        }

        if (liters <= 0) {
            throw new InvalidInputException("Liters must be greater than 0!");
        }

        double pricePerLiter = fuelService.getFuelPrice(fuelType);
        if (pricePerLiter == 0) {
            throw new InvalidInputException("Invalid fuel type! Use Petrol, Diesel, or Oil");
        }

        FuelTransaction transaction = new FuelTransaction(fuelType, liters, pricePerLiter);
        vehicle.addFuelTransaction(transaction);
        double fuelPayment = transaction.getTotalCost();

        System.out.printf("%n Fuel service provided to %s:%n", plateNumber);
        System.out.printf("   %s: %.2f liters @ Rs.%.2f = Rs.%.2f%n",
                fuelType, liters, pricePerLiter, fuelPayment);

        addToTransactionLog(String.format("FUEL | %s | %s | %.2fL | Rs.%.2f",
                plateNumber, fuelType, liters, fuelPayment));

        return fuelPayment;
    }

    // VEHICLE EXIT - Calculate payment and remove vehicle
    public void vehicleExit(String plateNumber)
            throws VehicleNotFoundException {

        Vehicle vehicle = activeVehicles.get(plateNumber);
        if (vehicle == null) {
            throw new VehicleNotFoundException("Vehicle with plate number " + plateNumber + " not found!");
        }

        vehicle.setExitTime(LocalDateTime.now());

        for (ParkingSlot slot : slots) {
            if (slot.isOccupied() && slot.getVehicle().getPlateNumber().equals(plateNumber)) {
                slot.removeVehicle();
                break;
            }
        }

        double parkingFee = vehicle.calculateParkingFee();
        double totalFuelCost = vehicle.getTotalFuelCost();

        PaymentCalculator.generateBill(vehicle, parkingFee, totalFuelCost, vehicle.getFuelTransactions());

        activeVehicles.remove(plateNumber);

        addToTransactionLog(String.format("EXIT | %s | Parking: Rs.%.2f | Fuel: Rs.%.2f | Total: Rs.%.2f",
                plateNumber, parkingFee, totalFuelCost, parkingFee + totalFuelCost));
    }

    // DISPLAY PARKING STATUS
    public void displayParkingStatus() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              PARKING SLOT STATUS");
        System.out.println("=".repeat(60));

        int occupiedCount = 0;
        for (ParkingSlot slot : slots) {
            System.out.println(slot.getSlotStatus());
            if (slot.isOccupied())
                occupiedCount++;
        }

        System.out.println("-".repeat(60));
        System.out.printf("Total Slots: %d | Occupied: %d | Available: %d%n",
                totalSlots, occupiedCount, totalSlots - occupiedCount);
        System.out.println("=".repeat(60));
    }

    // DISPLAY ALL ACTIVE VEHICLES
    public void displayActiveVehicles() {
        if (activeVehicles.isEmpty()) {
            System.out.println("\nNo vehicles currently in parking.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("                         ACTIVE VEHICLES");
        System.out.println("=".repeat(80));
        System.out.printf("| %-12s | %-12s | %-20s | %-25s |%n",
                "Type", "Plate No", "Entry Time", "Fuel Served");
        System.out.println("-".repeat(80));

        for (Vehicle vehicle : activeVehicles.values()) {
            System.out.println(vehicle);
        }
        System.out.println("=".repeat(80));
    }

    // UPDATE VEHICLE INFORMATION
    public void updateVehicleInfo(String plateNumber, String newPlateNumber)
            throws VehicleNotFoundException, InvalidInputException {

        Vehicle vehicle = activeVehicles.get(plateNumber);
        if (vehicle == null) {
            throw new VehicleNotFoundException("Vehicle not found!");
        }

        if (newPlateNumber == null || newPlateNumber.trim().isEmpty()) {
            throw new InvalidInputException("New plate number cannot be empty!");
        }

        if (activeVehicles.containsKey(newPlateNumber) && !plateNumber.equals(newPlateNumber)) {
            throw new InvalidInputException("New plate number already in use!");
        }

        activeVehicles.remove(plateNumber);
        vehicle.setPlateNumber(newPlateNumber);
        activeVehicles.put(newPlateNumber, vehicle);

        System.out.printf(" Vehicle plate number updated from %s to %s%n", plateNumber, newPlateNumber);
        addToTransactionLog(String.format("UPDATE | %s -> %s", plateNumber, newPlateNumber));
    }

    // DISPLAY TRANSACTION LOG
    public void displayTransactionLog() {
        if (transactionLog.isEmpty()) {
            System.out.println("\nNo transactions recorded.");
            return;
        }

        System.out.println("\n" + "=".repeat(70));
        System.out.println("                    TRANSACTION HISTORY");
        System.out.println("=".repeat(70));

        for (String log : transactionLog) {
            System.out.println(log);
        }
        System.out.println("=".repeat(70));
    }

    public FuelService getFuelService() {
        return fuelService;
    }

    public Map<String, Vehicle> getActiveVehicles() {
        return activeVehicles;
    }

    public List<ParkingSlot> getSlots() {
        return slots;
    }
}

// ==================== MAIN CLASS ====================

public class SmartParkingSystem {
    private static ParkingManager parkingManager;
    private static Scanner scanner;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        parkingManager = new ParkingManager(10);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("   WELCOME TO SMART PARKING MANAGEMENT SYSTEM");
        System.out.println("=".repeat(60));

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getIntInput("Enter your choice: ");

            try {
                switch (choice) {
                    case 1:
                        vehicleEntry();
                        break;
                    case 2:
                        vehicleExit();
                        break;
                    case 3:
                        provideFuelService();
                        break;
                    case 4:
                        parkingManager.displayParkingStatus();
                        break;
                    case 5:
                        parkingManager.displayActiveVehicles();
                        break;
                    case 6:
                        updateVehicleInfo();
                        break;
                    case 7:
                        parkingManager.getFuelService().displayFuelPrices();
                        break;
                    case 8:
                        updateFuelPrice();
                        break;
                    case 9:
                        parkingManager.displayTransactionLog();
                        break;
                    case 10:
                        System.out.println("\n Thank you for using Smart Parking System!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            } catch (Exception e) {
                System.out.println(" Error: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static void displayMenu() {
        System.out.println("\n" + "-".repeat(60));
        System.out.println("1. Vehicle Entry (Register & Assign Slot)");
        System.out.println("2. Vehicle Exit (Generate Bill)");
        System.out.println("3. Provide Fuel Service");
        System.out.println("4. View Parking Status");
        System.out.println("5. View Active Vehicles");
        System.out.println("6. Update Vehicle Information");
        System.out.println("7. View Fuel Prices");
        System.out.println("8. Update Fuel Price");
        System.out.println("9. View Transaction Log");
        System.out.println("10. Exit");
        System.out.println("-".repeat(60));
    }

    private static void vehicleEntry() {
        System.out.println("\n--- VEHICLE ENTRY ---");
        String plateNumber = getStringInput("Enter Plate Number: ");
        System.out.println("Vehicle Types: Car, Motorcycle, Bajaj");
        String vehicleType = getStringInput("Enter Vehicle Type: ");

        try {
            parkingManager.vehicleEntry(plateNumber, vehicleType);
        } catch (ParkingFullException | InvalidInputException e) {
            System.out.println(" " + e.getMessage());
        }
    }

    private static void vehicleExit() {
        System.out.println("\n--- VEHICLE EXIT ---");
        String plateNumber = getStringInput("Enter Plate Number: ");

        try {
            parkingManager.vehicleExit(plateNumber);
        } catch (VehicleNotFoundException e) {
            System.out.println(" " + e.getMessage());
        }
    }

    private static void provideFuelService() {
        System.out.println("\n--- FUEL SERVICE ---");
        String plateNumber = getStringInput("Enter Plate Number: ");

        if (!parkingManager.getActiveVehicles().containsKey(plateNumber)) {
            System.out.println(" Vehicle not found in parking area!");
            return;
        }

        parkingManager.getFuelService().displayFuelPrices();
        String fuelType = getStringInput("Enter Fuel Type (Petrol/Diesel/Oil): ");
        double liters = getDoubleInput("Enter Liters: ");

        try {
            double payment = parkingManager.provideFuelService(plateNumber, fuelType, liters);
            System.out.printf(" Fuel payment amount: Rs. %.2f%n", payment);
        } catch (VehicleNotFoundException | InvalidInputException e) {
            System.out.println(" " + e.getMessage());
        }
    }

    private static void updateVehicleInfo() {
        System.out.println("\n--- UPDATE VEHICLE INFO ---");
        String oldPlate = getStringInput("Enter Current Plate Number: ");
        String newPlate = getStringInput("Enter New Plate Number: ");

        try {
            parkingManager.updateVehicleInfo(oldPlate, newPlate);
        } catch (VehicleNotFoundException | InvalidInputException e) {
            System.out.println(" " + e.getMessage());
        }
    }

    private static void updateFuelPrice() {
        System.out.println("\n--- UPDATE FUEL PRICE ---");
        parkingManager.getFuelService().displayFuelPrices();
        String fuelType = getStringInput("Enter Fuel Type to update (Petrol/Diesel/Oil): ");
        double newPrice = getDoubleInput("Enter New Price per Liter: ");

        parkingManager.getFuelService().updateFuelPrice(fuelType, newPrice);
    }

    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        int input = scanner.nextInt();
        scanner.nextLine();
        return input;
    }

    private static double getDoubleInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextDouble()) {
            System.out.print("Invalid input. Please enter a number: ");
            scanner.next();
        }
        double input = scanner.nextDouble();
        scanner.nextLine();
        return input;
    }
}