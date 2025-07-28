package backend;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class TicketService {
    private List<Ticket> ticketList;
    private List<Ticket> prevTicketList;
    private String currfilename;
    private String prevfilename;
    TrainService trainService;
    PassengerService passengerService;

    /**
     * Constructor to initialize TicketService with a filename.
     * It reads existing tickets from the file.
     *
     * @param currentfilename the name of the file containing ticket data
     * @throws IOException if there is an error reading the file
     */
    public TicketService(String currentfilename, String previousfilename, PassengerService passengerService, TrainService trainService) throws IOException {
        this.currfilename = currentfilename;
        this.prevfilename = previousfilename;
        this.trainService = trainService;
        this.passengerService = passengerService;
        this.ticketList = TicketFileHandler.readTickets(currfilename, prevfilename, passengerService, trainService);
        this.prevTicketList = TicketFileHandler.readOldTickets(prevfilename, passengerService);
    }

    public List<Ticket> getAllTickets() {
        return ticketList;
    }
    public List<Ticket> getAllPrevTickets() {return prevTicketList; }
    private long generateNewTicketId(){
        long maxId = 0;
        for(Ticket ticket : ticketList){
            if(ticket.getTicketId() > maxId){
                maxId = ticket.getTicketId();
            }
        }
        return maxId + 1;
    }

    public void reloadTicketsFromFile() {
        try {
            ticketList = TicketFileHandler.readTickets(currfilename, prevfilename, passengerService, trainService);
            prevTicketList = TicketFileHandler.readOldTickets(prevfilename, passengerService);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean isSeatBooked(int trainId, String seatNumber){
        for(Ticket ticket : ticketList){
            if(ticket.getTrainId() == trainId && ticket.getSeatNumber().equalsIgnoreCase(seatNumber) && ticket.getStatus().equalsIgnoreCase("Booked")){
                return true;
            }
        }
        return false;
    }

    public Ticket getTicketById(long id){
        for(Ticket ticket : ticketList){
            if(ticket.getTicketId() == id){
                return ticket;
            }
        }
        return null;
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

    public Ticket cancelTicket(long ticketId, int passengerId) throws IOException { // return type changed from boolean to Ticket
        boolean found = false;
        Ticket t = null;
        for(Ticket ticket : ticketList){
            if(ticket.getTicketId() == ticketId && ticket.getPassengerId() == passengerId){
                if(!ticket.getStatus().equalsIgnoreCase("Booked")){
                    System.out.println("Ticket is not booked yet!");
                    return null;
                }
                ticket.setStatus("Vacant");
                ticket.setPassengerId(0);
                ticket.setBookingDate(null);
                ticket.setPrice(100);
                found = true;
                t = ticket;
                break;
            }
        }
        if(found){
//            TicketFileHandler.writeTickets(filename, ticketList);
            TicketFileHandler.updateTicket(currfilename, ticketId + "", t.toCSV());
//            TicketFileHandler.writeTickets(currfilename, ticketList);
            reloadTicketsFromFile();
            saveAllTickets();


            System.out.println("Ticket cancelled successfully!" );
            return t;
        }
        System.out.println("Ticket not found!");
        return null;
    }


    public void saveAllTickets() {
        try {
            TicketFileHandler.writeTickets(currfilename, ticketList);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public List<Ticket> getPrevTicketList(int passengerId){
        List<Ticket> prevTickets = new ArrayList<>();
        for(Ticket ticket : prevTicketList){
            if(ticket.getPassengerId() == passengerId){
                prevTickets.add(ticket);
            }
        }
        return prevTickets;
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
