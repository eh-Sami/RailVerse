package backend;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class TrainFileHandler {
    public static List<Train> readTrains(String filename, String ticketFilePath) throws IOException { // changed on 2025-7-18 to include ticketFilePath
        List<Train> trainList = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;

        while ((line = reader.readLine()) != null) {
            Train train = Train.fromCSV(line, filename, ticketFilePath); // changed on 2025-7-18 to include ticketFilePath
            if (train != null) {
                trainList.add(train);
            }
        }
        return trainList;
    }

    public static void writeTrains(String filename, List<Train> trainList) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (Train train : trainList) {
            writer.write(train.toCSV());
            writer.newLine();
        }
    }

    public static void appendTrains(String filename, List<Train> trainList) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
        for (Train train : trainList) {
            writer.write(train.toCSV());
            writer.newLine();
        }
    }

    public static void appendTrain(String filename, Train train) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
        writer.write(train.toCSV());
        writer.newLine();
        writer.close();
    }

    public static void updateTrain(String filePath, String trainId, String newLine) throws IOException {
        Path tempFile = Files.createTempFile(null, ".tmp");
        boolean updated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith(trainId + ",")) {
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
            System.out.println("Ticket " + trainId + " updated successfully.");
        } else {
            Files.delete(tempFile);
            System.out.println("Ticket " + trainId + " not found.");
        }
    }
    public static List<Train> findTrainByDestination(String trainFilePath, String ticketFilePath, String station, String destination, LocalDateTime dateTime) {
        List<Train> trainList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(trainFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Train train = Train.fromCSV(line, trainFilePath, ticketFilePath);
                if (train.getSource().equalsIgnoreCase(station) && train.getDestination().equalsIgnoreCase(destination) && train.getDepartureTime().isAfter(dateTime)) {
                    trainList.add(train);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return trainList;
    }


}


