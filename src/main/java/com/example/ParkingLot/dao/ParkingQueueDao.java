package com.example.ParkingLot.dao;

import com.example.ParkingLot.model.ParkingQueue;

import java.util.List;

public interface ParkingQueueDao {
    void insertNewParkingQueue(ParkingQueue parkingQueue);
    List<ParkingQueue> getAllParkingQueues();
    int insertCarToQueue(Integer carId, Integer parkingId);
    Integer getFrontCarId(Integer parkingId);
    void removeCar(Integer parkingId);
}
