package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDTO;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne 관계
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public OrderResponse orderV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<SimpleOrderDTO> collect = orders.stream()
                .map(SimpleOrderDTO::new)
                .collect(Collectors.toList());
        return new OrderResponse(collect);
    }

    @GetMapping("/api/v3/simple-orders")
    public OrderResponse orderV3(){
        List<Order> orders = orderRepository.findAllWithMemberDelivery(0, 100);
        List<SimpleOrderDTO> collect = orders.stream()
                .map(SimpleOrderDTO::new)
                .collect(Collectors.toList());
        return new OrderResponse(collect);
    }

    @GetMapping("/api/v4/simple-orders")
    public OrderResponse orderV4(){
        List<OrderSimpleQueryDTO> orders = orderSimpleQueryRepository.findOrderDTOs();
        return new OrderResponse(orders);
    }
    @Data
    @AllArgsConstructor
    static class OrderResponse<T> {
        private T data;
    }

    @Data
    public class SimpleOrderDTO {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDTO(Order order){
            this.orderId = order.getId();
            this.orderDate = order.getOrderDate();
            this.name = order.getMember().getName();
            this.orderStatus = order.getStatus();
            this.address = order.getDelivery().getAddress();
        }
    }

}
