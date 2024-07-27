package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    public List<Order> findAll(OrderSearch orderSearch)
    {
        CriteriaBuilder cb = em.getCriteriaBuilder();

        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name = cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);}

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }

    public List<Order> findAll2(OrderSearch orderSearch)
    {
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QOrder order = QOrder.order;
        QMember member = QMember.member;

        BooleanExpression statusCondition = getStatusCondition(orderSearch.getOrderStatus(), order);
        BooleanExpression nameCondition = getNameCondition(orderSearch.getMemberName(), member);

        return queryFactory
                .selectFrom(order)
                .join(order.member, member)
                .where(statusCondition, nameCondition)
                .limit(1000)
                .fetch();
    }

    private BooleanExpression getStatusCondition(OrderStatus status, QOrder order) {
        return status != null ? order.status.eq(status) : null;
    }

    private BooleanExpression getNameCondition(String memberName, QMember member) {
        return StringUtils.hasText(memberName) ? member.name.containsIgnoreCase(memberName) : null;
    }
}
