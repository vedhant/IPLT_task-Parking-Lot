package com.example.ParkingLot.dao;

import com.example.ParkingLot.model.Car;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("carDataAccessService")
public class CarDataAccessService implements CarDao{

    private static List<Car> DB = new ArrayList<>();

    @Override
    public int insertCar(Car car) {
        DB.add(new Car(car.getId(), car.getParkingQueue()));
        return 1;
    }

    @Override
    public List<Car> getAllCars() {
        List<Car> clonedCars = new ArrayList<>();
        for(Car car : DB)
            clonedCars.add(car.clone());
        return clonedCars;
    }

    @Override
    public Integer getParkingQueueId(Integer carId) {
        for(Car car : DB) {
//            System.out.println(car.getId() + " : " + car.getParkingQueue());
            if(car.getId() == carId)
                return car.getParkingQueue();
        }
        return null;
    }

    @Override
    public void removeCar(Integer carId) {
        for(int i = 0; i < DB.size(); ++i) {
            if(DB.get(i).getId() == carId) {
                DB.remove(i);
                return;
            }
        }
    }
}
