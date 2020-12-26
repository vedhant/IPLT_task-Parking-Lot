package com.example.ParkingLot.api;

import org.springframework.scheduling.annotation.Async;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.concurrent.CompletableFuture;

public class AsyncRequest {
    @Async
    static public CompletableFuture<Void> sendRequestAsync(RequestBuilder request, MockMvc mvc) throws Exception {
        mvc.perform(request).andReturn();
        return CompletableFuture.completedFuture(null);
    }
}
