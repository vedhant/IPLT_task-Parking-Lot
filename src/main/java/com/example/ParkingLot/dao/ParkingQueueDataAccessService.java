package com.example.ParkingLot.dao;

import com.example.ParkingLot.model.ParkingQueue;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("parkingQueueDataAccessService")
public class ParkingQueueDataAccessService implements ParkingQueueDao{

    private static List<ParkingQueue> DB = new ArrayList<>();

    public ParkingQueueDataAccessService() {
        for(int i=1; i<=5; ++i) {
            insertNewParkingQueue(new ParkingQueue(i, 10));
        }
    }

    @Override
    public void insertNewParkingQueue(ParkingQueue parkingQueue) {
        DB.add(new ParkingQueue(parkingQueue.getId(), parkingQueue.getMaxQueueSize()));
    }

    @Override
    public List<ParkingQueue> getAllParkingQueues() {
        List<ParkingQueue> clonedParkingQueues = new ArrayList<>();
        for(ParkingQueue parkingQueue : DB)
            clonedParkingQueues.add(parkingQueue.clone());
        return clonedParkingQueues;
    }

    @Override
    public int insertCarToQueue(Integer carId, Integer parkingId) {
        ParkingQueue parkingQueue = DB.get(parkingId - 1);
        if(parkingQueue.pushCarToQueue(carId) == 0)
            return 0;
        return 1;
    }

    @Override
    public Integer getFrontCarId(Integer parkingId) {
        return DB.get(parkingId - 1).getFrontCarId();
    }

    @Override
    public void removeCar(Integer parkingId) {
        DB.get(parkingId - 1).removeCar();
    }
}
