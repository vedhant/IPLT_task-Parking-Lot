package com.example.ParkingLot.service;

import com.example.ParkingLot.dao.CarDao;
import com.example.ParkingLot.dao.ParkingQueueDao;
import com.example.ParkingLot.model.Car;
import com.example.ParkingLot.model.ParkingQueue;
import com.example.ParkingLot.utils.ParkingPriorityQueueComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ParkingLotService {

    private final ParkingQueueDao parkingQueueDao;
    private final CarDao carDao;
    private QueueService queueService;

    @Autowired
    public ParkingLotService(@Qualifier("parkingQueueDataAccessService") ParkingQueueDao parkingQueueDao,
                             @Qualifier("carDataAccessService") CarDao carDao,
                             @Qualifier("queueService") QueueService queueService) throws Exception {
        this.parkingQueueDao = parkingQueueDao;
        this.carDao = carDao;
        this.queueService = queueService;
        for(int i = 0; i < 5; ++i)
            this.queueService.FetchFromBlockingQueue(i + 1);
    }

    public int parkCars(Integer N) {
        Integer startId = getMaxCarId() + 1;
        PriorityQueue<Pair<Integer, Integer>> priorityParkingQueue = new PriorityQueue<>(new ParkingPriorityQueueComparator());
        List<ParkingQueue> parkingQueues = parkingQueueDao.getAllParkingQueues();
        List<CompletableFuture> parkingRequests = new ArrayList<>();
        for(ParkingQueue parkingQueue : parkingQueues)
            priorityParkingQueue.add(Pair.of(parkingQueue.getId(), parkingQueue.getMaxQueueSize() - parkingQueue.getNoOfCarsParked()));

        int noOfCarsCanPark = 0;
        for(int carId = startId; carId < startId + N; ++carId) {
            if(priorityParkingQueue.size() == 0)
                break;
            Pair<Integer, Integer> parkToQueue = priorityParkingQueue.poll();
            if(parkToQueue.getSecond() <= 0)
                continue;
            parkToQueue = Pair.of(parkToQueue.getFirst(), parkToQueue.getSecond() - 1);
            priorityParkingQueue.add(parkToQueue);
            CompletableFuture parkingRequest = queueService.queueCarToPark(carId, parkToQueue.getFirst());
            parkingRequests.add(parkingRequest);
        }
        for(CompletableFuture parkingRequest : parkingRequests) {
            Integer parked = (Integer) parkingRequest.join();
            if(parked == 1)
                ++noOfCarsCanPark;
        }
        return noOfCarsCanPark;
    }

    public Integer fetchCar(Integer carId) {
        Integer parkingQueueId = carDao.getParkingQueueId(carId);
        if(parkingQueueId == null)
            return null;
        queueService.fetchCarFromParking(carId, parkingQueueId).join();
        return parkingQueueId;
    }

    public Integer getMaxCarId() {
        List<Car> allCars = carDao.getAllCars();
        Integer maxId = 0;
        for(Car car : allCars) {
            maxId = Math.max(maxId, car.getId());
        }
        return maxId;
    }

    public List<ParkingQueue> getParkLotStatus() {
        return parkingQueueDao.getAllParkingQueues();
    }
}
