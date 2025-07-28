package backend;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TrainOperator extends User {
    private TicketMasterService ticketMasterService;
    private Train assignedTrain;
    private TicketMaster assignedTicketMaster;
    private TrainService trainService;


    public TrainOperator(int id, String name, String nid, String email, String address, String password, Train train, TrainService trainService, TicketMasterService ticketMasterService) {
        super(id, name, nid, email, address, password);
        this.assignedTrain = train;
        this.ticketMasterService = ticketMasterService;
        this.trainService = trainService;
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public String getRole(){
        return "TrainOperator";
    }

    public void changeName(String name){
        this.name = name;
    }
    public void changePassword(String pass){
        this.password = pass;
    }
    public void changeAddress(String address){
        this.address = address;
    }
    public void setTrainStatus(String status, Train train){
        try {
            train.setStatus(status);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Train status set to " + status);
    }

    public void changeDepartureTime(LocalDateTime newTime, Train train) {
        train.setDepartureTime(newTime);
        System.out.println("Departure time updated to: " + newTime);
    }

    public Train getAssignedTrain() {
        return assignedTrain;
    }

    public void assignTicketMaster(TicketMaster tm, int trainId, String status) throws IOException {
                Train assignedTrain = trainService.getTrainById(trainId);
                tm.setAssignedTrain(assignedTrain, status);
                assignedTicketMaster = tm;
                System.out.println("Ticket Master " + tm.getName() + " assigned to Train " + assignedTrain.getName());
                return;
    }

    public List<Ticket> viewSeatsOccupied(Train train, TicketService ticketService){
        List<Ticket> bookedTikcets = new ArrayList<>();
        for (Ticket t : ticketService.getAllTickets()) {
            if (t.getTrainId() == train.getId() && t.getStatus().equalsIgnoreCase("Booked")) {
                bookedTikcets.add(t);
            }
        }
        return bookedTikcets;
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
        return new TrainOperator(id, name, nid, email, address, password, train, trainService,ticketMasterService);
    }
}
