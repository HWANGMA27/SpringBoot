package jpabook.jpashop.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    // order를 통해 delivery를 접근할 가능성이 높다는 가정하에 연관관계의 주인을 order의 delivery로 설정
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // 연관관계 편의 메서드
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    //생성 메서드
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);

        for(OrderItem orderItem: orderItems){
            order.addOrderItem(orderItem);
        }
        order.setStatus(OrderStatus.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    //비지니스 로직
    public void cancel(){
        //배송완료시 취소 불가
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송된 상품으로 취소가 불가능합니다.");
        }

        this.setStatus(OrderStatus.CANCLE);
        for(OrderItem orderItem : orderItems){
            orderItem.cancel();
        }
    }

    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem : orderItems){
            //수량이 1개 이상일 수 있기 때문에 수량 * 가격을 구하는 메서드 아이템에 추
            totalPrice += orderItem.getOrderTotalPrice();

        }
        return totalPrice;

        /*
        스트림 활용 한줄 코딩 가능
        return orderItems.stream().mapToInt(OrderItem::getOrderTotalPrice).sum();
         */
    }
}
