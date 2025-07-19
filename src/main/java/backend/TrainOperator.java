package backend;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class TrainOperator extends User {
    private TicketMasterService ticketMasterService;
    private Train assignedTrain;
    private TicketMaster assignedTicketMaster;

    public TrainOperator(int id, String name, String nid, String email, String address, String password, Train train, TicketMasterService ticketMasterService) {
        super(id, name, nid, email, address, password);
        this.assignedTrain = train;
        this.ticketMasterService = ticketMasterService;
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public String getRole(){
        return "TrainOperator";
    }

    public void setTrainStatus(String status){
        try {
            assignedTrain.setStatus(status);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Train status set to " + status);
    }

    public void changeDepartureTime(LocalDateTime newTime) {
        assignedTrain.setDepartureTime(newTime);
        System.out.println("Departure time updated to: " + newTime);
    }

    public Train getAssignedTrain() {
        return assignedTrain;
    }

    public void assignTicketMaster() throws IOException {
        for(TicketMaster tm : ticketMasterService.getAllTicketMasters()) {
            if (tm.available()) {
                tm.setAssignedTrain(assignedTrain);
                assignedTicketMaster = tm;
                System.out.println("Ticket Master " + tm.getName() + " assigned to Train " + assignedTrain.getName());
                return;
            }
        }
    }

    public void viewSeatsOccupied(Train train, TicketService ticketService) {
        int booked = 0;
        int total = 6 * 4 * train.getCompartmentCount(); // 6 rows, 4 columns per compartment

        for (Ticket t : ticketService.getAllTickets()) {
            if (t.getTrainId() == train.getId() && t.getStatus().equalsIgnoreCase("Booked")) {
                booked++;
            }
        }
        System.out.println("Train: " + train.getName());
        System.out.println("Booked Seats: " + booked + "/" + total);
    }

    public void cancelTicket(Object ticket) {
        assignedTicketMaster.cancelTicket(ticket);
    }

    public String toString(){
        return String.format("TrainOperator{id=%d, name=%s, nid=%s, email=%s, train=%s}", id, name, nid, email, assignedTrain != null ? assignedTrain.getName() : "None");
    }

    public String toCSV() {
        return id + "," + name + "," + nid + "," + email + "," + password + "," + assignedTrain.getId() + "," + address;
    }

    public static TrainOperator fromCSV(String line, TrainService trainService, TicketMasterService ticketMasterService) {
//        String[] parts = line.split(",");
//        if (parts.length != 4) return null;
//        int id = Integer.parseInt(parts[0]);
//        String name = parts[1];
//        String email = parts[2];
//        String password = parts[3];
//        return new TrainOperator(id, name, email, password);


        String[] parts = line.split(",");
        if (parts.length < 6) return null;
        int id = Integer.parseInt(parts[0]);
        String name = parts[1];
        String nid = parts[2];
        String email = parts[3];
        String password = parts[4];
        Train train = trainService.getTrainById(Integer.parseInt(parts[5]));
        String address = "";
        for(int i = 6; i < parts.length; i++) {
            address += parts[i] + ",";
        }
        return new TrainOperator(id, name, nid, email, address, password, train, ticketMasterService);
    }

    public void dashboard(TrainService trainService, TicketService ticketService) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("TrainOperator Dashboard: ");
            System.out.println("1. Set Train Status (On Time, Delayed, Cancelled)");
            System.out.println("2. Change Train Departure Time");
            System.out.println("3. View Seats Occupied for a Train");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1": // Set train status
                    System.out.print("Enter Train ID: ");
                    int trainId1 = Integer.parseInt(scanner.nextLine());
                    Train train1 = trainService.getTrainById(trainId1);
                    if (train1 == null) {
                        System.out.println("Train not found.");
                        break;
                    }
                    System.out.print("Enter new status (On Time/Delayed/Cancelled): ");
                    String status = scanner.nextLine();
                    setTrainStatus(status);
                    trainService.addTrain(train1); // update file
                    break;

                case "2": // Change departure time
                    System.out.print("Enter Train ID: ");
                    int trainId2 = Integer.parseInt(scanner.nextLine());
                    Train train2 = trainService.getTrainById(trainId2);
                    if (train2 == null) {
                        System.out.println("Train not found.");
                        break;
                    }
                    System.out.print("Enter new departure time (yyyy-MM-dd HH:mm): ");
                    String dtStr = scanner.nextLine();
                    try {
                        LocalDateTime newTime = LocalDateTime.parse(dtStr, formatter);
                        changeDepartureTime(newTime);
                        trainService.addTrain(train2); // update file
                    } catch (Exception e) {
                        System.out.println("Invalid date/time format.");
                    }
                    break;

                case "3": // View compartments occupied
                    System.out.print("Enter Train ID: ");
                    int trainId3 = Integer.parseInt(scanner.nextLine());
                    Train train3 = trainService.getTrainById(trainId3);
                    if (train3 == null) {
                        System.out.println("Train not found.");
                        break;
                    }
                    viewSeatsOccupied(train3, ticketService);
                    break;

                case "4": // Logout
                    System.out.println("Logging out...");
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

}
