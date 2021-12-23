package jpabook.jpashop.api;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDTO;
import jpabook.jpashop.repository.order.query.OrderItemQueryDTO;
import jpabook.jpashop.repository.order.query.OrderQueryDTO;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.service.query.OrderDTO;
import jpabook.jpashop.service.query.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderQueryService orderQueryService;
    private final OrderQueryRepository orderQueryRepository;

    //Entity를 바로 반환하는 안좋은 예제1
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1(){
        return orderQueryService.ordersV1();
    }

    //LazyLoading으로 인해 N+1 문제가 발생하는 예제
    @GetMapping("/api/v2/orders")
    public List<OrderDTO> ordersV2(){
       return orderQueryService.ordersV2();
    }

    /**
     * 1 : 다 fetch 조인 적용
     * 문제 1. order:items = 1:4 이기 때문에 데이터가 뻥튀기된다.
     * 문제 2. 페이징 적용이 불가능 -> 메모리에 올려서 페이징 처리를 하는데 데이터가 많을 경우 메모리 Over됨
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDTO> ordersV3(){
        return orderQueryService.ordersV3();
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
        return orderQueryService.ordersV3_page(offset, limit);
    }

    /**
     * DTO로 바로 반환받는 방법
     * 문제 : orderId로 item을 조회함으로 N+1문제는 여전
     */
    @GetMapping("/api/v4/orders")
    public List<OrderQueryDTO> ordersV4(){
        return orderQueryRepository.findOrderQueryDTOs();
    }

    /**
     * 버전 4에서 리팩토링
     * orderId를 = 이 아닌 in으로 한번에 조회해서 메모리에서 orderId로 item을 조회
     */
    @GetMapping("/api/v5/orders")
    public List<OrderQueryDTO> ordersV5(){
        return orderQueryRepository.findAllByDTO_Optimization();
    }

    /**
     * 1:1:다:다의 관계의 데이터가 뻥튀기 된채로 다 들어있는 flatDTO를 생성
     * 일단 뻥튀기 된 상태로 select해온 뒤
     * OrderId로 grouping을 손수 해서 Loop를 돌리는 방식
     */
    @GetMapping("/api/v6/orders")
    public List<OrderQueryDTO> ordersV6(){
        
        List<OrderFlatDTO> flats = orderQueryRepository.findAllByDTO_flat();

        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDTO(o.getOrderId(),
                                o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDTO(o.getOrderId(),
                                o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDTO(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(),   e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }
}
