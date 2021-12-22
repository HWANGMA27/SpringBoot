package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;

    //기존 orderRepository와 분리하는것을 추천한다.
    public List<OrderSimpleQueryDTO> findOrderDTOs() {
        return em.createQuery("select new jpabook.jpashop.repository.OrderSimpleQueryDTO(o.id, o.orderDate, m.name, o.status, d.address)" +
                " from Order o" +
                " join o.member m" +
                " join o.delivery d", OrderSimpleQueryDTO.class).getResultList();
    }
}
