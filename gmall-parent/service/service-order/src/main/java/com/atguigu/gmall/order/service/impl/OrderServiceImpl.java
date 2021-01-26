package com.atguigu.gmall.order.service.impl;

import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.mapper.OrderDetailMapper;
import com.atguigu.gmall.order.mapper.OrderMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.org.apache.regexp.internal.RE;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import sun.util.calendar.LocalGregorianCalendar;

import javax.management.Query;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Override
    public boolean checkTradeNo(String userId, String tradeNo) {
        String tradeNoFromCache = (String)redisTemplate.opsForValue().get("user:" + userId + ":tradeCode");
        if (tradeNoFromCache != null && tradeNoFromCache.equals(tradeNo+"")){
            redisTemplate.delete("user:" + userId + ":tradeCode");
            return true;
        }else {
            return false;
        }

    }

    @Override
    public String genTradeNo(String userId) {
        String tradeNo = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set("user:"+ userId + ":tradeCode",tradeNo);
        return tradeNo;
    }

    @Override
    public String submitOrder(OrderInfo order) {
        //设置订单保存数据
        order.setProcessStatus(ProcessStatus.UNPAID.getComment());
        order.setOrderStatus(OrderStatus.UNPAID.getComment());
        //设置日期
        Date date = new Date();
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE,1);
        order.setExpireTime(instance.getTime());
        order.setCreateTime(date);
        //外部订单号
        String outTradeNo = "hahaha";
        outTradeNo = outTradeNo +System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat();
        String format = sdf.format(date);
        outTradeNo = outTradeNo+format;
        order.setOutTradeNo(outTradeNo);
        order.setOrderComment("大黄");
        order.setTotalAmount(getTotalAmount(order.getOrderDetailList()));
        orderMapper.insert(order);
        //获取自动生成的订单id
        Long orderId = order.getId();
        List<OrderDetail> orderDetailList = order.getOrderDetailList();
        for (OrderDetail orderDetail : orderDetailList) {
            orderDetail.setOrderId(orderId);
            orderDetailMapper.insert(orderDetail);

        }

           return orderId+"";
    }

    @Override
    public OrderInfo getOrderInfoById(Long orderId) {
        OrderInfo orderInfo = orderMapper.selectById(orderId);
        QueryWrapper<OrderDetail> orderDetailQueryWrapper = new QueryWrapper<>();
        orderDetailQueryWrapper.eq("order_id", orderId);
        List<OrderDetail> orderDetails = orderDetailMapper.selectList(orderDetailQueryWrapper);
        orderInfo.setOrderDetailList(orderDetails);
        return orderInfo;
    }

    @Override
    public Long updateOrderPay(PaymentInfo paymentInfo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderStatus(OrderStatus.PAID.getComment());
        orderInfo.setProcessStatus(ProcessStatus.PAID.getComment());
        orderInfo.setTradeBody(paymentInfo.getCallbackContent());
        QueryWrapper<OrderInfo> orderInfoQueryWrapper = new QueryWrapper<>();

        orderInfoQueryWrapper.eq("out_trade_no", paymentInfo.getOutTradeNo());
        orderMapper.update(orderInfo, orderInfoQueryWrapper);
        //查id返回去
        OrderInfo orderInfoFromDb = orderMapper.selectOne(orderInfoQueryWrapper);
        if (orderInfoFromDb != null ){
            return orderInfoFromDb.getId();
        }else {
            return null;
        }
    }

    private BigDecimal getTotalAmount(List<OrderDetail> orderDetailList) {
        BigDecimal bigDecimal = new BigDecimal("0");
        for (OrderDetail orderDetail : orderDetailList) {
            Integer skuNum = orderDetail.getSkuNum();
            BigDecimal orderPrice = orderDetail.getOrderPrice().multiply(new BigDecimal(skuNum));
            bigDecimal = bigDecimal.add(orderPrice);
        }
        return bigDecimal;
    }
}
