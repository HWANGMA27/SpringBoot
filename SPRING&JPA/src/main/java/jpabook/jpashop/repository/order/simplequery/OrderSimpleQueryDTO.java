package jpabook.jpashop.repository.order.simplequery;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.OrderStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrderSimpleQueryDTO {

    private Long orderId;
    private String name;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
    private Address address;

    public OrderSimpleQueryDTO(Long orderId, LocalDateTime orderDate, String name, OrderStatus orderStatus, Address address){
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.name = name;
        this.orderStatus = orderStatus;
        this.address = address;
    }
}
