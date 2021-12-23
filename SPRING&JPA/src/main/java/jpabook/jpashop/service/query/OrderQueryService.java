package jpabook.jpashop.service.query;

import jpabook.jpashop.api.OrderApiController;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class OrderQueryService {
    private final OrderRepository orderRepository;

    /**
     * osiv false 세팅으로 controller에 있던 지연로딩 로직 service단으로 이관(분리)
     * 트래픽이 꽤 되는 서비스의 경우는 osiv를 꺼야 커넥션 풀이 유지된다.
     */
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
    public List<OrderDTO> ordersV2(){
        List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
        List<OrderDTO> collect = orders.stream().map(order -> new OrderDTO(order)).collect(toList());
        return collect;
    }

    public List<OrderDTO> ordersV3(){
        List<Order> all = orderRepository.findAllWithItem();
        List<OrderDTO> collect = all.stream().map(order -> new OrderDTO(order)).collect(toList());
        return collect;
    }

    public List<OrderDTO> ordersV3_page(int offset, int limit){
        List<Order> all = orderRepository.findAllWithMemberDelivery(offset, limit);
        List<OrderDTO> collect = all.stream().map(order -> new OrderDTO(order)).collect(toList());
        return collect;
    }
}
