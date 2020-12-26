package com.example.ParkingLot;

import java.util.concurrent.CompletableFuture;

public class BlockingQueueRequest {
    private final Integer carId;
    private final Integer relocate;
    private final CompletableFuture parkingRequest;

    public BlockingQueueRequest(Integer carId, Integer relocate, CompletableFuture parkingRequest) {
        this.carId = carId;
        this.relocate = relocate;
        this.parkingRequest = parkingRequest;
    }

    public Integer getCarId() {
        return carId;
    }


    public Integer getRelocate() {
        return relocate;
    }

    public CompletableFuture getRequest() {
        return parkingRequest;
    }
}
