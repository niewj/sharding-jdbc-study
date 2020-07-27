package com.niewj.shardingjdbcstudy;

import com.niewj.entity.Order;
import com.niewj.mapper.OrderMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class ApplicationTests {

    @Autowired
    private OrderMapper orderMapper;


    @Test
    void testAdd() {
        System.out.println(orderMapper);
        Order order = new Order();
        order.setOid(1004L);
        order.setOname("小米10");
        order.setOstatus("已发货");
        order.setUserId(320001L);
        int insert = orderMapper.insert(order);
        System.out.println(insert);
    }

}
