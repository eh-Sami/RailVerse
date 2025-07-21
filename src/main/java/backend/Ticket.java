package backend;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket{
    private int ticketId;
    private int passengerId;
    private Passenger assignedPassenger;
    private int trainId;
    private String seatNumber;
    private LocalDateTime bookingDate;
    private String status; // status of the ticket: "Booked", "Cancelled", "Completed"
    private double fineAmount;
    private double price;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public Ticket(int ticketId, int passengerId, int trainId, String seatNumber, LocalDateTime bookingDate, String status, Double fine, Double price) {
        this.ticketId = ticketId;
//        this.assignedPassenger = passenger;

        this.passengerId = passengerId;
        this.trainId = trainId;
        this.seatNumber = seatNumber;
        this.bookingDate = bookingDate;
        this.fineAmount = fine;
        this.price = price;
        this.status = status;
    }

    /**
     * Getters and Setters for Ticket attributes.
     */
    public int getTicketId() {
        return ticketId;
    }
    public Passenger getPassenger() {
        return assignedPassenger;
    }
    public int getPassengerId() {
        return assignedPassenger != null ? assignedPassenger.getId() : passengerId;
    }
    public void setPassenger(Passenger passenger) {
        this.assignedPassenger = passenger;
        this.passengerId = passenger.getId();
    }
    public int getTrainId() {
        return trainId;
    }
    public double getPrice() {
        return price;
    }
    public String getSeatNumber() {
        return seatNumber;
    }
    public LocalDateTime getBookingDate() {
        return bookingDate;
    }
    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }
    public String getStatus() {
        return status;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    @Override
    public String toString(){
        String dateStr = (bookingDate != null) ? bookingDate.format(formatter) : "null";
        return "Ticket{" + "ticketId: " + ticketId + ", passengerId: " + passengerId + ", trainId: " + trainId + ", seatNumber: " + seatNumber + ", bookingDate: " + dateStr + ", status: " + status + ", fineAmount: " + fineAmount + ", price: " + price + '}';
    }

    /**
     * Converts the Ticket object to a CSV string format to be used for file storage.
     * @return CSV representation of the Ticket.
     */
    public String toCSV() {
        String dateStr = (bookingDate != null) ? bookingDate.format(formatter) : "null";
        return ticketId + "," + passengerId + "," + trainId + "," + seatNumber + ','
                + dateStr + "," + status + "," + fineAmount + "," + price;
    }

    /**
     * Parses a CSV string to create a Ticket object.
     * @param line CSV string representing a Ticket.
     * @return Ticket object or null if the input is invalid.
     */
    public static Ticket fromCSV(String line, PassengerService passengerService) {
        String[] parts = line.split(",");
        if (parts.length < 8) return null;
        int ticketId = Integer.parseInt(parts[0]);
        int passengerId = Integer.parseInt(parts[1]);
        int trainId = Integer.parseInt(parts[2]);
        String seatNumber = parts[3];
        LocalDateTime bookingDate;
        if(parts[4].equalsIgnoreCase("null")) {
            bookingDate = null;
        }
        else{
            bookingDate = LocalDateTime.parse(parts[4], formatter);
        }

        String status = parts[5];
        double fineAmount = Double.parseDouble(parts[6]);
        double price = Double.parseDouble(parts[7]);
        return new Ticket(ticketId, passengerId, trainId, seatNumber, bookingDate, status, fineAmount, price);
    }
}
