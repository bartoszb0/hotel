package com.bd.hotelapp;

import org.springframework.boot.SpringApplication;

public class TestHotelAppApplication {

    public static void main(String[] args) {
        SpringApplication.from(HotelAppApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
