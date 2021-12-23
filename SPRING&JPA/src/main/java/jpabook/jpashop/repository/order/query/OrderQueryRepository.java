package jpabook.jpashop.repository.order.query;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository {

    private final EntityManager em;

    public List<OrderQueryDTO> findOrderQueryDTOs(){
        List<OrderQueryDTO> orders = findOrders();
        orders.forEach(orderQueryDTO -> {
            List<OrderItemQueryDTO> orderItems = findOrderItems(orderQueryDTO.getOrderId());
            orderQueryDTO.setOrderItems(orderItems);
        });
        return orders;
    }

    private List<OrderQueryDTO> findOrders(){
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderQueryDTO(o.id, m.name, o.orderDate, o.status, d.address) " +
                        "from Order o " +
                        "join o.member m " +
                        "join o.delivery d", OrderQueryDTO.class)
                        .getResultList();
    }

    private List<OrderItemQueryDTO> findOrderItems(Long orderId){
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDTO(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                        "from OrderItem oi " +
                        "join oi.item i " +
                        "where oi.order.id = :orderId", OrderItemQueryDTO.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    public List<OrderQueryDTO> findAllByDTO_Optimization() {
        List<OrderQueryDTO> result = findOrders();
        //조회된 order의 id를 collect
        List<OrderItemQueryDTO> orderItems = findOrderItemMap(toOrderIds(result));
        //orderId : items 를 Map형태로 가공
        Map<Long, List<OrderItemQueryDTO>> orderItemMap = orderItems.stream()
                .collect(Collectors.groupingBy(orderItemQueryDTO -> orderItemQueryDTO.getOrderId()));
        //result에 item을 채워준다
        result.forEach(orderQueryDTO -> orderQueryDTO.setOrderItems(orderItemMap.get(orderQueryDTO.getOrderId())));
        return result;
    }

    private List<OrderItemQueryDTO> findOrderItemMap(List<Long> orderIds) {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderItemQueryDTO(oi.order.id, i.name, oi.orderPrice, oi.count)" +
                                                                    "from OrderItem oi " +
                                                                    "join oi.item i " +
                                                                    "where oi.order.id in :orderIds", OrderItemQueryDTO.class)
                                                            .setParameter("orderIds", orderIds)
                                                            .getResultList();
    }

    private List<Long> toOrderIds(List<OrderQueryDTO> result) {
        return result.stream()
                    .map(orderQueryDTO -> orderQueryDTO.getOrderId())
                    .collect(Collectors.toList());
    }

    public List<OrderFlatDTO> findAllByDTO_flat() {
        return em.createQuery("select new jpabook.jpashop.repository.order.query.OrderFlatDTO(o.id, m.name, o.orderDate, o.status, d.address, i.name, oi.orderPrice, oi.count) "+
                "from Order o " +
                "join o.member m "+
                "join o.delivery d "+
                "join o.orderItems oi " +
                "join oi.item i", OrderFlatDTO.class).getResultList();
    }
}
