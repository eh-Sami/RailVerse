package backend;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class TicketMasterHandler {
    public static List<TicketMaster> readTicketMasters(String filename, TrainService trainService) throws IOException, IOException {
        List<TicketMaster> ticketMasters = new ArrayList<>();
        List<TicketMaster> ticketMasterstoEdit = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                TicketMaster tm = TicketMaster.fromCSV(line, trainService, filename);
                if (tm != null) {
                    LocalDateTime currtime = LocalDateTime.now();
                    LocalDateTime trainTime = tm.getAssignedTrain().getDepartureTime();
                    if(currtime.isAfter(trainTime)){
                        ticketMasterstoEdit.add(tm);
                    }
                    else {
                        ticketMasters.add(tm);
                    }
                }
            }
        }
        for(TicketMaster tm : ticketMasterstoEdit){
            tm.setAssignedTrain(trainService.getTrainById(0), "inactive");
            tm.setStatus("inactive");
            ticketMasters.add(tm);
        }
        return ticketMasters;
    }

    public static void writeTicketMasters(String filename, List<TicketMaster> ticketMasters) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (TicketMaster tm : ticketMasters) {
                writer.write(tm.toCSV());
                writer.newLine();
            }
        }
    }

    public static void appendTicketMaster(String filename, TicketMaster tm) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(tm.toCSV());
            writer.newLine();
        }
    }

    public static void updateTicketMaster(String filePath, String ticketMasterId, String newLine) throws IOException {
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
                if (line.trim().startsWith(ticketMasterId + ",")) {
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
            System.out.println("TicketMaster " + ticketMasterId + " updated successfully.");
        } else {
            Files.delete(tempFile);
            System.out.println("TicketMaster " + ticketMasterId + " not found.");
        }
    }
}
