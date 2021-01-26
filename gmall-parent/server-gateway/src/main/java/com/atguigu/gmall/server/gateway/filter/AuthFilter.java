package com.atguigu.gmall.server.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.user.UserInfo;
import com.atguigu.gmall.user.client.UserFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
public class AuthFilter implements GlobalFilter {
    @Autowired
    private UserFeignClient userFeignClient;
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Value("${authUrls.url}")
    private String authUrlsUrl;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //所有请求都会来到这里
        //通过参数拿到请求和返回  是渲染的
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String uri = request.getURI().toString();
        //接下来应该对各种请求进行过滤
        if (uri.contains(".ico") || uri.contains("login") || uri.contains(".css")|| uri.contains(".jpg")|| uri.contains(".png")|| uri.contains(".js")){
            //直接放行
            return chain.filter(exchange);
        }
        //黑名单
        boolean match = antPathMatcher.match("**/admin/**",uri);
        if (match){
            //满足就拦截他 不让他过去
            return out(response,ResultCodeEnum.PERMISSION);
        }
        //白名单 必须登录的放在这里
        String[] urls = authUrlsUrl.split(",");
        boolean ifWhite = false;
        for (String url : urls) {
            if (uri.contains(url)){
                //如果包含白名单中的内容
                ifWhite=true;
            }
        }
        // 远程调用sso系统进行身份认证
        String  token = getToken(request);
        Map<String,Object> map = null;
        if (!StringUtils.isEmpty(token)){
            map = userFeignClient.verify(token);
        }else {
              //如果没有token 就需要给一个临时的id
          String userTempId = getUserTempId(request);
          //不写了  现在放弃了  太麻烦了
            System.out.println(userTempId);

        }

        if(map != null){
             //存在用户就应该把用户带到其他地方
            Object o = map.get("user");
            UserInfo userInfo = JSON.parseObject(JSON.toJSONString(o), UserInfo.class);
            //设置userInfo到request
            if (userInfo != null){
                request.mutate().header("userId", userInfo.getId()+"").build();
                exchange.mutate().request(request);
                return chain.filter(exchange);
            }
        }else {
            if (ifWhite){
                response.setStatusCode(HttpStatus.SEE_OTHER);
                response.getHeaders().set(HttpHeaders.LOCATION,"http://passport.gmall.com/login.html?originUrl=" + uri);
                Mono<Void> voidMono = response.setComplete();
                return voidMono;
            }
        }
        return chain.filter(exchange);
    }

    private String getUserTempId(ServerHttpRequest request) {
        String userTempId = "";
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (null != cookies){
            List<HttpCookie> tokenCookie = cookies.get("userTempId");
            if ( null != tokenCookie){
                for (HttpCookie httpCookie : tokenCookie) {
                    if (httpCookie.getName().equals("userTempId")){
                        userTempId = httpCookie.getValue();
                    }

                }
            }
        }
        if (StringUtils.isEmpty(userTempId)){
            HttpHeaders headers = request.getHeaders();
            if (null != headers){
                List<String> strings = headers.get("userTempId");
                if (null != strings){
                    userTempId = strings.get(0);
                }
            }
        }
        return userTempId;
    }

    private String getToken(ServerHttpRequest request) {
        String token = "";
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        if (null != cookies){
            List<HttpCookie> tokenCookie = cookies.get("token");
            if ( null != tokenCookie){
                for (HttpCookie httpCookie : tokenCookie) {
                    if (httpCookie.getName().equals("token")){
                        token = httpCookie.getValue();
                    }
                }
            }
        }
        if (StringUtils.isEmpty(token)){
            HttpHeaders headers = request.getHeaders();
            if (null != headers){
                List<String> strings = headers.get("token");
                if (null != strings){
                    token = strings.get(0);
                }
            }
        }
        return token;
    }

    // 接口鉴权失败返回数据
    private Mono<Void> out(ServerHttpResponse response, ResultCodeEnum resultCodeEnum) {
        // 返回用户没有权限登录
        Result<Object> result = Result.build(null, resultCodeEnum);
        byte[] bits = JSONObject.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer wrap = response.bufferFactory().wrap(bits);
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        // 输入到页面
        Mono<Void> voidMono = response.writeWith(Mono.just(wrap));

        return voidMono;
    }
}
