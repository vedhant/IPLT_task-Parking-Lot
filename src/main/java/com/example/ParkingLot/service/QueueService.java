package com.example.ParkingLot.service;

import com.example.ParkingLot.BlockingQueueRequest;
import com.example.ParkingLot.dao.CarDao;
import com.example.ParkingLot.dao.ParkingQueueDao;
import com.example.ParkingLot.model.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class QueueService {
    private final ParkingQueueDao parkingQueueDao;
    private final CarDao carDao;

    private final List<BlockingQueue<BlockingQueueRequest>> parkBlockingQueues;
    private final List<BlockingQueue<BlockingQueueRequest>> fetchBlockingQueues;

    @Autowired
    public QueueService(@Qualifier("parkingQueueDataAccessService") ParkingQueueDao parkingQueueDao,
                             @Qualifier("carDataAccessService") CarDao carDao) {
        this.parkingQueueDao = parkingQueueDao;
        this.carDao = carDao;
        parkBlockingQueues = new ArrayList<>();
        for(int i = 0; i < 5; ++i)
            parkBlockingQueues.add(new LinkedBlockingQueue());
        fetchBlockingQueues = new ArrayList<>();
        for(int i = 0; i < 5; ++i)
            fetchBlockingQueues.add(new LinkedBlockingQueue());
    }

    public List<BlockingQueue<BlockingQueueRequest>> getParkBlockingQueues() {
        return parkBlockingQueues;
    }

    public List<BlockingQueue<BlockingQueueRequest>> getFetchBlockingQueues() {
        return fetchBlockingQueues;
    }

    @Async
    public CompletableFuture<Integer> ProcessParkBlockingQueue(Integer queueId) throws InterruptedException {
        BlockingQueue<BlockingQueueRequest> blockingQueue = parkBlockingQueues.get(queueId - 1);
        while(true) {
            synchronized (blockingQueue) {
                if(blockingQueue.isEmpty()) {
                    blockingQueue.notify();
                    System.out.println(queueId + " is empty");
                }
            }
            BlockingQueueRequest request = blockingQueue.peek();
            while (request == null)
                request = blockingQueue.peek();

            Integer carId = request.getCarId();
            System.out.println("parking car " + carId);
            Integer relocate = request.getRelocate();
            CompletableFuture parkingRequest = request.getRequest();

            Thread.sleep(5000L);
            if(parkingQueueDao.insertCarToQueue(carId, queueId) == 0) {
                parkingRequest.complete(0);
                blockingQueue.take();
                continue;
            }
            if(relocate == 0)
                carDao.insertCar(new Car(carId, queueId));
            parkingRequest.complete(1);
            blockingQueue.take();
            System.out.println("parked car " + carId + " in queue " + queueId);
        }
    }

    @Async
    public CompletableFuture<Integer> ProcessFetchBlockingQueue(Integer queueId) throws InterruptedException {
        BlockingQueue<BlockingQueueRequest> blockingQueue = fetchBlockingQueues.get(queueId - 1);
        BlockingQueue<BlockingQueueRequest> parkBlockingQueue = parkBlockingQueues.get(queueId - 1);
        while(true) {
            synchronized (blockingQueue) {
                if(blockingQueue.isEmpty())
                    blockingQueue.notify();
            }

            BlockingQueueRequest request = blockingQueue.take();
            Integer carId = request.getCarId();
            CompletableFuture fetchRequest = request.getRequest();

            while (true) {
                Thread.sleep(3000L);
                Integer fetchCarId = parkingQueueDao.getFrontCarId(queueId);
                parkingQueueDao.removeCar(queueId);
                if(fetchCarId == carId) {
                    carDao.removeCar(carId);
                    break;
                }
                else {
                    System.out.println("relocating car " + fetchCarId + " from queue " + queueId);
                    parkBlockingQueue.add(new BlockingQueueRequest(fetchCarId, 1, new CompletableFuture()));
                }
            }
            fetchRequest.complete(1);
            System.out.println("fetched car " + carId + " from queue " + queueId);
        }
    }

    public CompletableFuture queueCarToPark(Integer carId, Integer parkingQueueId) {
        System.out.println("queueing car " + carId + " to park in queue " + parkingQueueId);
        CompletableFuture parkingRequest = new CompletableFuture();
        parkBlockingQueues.get(parkingQueueId - 1).add(new BlockingQueueRequest(carId, 0, parkingRequest));

        return parkingRequest;
    }

    public CompletableFuture fetchCarFromParking(Integer carId, Integer parkingQueueId) {
        System.out.println("queueing fetching request for car " + carId);
        CompletableFuture fetchRequest = new CompletableFuture();
        fetchBlockingQueues.get(parkingQueueId - 1).add(new BlockingQueueRequest(carId, 0, fetchRequest));

        return fetchRequest;
    }
}
