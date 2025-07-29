package backend;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class PassengerFileHandler {
    public static List<Passenger> readPassengers(String filename, TrainService trainService) throws Exception{
        List<Passenger> passengerList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            Passenger passenger = Passenger.fromCSV(line, trainService, filename);
            if (passenger != null) {
                passengerList.add(passenger);
            }
        }
        reader.close();
        return passengerList;
    }

    public static void writePassengers(String filename, List<Passenger> passengerList) throws Exception{
        BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(filename));
        for (Passenger passenger : passengerList) {
            writer.write(passenger.toCSV());
            writer.newLine();
        }
        writer.close();
    }

    public static void appendPassengers(String filename, Passenger passenger) throws Exception{
        BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(filename, true));
        writer.write(passenger.toCSV());
        writer.newLine();
        writer.close();
    }

    public static void updatePassenger(String filePath, String passengerId, String newLine) throws IOException {
        Path tempFile = Files.createTempFile(null, ".tmp");
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith(passengerId + ",")) {
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
            System.out.println("Ticket " + passengerId + " updated successfully.");
        } else {
            Files.delete(tempFile);
            System.out.println("Ticket " + passengerId + " not found.");
        }
    }
}
