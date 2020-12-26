package com.example.ParkingLot.model;

public class Car {
    private final Integer id;
    private Integer parkingQueue;

    public Car(Integer id, Integer parkingQueue) {
        this.id = id;
        this.parkingQueue = parkingQueue;
    }

    public Integer getId() {
        return id;
    }

    public Integer getParkingQueue() {
        return parkingQueue;
    }

    public void setParkingQueue(Integer parkingQueue) {
        this.parkingQueue = parkingQueue;
    }

    public Car clone() {
        return new Car(this.id, this.parkingQueue);
    }
}
