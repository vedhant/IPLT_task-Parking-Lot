package com.example.ParkingLot.api;

import com.example.ParkingLot.model.ParkingQueue;
import com.example.ParkingLot.service.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;

@RestController
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    @Autowired
    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @RequestMapping("api/parkCars/{N}")
    @PostMapping
    public String parkCars(@PathVariable Integer N) throws InterruptedException, ExecutionException {
        int noOfCarsParked = parkingLotService.parkCars(N);
        if(noOfCarsParked == N)
            return "Ok. All cars parked.";
        return "Car park full. Only " + noOfCarsParked + " were parked.";
    }

    @RequestMapping("api/fetchCar/{carId}")
    @PostMapping
    public String fetchCar(@PathVariable Integer carId) {
        Integer parkingId = parkingLotService.fetchCar(carId);
        if(parkingId == null) {
            return "car " + carId + " is not parked in the parking lot.";
        }
        return "car " + carId + " is fetched from parking queue " + parkingId + '.';
    }

    @RequestMapping("api/showCarParkStatus")
    @GetMapping
    public String showCarParkStatus() {
        return parkingLotService.getParkLotStatus();
    }
}
