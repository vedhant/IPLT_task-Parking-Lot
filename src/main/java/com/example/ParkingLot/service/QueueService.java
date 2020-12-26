package com.example.ParkingLot.service;

import com.example.ParkingLot.BlockingQueueRequest;
import com.example.ParkingLot.dao.CarDao;
import com.example.ParkingLot.dao.ParkingQueueDao;
import com.example.ParkingLot.model.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class QueueService {
    private final ParkingQueueDao parkingQueueDao;
    private final CarDao carDao;

    private final List<BlockingQueue<BlockingQueueRequest>> blockingQueues;

    @Autowired
    public QueueService(@Qualifier("parkingQueueDataAccessService") ParkingQueueDao parkingQueueDao,
                             @Qualifier("carDataAccessService") CarDao carDao) {
        this.parkingQueueDao = parkingQueueDao;
        this.carDao = carDao;
        blockingQueues = new ArrayList<>();
        for(int i = 0; i < 5; ++i)
            blockingQueues.add(new LinkedBlockingQueue());
    }

    @Async
    public CompletableFuture<Integer> FetchFromBlockingQueue(Integer queueId) throws InterruptedException {
        while(true) {
            BlockingQueueRequest request = blockingQueues.get(queueId - 1).take();
            Integer carId = request.getCarId();
            Integer parkCar = request.getParkCar();
            Integer relocate = request.getRelocate();
            CompletableFuture parkingRequest = request.getParkingRequest();
            if(parkCar == 1) {
                Thread.sleep(5000L);
                if(parkingQueueDao.insertCarToQueue(carId, queueId) == 0) {
                    parkingRequest.complete(0);
                    continue;
                }
                if(relocate == 0)
                    carDao.insertCar(new Car(carId, queueId));
                parkingRequest.complete(1);
                System.out.println("parked car " + carId + " in queue " + queueId);
            }
            else {
                while(true) {
                    Thread.sleep(3000L);
                    Integer fetchCarId = parkingQueueDao.getFrontCarId(queueId);
                    parkingQueueDao.removeCar(queueId);
                    if(fetchCarId == carId) {
                        carDao.removeCar(carId);
                        break;
                    }
                    else {
                        blockingQueues.get(queueId - 1).add(new BlockingQueueRequest(fetchCarId, 1, 1, new CompletableFuture()));
                        System.out.println("relocating car " + fetchCarId + " from queue " + queueId);
                    }
                }
                parkingRequest.complete(1);
                System.out.println("fetched car " + carId + " from queue " + queueId);
            }
        }
    }

    public CompletableFuture queueCarToPark(Integer carId, Integer parkingQueueId) {
        System.out.println("queueing car " + carId + " to park in queue " + parkingQueueId);
        CompletableFuture parkingRequest = new CompletableFuture();
        blockingQueues.get(parkingQueueId - 1).add(new BlockingQueueRequest(carId, 1, 0, parkingRequest));

        return parkingRequest;
    }

    public CompletableFuture fetchCarFromParking(Integer carId, Integer parkingQueueId) {
        System.out.println("queueing fetching request for car " + carId);
        CompletableFuture fetchRequest = new CompletableFuture();
        blockingQueues.get(parkingQueueId - 1).add(new BlockingQueueRequest(carId, 0, 0, fetchRequest));

        return fetchRequest;
    }
}
