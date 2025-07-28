package backend;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Train {
    private final int Id;
    private final String name;

    private final String source;
    private final String destination;

    private List<Ticket[][]> compartments;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private String status;
    private double baseFare;

    private String trainFilePath; // changed on 2025-7-18 to use file paths
    private String ticketFilePath;  // changed on 2025-7-18 to use file paths

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    DateTimeFormatter formatterTwo = DateTimeFormatter.ofPattern("ddMMyy");


    private long createTicketId(int row, int col, int compartmentIndex) {
        // format : TrainId + compartment serial + seatNumber in that compartment
        LocalDate date = departureTime.toLocalDate();
        String formattedDate = date.format(formatterTwo);
        long dateLong = Long.parseLong(formattedDate);
        return dateLong * 1000000 + this.Id * 1000 + compartmentIndex * 100 + row * 4 + col + 1;
    }


    public Train(int id, String name, String source, String destination, LocalDateTime departureTime, LocalDateTime arrivalTime, String status, double fare, int compartmentCount, String trainFilePath, String ticketFilePath) throws IOException {
        Id = id;
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.status = status;
        baseFare = fare;
        this.trainFilePath = trainFilePath;
        this.ticketFilePath = ticketFilePath;
        System.out.println("Train " + name + " created with " + compartmentCount + " compartments." );
        compartments = new ArrayList<>();
        if(LocalDateTime.now().isBefore(departureTime)) {
            for (int i = 0; i < compartmentCount; i++) {
                Ticket[][] compartment = new Ticket[6][4]; // 6 rows, 4 columns

                for (int row = 0; row < 6; row++) {
                    for (int col = 0; col < 4; col++) {
                        // Create a new Ticket object for each seat
                        String ticketId = String.valueOf(createTicketId(row, col, i));
                        Ticket ticket = TicketFileHandler.findTicketById(ticketFilePath, ticketId);
                        System.out.println(ticketId);

                        if (ticket == null) {
                            // Otherwise, create a new Ticket object)
                            compartment[row][col] = new Ticket(createTicketId(row, col, i), 0, this.Id, "" + (char) ('A' + i) + (row * 4 + col + 1), null, "Vacant", 0.0, 100.0); // Initialize with null
                            // Append the ticket to the file
                            System.out.println("inside if" + (char)('A' + i));
                            TicketFileHandler.appendTicket(ticketFilePath, compartment[row][col]);
                        } else {
                            compartment[row][col] = ticket;
                        }
                    }
                }
                // Add the compartment to the compartments list
                compartments.add(compartment);
            }
        }
    }

    public int getId() {
        return Id;
    }
    public String getName() {
        return name;
    }
    public String getSource() {
        return source;
    }
    public String getDestination() {
        return destination;
    }
    double getBaseFare(){
        return baseFare;
    }
    public LocalDateTime getDepartureTime() {
        return departureTime;
    }
    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) throws IOException {
        this.status = status;
        try {
            TrainFileHandler.updateTrain(trainFilePath, String.valueOf(Id), this.toCSV());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
        try {
            TrainFileHandler.updateTrain(trainFilePath, String.valueOf(Id), this.toCSV());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
    public int getCompartmentCount() {
        return compartments.size();
    }

    public boolean isValidTicket(Object t) {
        for (Ticket[][] compartment : compartments) {
            for (Ticket[] row : compartment) {
                for (Ticket ticket : row) {
                    if(t instanceof Integer) {
                        if (ticket.getTicketId() == (Integer) t && ticket.getStatus().equalsIgnoreCase("Booked")) {
                            return true;
                        }
                    } else if (t instanceof Ticket) {
                        if (ticket.getTicketId() == ((Ticket) t).getTicketId() && ticket.getStatus().equalsIgnoreCase("Booked")) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public Ticket bookTicket(Passenger passenger, long ticketId) {
        for (Ticket[][] compartment : compartments) {
            for (Ticket[] row : compartment) {
                for (Ticket ticket : row) {
                    if (ticket.getTicketId() == ticketId && (ticket.getStatus().equalsIgnoreCase("Vacant") || ticket.getStatus().equalsIgnoreCase("Cancelled"))) {
                        ticket.setStatus("Booked");
                        ticket.setBookingDate(LocalDateTime.now());
                        ticket.setPassenger(passenger);
                        ticket.setPrice(baseFare);
//                        passenger.addTicket(ticket); // Add ticket to passenger's list

                        try {
                            TicketFileHandler.updateTicket(ticketFilePath, String.valueOf(ticket.getTicketId()), ticket.toCSV());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        return ticket; // Return the booked ticket
                    }
                }
            }
        }
        return null; // Seat not found or already booked
    }

    public boolean cancelTicket(Object t) {
        for (Ticket[][] compartment : compartments) {
            for (Ticket[] row : compartment) {
                for (Ticket ticket : row) {
                    if(t instanceof Integer) {
                        if (ticket.getTicketId() == (Long) t && ticket.getStatus().equalsIgnoreCase("Booked")) {
                            ticket.setStatus("Cancelled");
                            ticket.getPassenger().cancelTicket(ticket.getTicketId());
                            try {
                                TicketFileHandler.updateTicket(ticketFilePath, String.valueOf(ticket.getTicketId()), ticket.toCSV());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            return true;
                        }
                    } else if (t instanceof Ticket) {
                        if (ticket.getTicketId() == ((Ticket) t).getTicketId() && ticket.getStatus().equalsIgnoreCase("Booked")) {
                            ticket.setStatus("Cancelled");
                            ticket.getPassenger().cancelTicket(ticket);
                            try {
                                TicketFileHandler.updateTicket(ticketFilePath, String.valueOf(ticket.getTicketId()), ticket.toCSV());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public List<Ticket> getAllTickets() {
        List<Ticket> ticketList = new ArrayList<>();
        for (Ticket[][] compartment : compartments) {
            for (Ticket[] row : compartment) {
                for (Ticket ticket : row) {
                    ticketList.add(ticket);
                }
            }
        }
        return ticketList;
    }

    public List<Ticket> getAllOwnedTickets() {
        List<Ticket> ticketList = new ArrayList<>();
        for (Ticket[][] compartment : compartments) {
            for (Ticket[] row : compartment) {
                for (Ticket ticket : row) {
                    if(ticket.getStatus().equalsIgnoreCase("booked"))
                    {
                        ticketList.add(ticket);
                    }
                }
            }
        }
        return ticketList;
    }

    public String toString() {
        return "Train{" +
                "Id=" + Id +
                ", name=" + name +
                ", source=" + source +
                ", destination=" + destination +
                ", departureTime=" + departureTime.format(formatter) +
                ", arrivalTime=" + arrivalTime.format(formatter) +
                ", status=" + status +
                ", Base Fare=" + baseFare +
                ", compartments=" + compartments.size() +
                '}';
    }

    public String toCSV() {
        return Id + "," +
                name + "," +
                source + "," +
                destination + "," +
                departureTime.format(formatter) + "," +
                arrivalTime.format(formatter) + "," +
                status + "," +
                baseFare + "," +
                compartments.size();
    }

    public String getFrom() { return source; }
    public String getTo() { return destination; }

    public String getDepartureTimeString() {
        return departureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
    // Factory: Create Train object from CSV line
    public static Train fromCSV(String line, String trainFilePath, String ticketFilePath) {
        if (line == null || line.isEmpty()) return null;

        // Parse the CSV line
//        String[] parts = line.split(",");
//        if (parts.length < 10) return null;
//
//        int id = Integer.parseInt(parts[0]);
//        String name = parts[1];
//        String source = parts[2];
//        String destination = parts[3];
//        int rows = Integer.parseInt(parts[4]);
//        int cols = Integer.parseInt(parts[5]);
//        LocalDateTime departure = LocalDateTime.parse(parts[6], formatter);
//        LocalDateTime arrival = LocalDateTime.parse(parts[7], formatter);
//        String status = parts[8];
//        Double fare = Double.parseDouble(parts[9]);
//
//        return new Train(id, name, source, destination, rows, cols, departure, arrival, status, fare);

        String[] parts = line.split(",");
        if (parts.length < 9) return null;

        int id = Integer.parseInt(parts[0]);
        String name = parts[1];
        String source = parts[2];
        String destination = parts[3];
        LocalDateTime departure = LocalDateTime.parse(parts[4], formatter);
        LocalDateTime arrival = LocalDateTime.parse(parts[5], formatter);
        String status = parts[6];
        double fare = Double.parseDouble(parts[7]);
        int compartmentCount = Integer.parseInt(parts[8]);

        try {
            return new Train(id, name, source, destination, departure, arrival, status, fare, compartmentCount, trainFilePath, ticketFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getTrainId() {
        return Id + "";
    }


    public double getPrice() {
        return baseFare;
    }

}
