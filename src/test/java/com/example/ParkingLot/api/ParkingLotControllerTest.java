package com.example.ParkingLot.api;

import com.example.ParkingLot.ParkingLotApplication;
import com.example.ParkingLot.dao.CarDataAccessService;
import com.example.ParkingLot.dao.ParkingQueueDataAccessService;
import com.example.ParkingLot.service.ParkingLotService;
import com.example.ParkingLot.service.QueueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ParkingLotControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void parkCars() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/api/parkCars/20");
        long start = System.currentTimeMillis();

        MvcResult result = mvc.perform(request).andReturn();

        long end = System.currentTimeMillis();
        long timeElapsed = end - start;
        System.out.println(timeElapsed);
        assertTrue(timeElapsed < 25000L);
    }
}