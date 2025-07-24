package backend;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Passenger extends User{
    private TrainService trainService; // to see available trains and book tickets
    private List<Ticket> myTickets = new ArrayList<>(); // storing current tickets
    private List<Ticket> previousTickets = new ArrayList<>(); // storing previous tickets history
    private double fine = 0;
    String passengerFilePath;

    public Passenger(int id, String name, String nid, String email, String address, String password, double fine, TrainService trainService, String passengerFilePath) {
        super(id, name, nid, email, address, password);
        this.fine = fine;
        this.trainService = trainService;
        this.passengerFilePath = passengerFilePath;
    }
    
    public String getRole(){
        return "Passenger";
    }
    public double getFine() {
        return fine;
    }
    public void addFine(double fine){
        this.fine += fine;
    }
    public void setFine(double amount){
        this.fine = amount;
    }

    @Override
    public String getAddress() {
        return super.getAddress();
    }

    public void setAdress(String address){
        this.address = address;
    }

    @Override
    public String toString() {
        return "Passenger{" + "id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + '}';
    }

    public void viewAvailableTrains(List<Train> trainList, String from, String to){
        System.out.println("Available Trains: ");
        for(Train train : trainList){
            System.out.println(train);
        }
    }

    public void bookTicket(Object t, Ticket ticket){
        if(t instanceof Train) {
            Train selectedTrain = (Train) t;
            if (selectedTrain == null) {
                System.out.println("Train not found for the ticket.");
                return;
            }
            if (selectedTrain.bookTicket(this, ticket.getTicketId()) != null) {
                myTickets.add(ticket);
                System.out.println("Ticket booked successfully for Train ID " + selectedTrain.getId() + ".");
            } else {
                System.out.println("Failed to book ticket.");
            }
        } else {
            System.out.println("Invalid input for booking ticket.");
        }
    }

    public  List<Ticket> viewPastTickets(TicketService ticketService, int passengerId){
        previousTickets = ticketService.getPrevTicketList(passengerId);
        return previousTickets;
    }

    public List<Ticket> viewTickets(TicketService ticketService, int passengerId) {
//        ticketService.printTicketForUser(id);
        // added by sami
        myTickets = ticketService.getTicketsForUser(passengerId);
        return myTickets;
    }

    /**
     * Adds a ticket to the previous tickets list and removes it from the current tickets list.
     * This is useful for maintaining a history of trips that have been completed.
     * @param ticket The ticket to be added to previous tickets.
     * @return true if the ticket was successfully added, false otherwise.
     */
    public boolean completedTrip(Ticket ticket) {
        if (ticket != null) {
            myTickets.remove(ticket); // remove from current tickets
            previousTickets.add(ticket); // add to previous tickets
            return true;
        }
        return false;
    }

    public void cancelTicket(Object t){
//        Scanner sc = new Scanner(System.in);
//        System.out.println("Enter Ticket Id to cancel: ");
//        ticketId = sc.nextInt();
//        sc.nextLine();
//
//        System.out.println("Cancelling ticket with ID " + ticketId + "...");
//        try{
//            Ticket t = ticketService.cancelTicket(ticketId, id);
//            if(t != null) {
//                myTickets.remove(t);
//            }
//        }
//        catch(Exception e){
//            System.out.println("error " + e.getMessage());
//        }

        if (t instanceof Integer) {
            int ticketId = (Integer) t;
            Ticket ticketToCancel = null;
            for (Ticket ticket : myTickets) {
                if (ticket.getTicketId() == ticketId) {
                    ticketToCancel = ticket;
                    break;
                }
            }
            if (ticketToCancel != null) {
                ticketToCancel.setStatus("vacant");
                ticketToCancel.setPassengerId(0);
                int trainId = ticketToCancel.getTrainId();
                Train train = trainService.getTrainById(trainId);
                train.cancelTicket(ticketId); // assuming this method exists in TrainService
                myTickets.remove(ticketToCancel);
                System.out.println("Ticket with ID " + ticketId + " cancelled successfully.");
            } else {
                System.out.println("Ticket with ID " + ticketId + " not found.");
            }
        } else if (t instanceof Ticket) {
            Ticket ticketToCancel = (Ticket) t;
            if (myTickets.contains(ticketToCancel)) {
                ticketToCancel.setStatus("Cancelled");
                int trainId = ticketToCancel.getTrainId();
                Train train = trainService.getTrainById(trainId);
                train.cancelTicket(ticketToCancel);
                myTickets.remove(ticketToCancel);
                System.out.println("Ticket with ID " + ticketToCancel.getTicketId() + " cancelled successfully.");
            } else {
                System.out.println("Ticket with ID " + ticketToCancel.getTicketId() + " not found in current tickets.");
            }
        } else {
            System.out.println("Invalid input for cancelling ticket.");
        }
    }
// okay - aditya

        public boolean updatePassword(String newPassword) throws IOException {
        // not necessary. validity is checked earlier. keeping anyway
        this.password = newPassword;
        try {
            PassengerFileHandler.updatePassenger(passengerFilePath, String.valueOf(this.id), toCSV());
        } catch (IOException e) {
            throw new IOException(e);
        }
        return true;
    }

    public boolean updateAddress(String newAddress) throws IOException {
        this.address = newAddress;
        try {
            PassengerFileHandler.updatePassenger(passengerFilePath, String.valueOf(this.id), toCSV());
        } catch (IOException e) {
            throw new IOException(e);
        }
        return true;
    }

    public boolean updateEmail(String newEmail) throws IOException {
        this.email = newEmail;
        try {
            PassengerFileHandler.updatePassenger(passengerFilePath, String.valueOf(this.id), toCSV());
        } catch (IOException e) {
            throw new IOException(e);
        }
        return true;
    }

//    public void editProfile(String name, String address) throws IOException {
//        if (name != null && !name.isEmpty()) {
//            this.name = name;
//        }
//        if (address != null && !address.isEmpty()) {
//            this.address = address;
//        }
//        try {
//            PassengerFileHandler.updatePassenger(passengerFilePath, String.valueOf(this.id), toCSV());
//        } catch (IOException e) {
//            throw new IOException("passenger not found");
//        }
//    }

    public String toCSV() {
        return id + "," + name + "," + nid + "," + email + "," + password + "," + fine + "," + address; // changed
    }

    public static Passenger fromCSV(String line, TrainService trainService, String passengerFilePath) { // changed
        String[] parts = line.split(",");
        if (parts.length < 7) return null;
        int id = Integer.parseInt(parts[0]);
        String name = parts[1];
        String nid = parts[2];
        String email = parts[3];
        String password = parts[4];
        double fine = Double.parseDouble(parts[5]);
        String address = "";
        for(int i = 6; i < parts.length; i++) {
            address += parts[i] + ",";
        }
        return new Passenger(id, name, nid, email, address, password, fine, trainService, passengerFilePath);
    }
}


