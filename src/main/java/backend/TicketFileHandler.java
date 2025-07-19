package backend;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class TicketFileHandler {
    public static PassengerService passengerService = null;
    /**
     * Reads tickets from a CSV file and returns a list of Ticket objects.
     *
     * @param filename the name of the file to read from
     * @return a list of Ticket objects
     * @throws IOException if there is an error reading the file
     */
    public static List<Ticket> readTickets(String filename, PassengerService passengerService) throws IOException {
        List<Ticket> ticketList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            Ticket ticket = Ticket.fromCSV(line, passengerService);
            if (ticket != null) {
                ticketList.add(ticket);
            }
        }
        return ticketList;
    }
    /**
     * Writes(Overwrite) a list of Ticket objects to a CSV file.
     *
     * @param filename the name of the file to write to
     * @param ticketList the list of Ticket objects to write
     * @throws IOException if there is an error writing to the file
     */
    public static void writeTickets(String filename, List<Ticket> ticketList) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (Ticket ticket : ticketList) {
            writer.write(ticket.toCSV());
            writer.newLine();
        }
    }
    /**
     * Appends a list of Ticket objects to a CSV file.
     *
     * @param filename the name of the file to append to
     * @param ticketList the list of Ticket objects to append
     * @throws IOException if there is an error writing to the file
     */
    public static void appendTickets(String filename, List<Ticket> ticketList) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
        for (Ticket ticket : ticketList) {
            writer.write(ticket.toCSV());
            writer.newLine();
        }
    }
    /**
     * Appends a single Ticket object to a CSV file.
     *
     * @param filename the name of the file to append to
     * @param ticket the Ticket object to append
     * @throws IOException if there is an error writing to the file
     */

    public static void appendTicket(String filename, Ticket ticket) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
        writer.write(ticket.toCSV());
        writer.newLine();
        writer.close();
    }

    /**
     * Updates the information of a single ticket in a text file.
     *
     * @param filePath  Path to the text file containing ticket information.
     * @param ticketId  The unique ID of the ticket to update.
     * @param newLine   The new information for the ticket, formatted as a string (e.g., "12345,new_info1,new_info2,new_info3").
     * @throws IOException If an I/O error occurs during file operations.
     */
    public static void updateTicket(String filePath, String ticketId, String newLine) throws IOException {
        // Create a temporary file to store updated content
        Path tempFile = Files.createTempFile(null, ".tmp");
        boolean updated = false;

        // Use try-with-resources to automatically close reader and writer
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))) {
            String line;
            // Read each line from the original file
            while ((line = reader.readLine()) != null) {
                // Check if the line starts with ticketId followed by a comma, ignoring leading/trailing whitespace
                if (line.trim().startsWith(ticketId + ",")) {
                    writer.write(newLine); // Write the updated line
                    writer.newLine();
                    updated = true;
                } else {
                    writer.write(line); // Copy the original line unchanged
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            // Clean up temporary file on error and rethrow exception
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException ex) {
                System.err.println("Failed to delete temporary file: " + ex.getMessage());
            }
            throw e;
        }

        // If ticket was updated, replace original file; otherwise, delete temp file
        if (updated) {
            Files.move(tempFile, Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Ticket " + ticketId + " updated successfully.");
        } else {
            Files.delete(tempFile);
            System.out.println("Ticket " + ticketId + " not found.");
        }
    }

    // changed on Friday
    public static Ticket findTicketById(String filePath, String ticketId) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith(ticketId + ",")) {
                    return Ticket.fromCSV(line, passengerService);
                }
            }
        }
        return null; // Ticket not found
    }
}



