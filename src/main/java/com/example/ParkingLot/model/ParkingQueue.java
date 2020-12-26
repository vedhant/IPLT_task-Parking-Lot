package com.example.ParkingLot.model;

import java.util.LinkedList;
import java.util.Queue;

public class ParkingQueue {
    private final Integer id;
    private Queue<Integer> queue;
    private final Integer maxQueueSize;

    public ParkingQueue(Integer id, Integer maxQueueSize) {
        this.id = id;
        this.maxQueueSize = maxQueueSize;
        this.queue = new LinkedList<>();
    }

    public Integer getId() {
        return id;
    }

    public Integer getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setQueue(Queue<Integer> queue) {
        this.queue = new LinkedList<>(queue);
    }

    public Queue<Integer> getQueue() {
        return new LinkedList<>(queue);
    }

    public Integer getNoOfCarsParked() {
        return queue.size();
    }

    public int pushCarToQueue(Integer carId) {
        if(queue.size() == maxQueueSize)
            return 0;
        queue.add(carId);
        return 1;
    }

    public Integer getFrontCarId() {
        return queue.peek();
    }

    public void removeCar() {
        queue.remove();
    }

    public ParkingQueue clone() {
        ParkingQueue clonedParkingQueue = new ParkingQueue(this.id, this.maxQueueSize);
        clonedParkingQueue.setQueue(queue);
        return clonedParkingQueue;
    }
}
