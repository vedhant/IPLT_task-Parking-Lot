package com.example.ParkingLot.utils;

import com.example.ParkingLot.model.ParkingQueue;
import org.springframework.data.util.Pair;

import java.util.Comparator;

public class ParkingPriorityQueueComparator implements Comparator<Pair<Integer, Integer>> {

    @Override
    public int compare(Pair<Integer, Integer> p1, Pair<Integer, Integer> p2) {
        if(p1.getSecond() < p2.getSecond())
            return 1;
        else if(p1.getSecond() > p2.getSecond())
            return -1;
        else if(p1.getFirst() < p2.getFirst())
            return -1;
        return 1;
    }
}
