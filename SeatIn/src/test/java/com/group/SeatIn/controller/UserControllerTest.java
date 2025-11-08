package com.group.SeatIn.controller;


import com.group.SeatIn.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.HashMap;

import static org.mockito.Mockito.when;

@WebMvcTest(controllers=UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
    @Autowired
    MockMvc mvc;
    @MockitoBean
    UserService userService;

    @Test
    void testGetCustomerStatistics() throws Exception {
        // Create a dummy hashmap to return for the mocked service method used by controller
        HashMap<String,Integer> sol = new HashMap<>();
        // Mock the method which is used to simulate the controller
        when(userService.userCustomerAnalytics("")).thenReturn(sol);
        //Ensure status passes as logic has been tested in service tests ensure it matches empty hashmap
        mvc.perform(get("/user/get_customer_statistics").header("Authorization","")).andExpect(status().isOk()).andExpect(content().json("{}"));
    }

    @Test
    void testGetHostStatistics() throws Exception {
        // Create a dummy hashmap to return for the mocked service method used by controller
        HashMap<String,String> sol = new HashMap<>();
        when(userService.userHostAnalytics("")).thenReturn(sol);
        //Ensure status passes as logic has been tested in service tests ensure it matches empty hashmap
        mvc.perform(get("/user/get_host_statistics").header("Authorization","")).andExpect(status().isOk()).andExpect(content().json("{}"));
    }
}
