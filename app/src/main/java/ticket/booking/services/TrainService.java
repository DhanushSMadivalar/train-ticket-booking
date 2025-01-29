package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TrainService {

    private List<Train> trainList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String TRAIN_DB_PATH = "app/src/main/java/ticket/booking/localDb/trains.json";

    public TrainService() throws IOException {
        File trains = new File(TRAIN_DB_PATH);
        if (!trains.exists() || trains.length() == 0) {
            trainList = new ArrayList<>();
        } else {
            trainList = objectMapper.readValue(trains, new TypeReference<List<Train>>() {});
        }

    }

    public void saveTrainToFile() {
        try {
            objectMapper.writeValue(new File(TRAIN_DB_PATH), trainList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addTrain(Train newTrain) {
        trainList.add(newTrain);
        saveTrainToFile();
    }

    public boolean validTrain(Train train, String source, String destination) {
        List<String> stationOrder = train.getStation();
        int sourceIndex = stationOrder.indexOf(source.toLowerCase());
        int destinationIndex = stationOrder.indexOf(destination.toLowerCase());
        return sourceIndex != -1 && destinationIndex != -1 && sourceIndex < destinationIndex;
    }

    public List<Train> fetchTrain(String source, String destination) {
        if(source.isEmpty() && destination.isEmpty()){
            return trainList;
        }
        return trainList.stream()
                .filter(train -> validTrain(train, source, destination))
                .collect(Collectors.toList());
    }

    public List<List<Integer>> viewSeats(Train train) {
        return train.getSeats();
    }

    public void updateTrainSeats(Train train) {
        saveTrainToFile();
    }
}
