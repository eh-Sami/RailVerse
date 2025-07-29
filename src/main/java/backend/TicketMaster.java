package backend;


import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class TicketMaster extends User {
    private Train assignedTrain;
    private String status;
    String ticketMasterFilePath;

    public TicketMaster(int id, String name, String nid, String email, String address, Train assignedTrain, String password, String status, String ticketMasterFilePath) {
        super(id, name, nid, email, address, password);
        this.assignedTrain = assignedTrain;
        this.status = status;
        this.ticketMasterFilePath = ticketMasterFilePath;
    }

    public String getStatus(){
        return status;
    }

    public String getRole(){
        return "TicketMaster";
    }
    public Train getAssignedTrain() {
        return assignedTrain;
    }
    public void setAssignedTrain(Train assignedTrain, String status) throws IOException {
        this.assignedTrain = assignedTrain;
        this.status = status;
        try {
            TicketMasterHandler.updateTicketMaster(ticketMasterFilePath, String.valueOf(this.id), this.toCSV());
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
    public boolean available() {
        return status.equalsIgnoreCase("inactive");
    }
    public void setStatus(String status) throws IOException {
        this.status = status;
        try {
            TicketMasterHandler.updateTicketMaster(ticketMasterFilePath, String.valueOf(this.id), this.toCSV());
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    @Override
    public String toString() {
        return "TicketMaster{" + "id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + '}';
    }

    public boolean
    validateTicket(Ticket ticket) {
//        List<Ticket> tickets = ticketService.getAllTickets();
//        for (Ticket t : tickets) {
//            if (t.getTicketId() == ticketId && t.available().equalsIgnoreCase("Booked")) {
//                System.out.println("Ticket is valid: " + t);
//                return true;
//            }
//        }
//        System.out.println("Ticket is invalid or cancelled.");
//        return false;

        if (assignedTrain == null) {
            System.out.println("No train assigned to validate tickets.");
            return false;
        }

        boolean isValid = assignedTrain.isValidTicket(ticket);
        if (isValid) {
            System.out.println("Ticket ID " + ticket + " is valid.");
        } else {
            System.out.println("Ticket ID " + ticket + " is invalid or cancelled.");
        }
        return isValid;
    }

    public void cancelTicket(Object ticket) {
        if (assignedTrain == null) {
            System.out.println("No train assigned to cancel tickets.");
            return;
        }
        boolean cancelled = assignedTrain.cancelTicket(ticket);
        if (cancelled) {
            System.out.println("Ticket " + ticket + " cancelled successfully.");
        } else {
            System.out.println("Ticket " + ticket + " could not be cancelled or does not exist.");
        }

    }

    public boolean addFineToPassenger(Passenger passenger, double fineAmount) {
        if (fineAmount <= 0) {
            System.out.println("Fine amount must be positive.");
            return false;
        }
        double oldFine = passenger.getFine();
        passenger.setFine(oldFine + fineAmount);
        System.out.println("Fine added successfully. Total fine now: " + passenger.getFine());
        return true;
    }

//    public void viewAllTickets(TicketService ticketService) {
//        List<Ticket> tickets = ticketService.getAllTickets();
//        if (tickets.isEmpty()) {
//            System.out.println("No tickets found.");
//            return;
//        }
//        for (Ticket t : tickets) {
//            System.out.println(t);
//        }
//    }

    public List<Ticket> viewAllTickets() {
        if (assignedTrain == null) {
            System.out.println("No train assigned to view tickets.");
            return null;
        }
        System.out.println("Tickets for Train ID " + assignedTrain.getId() + ":");
        List<Ticket> tickets = assignedTrain.getAllTickets();
        for (Ticket t : tickets) {
            System.out.println(t);
        }
        return tickets;
    }

    public void changeName(String name){
        this.name = name;
        try {
            TicketMasterHandler.updateTicketMaster(ticketMasterFilePath, String.valueOf(this.id), this.toCSV());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void changeAddress(String address){
        this.address = address;
        try {
            TicketMasterHandler.updateTicketMaster(ticketMasterFilePath, String.valueOf(this.id), this.toCSV());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    public boolean changePassword(String oldPassword, String newPassword) {
        if (!User.passwordValidityCheck(oldPassword) || !oldPassword.equals(this.password)) {
            System.out.println("Old password is incorrect.");
            return false;
        }
        if(!User.passwordValidityCheck(newPassword)) {
            System.out.println("New password must be at least 8 characters long.");
            return false;
        }
        return updatePassword(newPassword);
    }

    public boolean updatePassword(String newPassword) {
        if (!User.passwordValidityCheck(newPassword)) {
            System.out.println("Invalid password format!");
            return false;
        }
        this.password = newPassword;
        System.out.println("Password updated successfully.");
        try {
            TicketMasterHandler.updateTicketMaster(ticketMasterFilePath, String.valueOf(this.id), this.toCSV());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public String toCSV() {
        return id + "," + name + "," + nid + "," + email + "," + password + "," + assignedTrain.getId() + "," + status + "," + address;
    }

    public static TicketMaster fromCSV(String line, TrainService trainService, String ticketMasterFilePath) {
//        String[] parts = line.split(",");
//        if (parts.length < 4) return null;
//        try {
//            int id = Integer.parseInt(parts[0]);
//            String name = parts[1];
//            String nid = parts[2]; // Assuming nid is part of the CSV
//            String email = parts[3];
//
//            String password = parts[3];
//            return new TicketMaster(id, name, nid, email, password);
//        } catch (Exception e) {
//            System.out.println("Error parsing TicketMaster from CSV: " + e.getMessage());
//            return null;
//        }

        String[] parts = line.split(",");
        if (parts.length < 8) return null;
        try {
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            String nid = parts[2];
            String email = parts[3];
            String password = parts[4];
            String status = parts[6];
            String address = "";
            for (int i = 7; i < parts.length; i++) {
                address += parts[i] + ",";
            }
            Train assignedTrain = trainService.getTrainById(Integer.parseInt(parts[5])); // You may need to fetch the Train object properly
            return new TicketMaster(id, name, nid, email, address, assignedTrain, password, status, ticketMasterFilePath);
        } catch (Exception e) {
            System.out.println("Error parsing TicketMaster from CSV: " + e.getMessage());
            return null;
        }
    }

}
