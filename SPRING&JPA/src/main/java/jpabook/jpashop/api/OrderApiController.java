package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderQueryDTO;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    //Entity를 바로 반환하는 안좋은 예제1
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());
        }
        return all;
    }

    //LazyLoading으로 인해 N+1 문제가 발생하는 예제
    @GetMapping("/api/v2/orders")
    public List<OrderDTO> ordersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDTO> collect = orders.stream().map(order -> new OrderDTO(order)).collect(Collectors.toList());
        return collect;
    }

    /**
     * 1 : 다 fetch 조인 적용
     * 문제 1. order:items = 1:4 이기 때문에 데이터가 뻥튀기된다.
     * 문제 2. 페이징 적용이 불가능 -> 메모리에 올려서 페이징 처리를 하는데 데이터가 많을 경우 메모리 Over됨
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDTO> ordersV3(){
        List<Order> all = orderRepository.findAllWithItem();
        List<OrderDTO> collect = all.stream().map(order -> new OrderDTO(order)).collect(Collectors.toList());
        return collect;
    }

    /**
     * 페이징 적용
     * (One or Many) To One 관계 fetch join으로 설정 (쿼리)
     *      > 이부분도 사실상 join없이 선행 테이블만 조회해도 lazy + batch_fetch_size설정이 적용되어 지연로딩이 됨
     * To Many 관계는 지연로딩으로 불러오되
     * default_batch_fetch_size 설정으로 n개만큼 select 쿼리를 생성하는것이 아니라 in 검색 쿼리로 한번에 조회해온다.
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDTO> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit){
        List<Order> all = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDTO> collect = all.stream().map(order -> new OrderDTO(order)).collect(Collectors.toList());
        return collect;
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDTO> ordersV4(){
        return orderQueryRepository.findOrderQueryDTOs();
    }

    @Data
    static class OrderDTO {

        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        private List<OrderItemDTO> orderItems;

        public OrderDTO(Order order){
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getMember().getAddress();
            orderItems = order.getOrderItems()
                    .stream()
                    .map(orderItem -> new OrderItemDTO(orderItem))
                    .collect(Collectors.toList());
        }

    }

    @Data
    static class OrderItemDTO{
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDTO(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }
}
