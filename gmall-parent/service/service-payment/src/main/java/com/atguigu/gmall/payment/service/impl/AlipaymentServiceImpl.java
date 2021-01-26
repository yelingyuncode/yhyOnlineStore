package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayAppTokenGetRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.payment.config.AlipayConfig;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.payment.service.AlipaymentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AlipaymentServiceImpl implements AlipaymentService {

    @Autowired
    private AlipayClient alipayClient;
    @Autowired
    private PaymentInfoMapper paymentInfoMapper;
    @Override
    public String aliPaySubmit(OrderInfo orderInfoById) {
        //创建页面支付对象
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //阿里支付完成 回调的页面
        request.setReturnUrl(AlipayConfig.return_payment_url);
        request.setNotifyUrl(AlipayConfig.notify_payment_url);
        //放入参数进去
        HashMap<String, Object> map = new HashMap<>();
        //四个必要的参数
        map.put("out_trade_no",orderInfoById.getOutTradeNo());
        map.put("product_code", "FAST_INSTANT_TRADE_PAY");
        map.put("total_amount", 0.01);
        map.put("subject", orderInfoById.getOrderDetailList().get(0).getSkuName());
        //发送
        request.setBizContent(JSON.toJSONString(map));
        //拿到阿里的回应
        AlipayTradePagePayResponse response = null;

        try {
            response = alipayClient.pageExecute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
          //拿到支付页面
        String body = response.getBody();
        return body;

    }

    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insert(paymentInfo);

    }

    @Override
    public void updatePaymentInfo(PaymentInfo paymentInfo) {
        QueryWrapper<PaymentInfo> paymentInfoQueryWrapper = new QueryWrapper<>();
        paymentInfoQueryWrapper.eq("out_trade_no", paymentInfo.getOutTradeNo());
        paymentInfoMapper.update(paymentInfo, paymentInfoQueryWrapper);

    }

    @Override
    public Map<String, Object> query(String out_trade_no) {
        //新建一个请求
        AlipayTradeQueryRequest alipayTradeQueryRequest = new AlipayTradeQueryRequest();
        HashMap<String, Object> map = new HashMap<>();
        map.put("out_trade_no", out_trade_no);
        alipayTradeQueryRequest.setBizContent(JSON.toJSONString(map));
        AlipayTradeQueryResponse response = null;
        try {
             response = alipayClient.execute(alipayTradeQueryRequest);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        boolean success = response.isSuccess();
        //支付成功
        if (success){
            System.out.println("本地接口成功");
            map.put("success", true);
            map.put("trade_status", response.getTradeStatus());
        }else {
            map.put("success", false);
            System.out.println("本地接口调用失败");
        }
        return map;
    }

    @Override
    public Map<String, Object> checkPayment(String out_trade_no) {

        return query(out_trade_no);
    }
}
