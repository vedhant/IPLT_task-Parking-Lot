package com.example.ParkingLot.api;

import com.example.ParkingLot.BlockingQueueRequest;
import com.example.ParkingLot.ParkingLotApplication;
import com.example.ParkingLot.dao.CarDataAccessService;
import com.example.ParkingLot.dao.ParkingQueueDao;
import com.example.ParkingLot.dao.ParkingQueueDataAccessService;
import com.example.ParkingLot.model.ParkingQueue;
import com.example.ParkingLot.service.ParkingLotService;
import com.example.ParkingLot.service.QueueService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ParkingLotControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private QueueService queueService;

    @Autowired
    private ParkingQueueDao parkingQueueDao;

    @Autowired
    private ParkingLotService parkingLotService;

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

    @Test
    void fetchCar() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/api/parkCars/20");
        mvc.perform(request).andReturn();

        mvc.perform(MockMvcRequestBuilders.post("/api/fetchCar/12")).andReturn();



        List<BlockingQueue<BlockingQueueRequest>> parkBlockingQueues = queueService.getParkBlockingQueues();

        for(BlockingQueue<BlockingQueueRequest> blockingQueue : parkBlockingQueues) {
            synchronized (blockingQueue) {
                while(!blockingQueue.isEmpty())
                    blockingQueue.wait();
            }
        }

        System.out.println(parkingLotService.getParkLotStatus());

        List<ParkingQueue> parkingQueues = parkingQueueDao.getAllParkingQueues();
        for(int i = 0; i < 5; ++i) {
            if(i == 1)
                continue;
            LinkedList<Integer> queue = (LinkedList<Integer>) parkingQueues.get(i).getQueue();
            assertEquals(4, queue.size());
            for(int j = 0; j < 4; ++j) {
                assertEquals(j * 5 + 1 + i, queue.get(j));
            }
        }
        LinkedList<Integer> queue2 = (LinkedList<Integer>) parkingQueues.get(1).getQueue();
        assertEquals(3, queue2.size());
        assertEquals(17, queue2.get(0));
        assertEquals(2, queue2.get(1));
        assertEquals(7, queue2.get(2));
        assertEquals(1, 1);
    }

    @Test
    void integrationTest() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders.post("/api/parkCars/30");
        mvc.perform(request).andReturn();

        CompletableFuture<Void> asyncRequest1 = AsyncRequest.sendRequestAsync(
                MockMvcRequestBuilders.post("/api/fetchCar/1"), mvc
        );
        CompletableFuture<Void> asyncRequest2 = AsyncRequest.sendRequestAsync(
                MockMvcRequestBuilders.post("/api/fetchCar/12"), mvc
        );
        CompletableFuture<Void> asyncRequest3 = AsyncRequest.sendRequestAsync(
                MockMvcRequestBuilders.post("/api/fetchCar/18"), mvc
        );

        CompletableFuture.allOf(asyncRequest1, asyncRequest2, asyncRequest3).join();

        List<BlockingQueue<BlockingQueueRequest>> parkBlockingQueues = queueService.getParkBlockingQueues();

        for(BlockingQueue<BlockingQueueRequest> blockingQueue : parkBlockingQueues) {
            synchronized (blockingQueue) {
                while(!blockingQueue.isEmpty())
                    blockingQueue.wait();
            }
        }

        System.out.println(parkingLotService.getParkLotStatus());

        List<ParkingQueue> parkingQueues = parkingQueueDao.getAllParkingQueues();

        ArrayList<Integer>[] expected = new ArrayList[5];
        expected[0] = new ArrayList<>(Arrays.asList(6, 11, 16, 21, 26));
        expected[1] = new ArrayList<>(Arrays.asList(17, 22, 27, 2, 7));
        expected[2] = new ArrayList<>(Arrays.asList(23, 28, 3, 8, 13));
        expected[3] = new ArrayList<>(Arrays.asList(4, 9, 14, 19, 24, 29));
        expected[4] = new ArrayList<>(Arrays.asList(5, 10, 15, 20, 25, 30));

        for(int i = 0; i < 5; ++i) {
            Queue<Integer> queue = parkingQueues.get(i).getQueue();
            assertEquals(expected[i].size(), queue.size());
            for(int j = 0; j < queue.size(); ++j) {
                assertEquals(expected[i].get(j), queue.poll());
            }
        }
    }
}