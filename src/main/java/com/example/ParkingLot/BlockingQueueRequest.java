package com.example.ParkingLot;

import java.util.concurrent.CompletableFuture;

public class BlockingQueueRequest {
    private final Integer carId;
    private final Integer parkCar;
    private final Integer relocate;
    private final CompletableFuture parkingRequest;

    public BlockingQueueRequest(Integer carId, Integer parkCar, Integer relocate, CompletableFuture parkingRequest) {
        this.carId = carId;
        this.parkCar = parkCar;
        this.relocate = relocate;
        this.parkingRequest = parkingRequest;
    }

    public Integer getCarId() {
        return carId;
    }

    public Integer getParkCar() {
        return parkCar;
    }

    public Integer getRelocate() {
        return relocate;
    }

    public CompletableFuture getParkingRequest() {
        return parkingRequest;
    }
}
