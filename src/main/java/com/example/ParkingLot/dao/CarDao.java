package com.example.ParkingLot.dao;

import com.example.ParkingLot.model.Car;

import java.util.List;

public interface CarDao {
    int insertCar(Car car);
    List<Car> getAllCars();
    Integer getParkingQueueId(Integer carId);
    void removeCar(Integer carId);
}
