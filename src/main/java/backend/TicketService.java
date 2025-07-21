package backend;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class TicketService {
    private List<Ticket> ticketList;
    private String filename;

    /**
     * Constructor to initialize TicketService with a filename.
     * It reads existing tickets from the file.
     *
     * @param filename the name of the file containing ticket data
     * @throws IOException if there is an error reading the file
     */
    public TicketService(String filename, PassengerService passengerService) throws IOException {
        this.filename = filename;
        this.ticketList = TicketFileHandler.readTickets(filename, passengerService);
    }

    public List<Ticket> getAllTickets() {
        return ticketList;
    }

    private int generateNewTicketId(){
        int maxId = 0;
        for(Ticket ticket : ticketList){
            if(ticket.getTicketId() > maxId){
                maxId = ticket.getTicketId();
            }
        }
        return maxId + 1;
    }

    public boolean isSeatBooked(int trainId, String seatNumber){
        for(Ticket ticket : ticketList){
            if(ticket.getTrainId() == trainId && ticket.getSeatNumber().equalsIgnoreCase(seatNumber) && ticket.getStatus().equalsIgnoreCase("Booked")){
                return true;
            }
        }
        return false;
    }

//    public Ticket bookTicket(Passenger passenger, Train train, String seatNumber) throws IOException {
//        if(isSeatBooked(train.getId(), seatNumber)){
//            System.out.println("Seated seat is already booked!");
//            return null;
//        }
//
//        double baseFare = train.getBaseFare();
//        double fine = passenger.getFine();
//        double totalPrice = baseFare + fine;
//
//        System.out.println("Booking Summary:");
//        System.out.println("Train: " + train.getName());
//        System.out.println("Seat: " + seatNumber);
//        System.out.println("Base Fare: " + baseFare);
//        System.out.println("Outstanding Fine: " + fine);
//        System.out.println("Total Amount to Pay: " + totalPrice);
//
//        Ticket ticket = new Ticket(generateNewTicketId(), passenger.getId(), train.getId(), seatNumber, LocalDateTime.now(), "Booked", fine, totalPrice);
//        ticketList.add(ticket);
//        passenger.setFine(0);
//        TicketFileHandler.appendTicket(filename, ticket);
//        System.out.println("Ticket booked successfully!");
//        return ticket;
//    }

    public Ticket cancelTicket(int ticketId, int passengerId) throws IOException { // return type changed from boolean to Ticket
        boolean found = false;
        Ticket t = null;
        for(Ticket ticket : ticketList){
            if(ticket.getTicketId() == ticketId && ticket.getPassengerId() == passengerId){
                if(!ticket.getStatus().equalsIgnoreCase("Booked")){
                    System.out.println("Ticket is not booked yet!");
                    return null;
                }
                ticket.setStatus("Cancelled");
                found = true;
                t = ticket;
                break;
            }
        }
        if(found){
//            TicketFileHandler.writeTickets(filename, ticketList);
            TicketFileHandler.updateTicket(filename, ticketId + "", t.toCSV());

            System.out.println("Ticket cancelled successfully!" );
            return t;
        }
        System.out.println("Ticket not found!");
        return null;
    }

    public List<Ticket> getTicketsForUser(int passengerId){
        List<Ticket> myTickets = new ArrayList<>();
        for(Ticket ticket : ticketList){
            if(ticket.getPassengerId() == passengerId){
                myTickets.add(ticket);
            }
        }
        return myTickets;
    }

    public void printTicketForUser(int passengerId){
        List<Ticket> myTickets = getTicketsForUser(passengerId);
        if(myTickets.isEmpty()){
            System.out.println("No tickets found for this user!");
        }else{
            for(Ticket ticket : myTickets){
                System.out.println(ticket);
            }
        }
    }

    public void displaySeatMap(Train train){
        int rows = 6;
        int cols = 4;
        int trainId = train.getId();
        System.out.println("Seats of Train ID " + trainId + ": (" + rows + "x" + cols + ")");

        for(int r=0; r<rows; r++){
            char rowChar = (char)('A' + r);
            for(int c=1; c<=cols; c++){
                if((c-1)==cols/2){
                    System.out.print("  ");
                }
                String seat = rowChar + "" + c;
                if(isSeatBooked(trainId, seat)){
                    System.out.print("[X] ");
                }else{
                    System.out.print("[" + seat + "] ");
                }
            }
            System.out.println();
        }
    }
}
