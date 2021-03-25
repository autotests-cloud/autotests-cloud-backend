package com.example.demo.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Order {
    public Order() {
    }

    String content;
    String price;

    public Order(String content, String price) {
        this.content = content;
        this.price = price;
    }


}
