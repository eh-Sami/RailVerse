package backend;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketFileHandler {
    public static List<Ticket> readTickets(String currfilename, String prevfilename, PassengerService passengerService, TrainService trainService) throws IOException {
        List<Ticket> ticketList = new ArrayList<>();
        List<Ticket> linesToKeep = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(currfilename));
        String line;
        while ((line = reader.readLine()) != null) {
            Ticket ticket = Ticket.fromCSV(line, passengerService);
            if (ticket != null) {
                LocalDateTime currtime = LocalDateTime.now();
                LocalDateTime trainTime = trainService.getTrainById(ticket.getTrainId()).getDepartureTime();

                if(ticket.getStatus().equalsIgnoreCase("Booked")){
                    ticket.setPassenger(passengerService.getPassengerById(ticket.getPassengerId()));
                }

                if(currtime.isBefore(trainTime)){
                    if(ticket.getStatus().equalsIgnoreCase("Cancelled")){
                        ticket.setStatus("Vacant");
                        ticket.setPassengerId(0);
                    }
                    ticketList.add(ticket);
                    linesToKeep.add(ticket);
                }
                else{
                    if(ticket.getStatus().equalsIgnoreCase("vacant") || ticket.getStatus().equalsIgnoreCase("cancelled")){
                        continue;
                    }
                    writeTicket(prevfilename, ticket);
                    System.out.println("triggered");
                }
            }
        }
        writeTickets(currfilename, linesToKeep);
        reader.close();
        return ticketList;
    }

    public static List<Ticket> readOldTickets(String filename, PassengerService passengerService) throws IOException {
        List<Ticket> ticketList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            Ticket ticket = Ticket.fromCSV(line, passengerService);
            if (ticket != null) {
                ticketList.add(ticket);
            }
        }
        reader.close();
        return ticketList;
    }

    public static void writeTickets(String filename, List<Ticket> ticketList) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (Ticket ticket : ticketList) {
            writer.write(ticket.toCSV());
            writer.newLine();
        }
        writer.close();
    }

    public static void writeTicket(String filename, Ticket ticket) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
        writer.write(ticket.toCSV());
        writer.newLine();
        writer.close();
    }

    public static void appendTickets(String filename, List<Ticket> ticketList) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
        for (Ticket ticket : ticketList) {
            writer.write(ticket.toCSV());
            writer.newLine();
        }
    }

    public static void appendTicket(String filename, Ticket ticket) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
        writer.write(ticket.toCSV());
        writer.newLine();
        writer.close();
    }

    public static void updateTicket(String filePath, String ticketId, String newLine) throws IOException {
        Path tempFile = Files.createTempFile(null, ".tmp");
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith(ticketId + ",")) {
                    writer.write(newLine);
                    writer.newLine();
                    updated = true;
                } else {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException ex) {
                System.err.println("Failed to delete temporary file: " + ex.getMessage());
            }
            throw e;
        }

        if (updated) {
            Files.move(tempFile, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Ticket " + ticketId + " updated successfully.");
        } else {
            Files.delete(tempFile);
            System.out.println("Ticket " + ticketId + " not found.");
        }
    }

        public static Ticket findTicketById(String filePath, String ticketId) throws IOException {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith(ticketId + ",")) {
                    return Ticket.fromCSV(line, null);
                }
            }
            reader.close();
            return null;
        }
}



