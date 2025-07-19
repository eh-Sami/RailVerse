package backend;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TrainOperatorFileHandler {
    public static List<TrainOperator> readTrainOperators(String filename, TrainService trainService, TicketMasterService ticketMasterService) throws IOException {
        List<TrainOperator> trainOperators = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        System.out.println("Trying to read file: " + filename);
        String line;
        while ((line = reader.readLine()) != null) {
            TrainOperator trainOperator = TrainOperator.fromCSV(line, trainService, ticketMasterService);
            if (trainOperator != null) {
                trainOperators.add(trainOperator);
            }
        }
        reader.close();
        return trainOperators;
    }
    public static void writeTrainOperators(String filename, List<TrainOperator> trainOperators) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (TrainOperator trainOperator : trainOperators) {
            writer.write(trainOperator.toCSV());
            writer.newLine();
        }
        writer.close();
    }

    public static void appendTrainOperator(String filename, TrainOperator trainOperator) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true));
        writer.write(trainOperator.toCSV());
        writer.newLine();
        writer.close();
    }
}
