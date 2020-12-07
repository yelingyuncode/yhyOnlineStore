package com.atguigu.gmall.config;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.model.product.SkuInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
public class GmallCacheAspect {
    @Autowired
    private RedisTemplate redisTemplate;



    @Around("@annotation(com.atguigu.gmall.config.GmallCache)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point){
              Object result = null;
              String cacheKey = "";
              //获取方法信息
        MethodSignature methodSignature = (MethodSignature)point.getSignature();
        //方法名字
        String name = methodSignature.getMethod().getName();
        cacheKey = name;
        //返回来行和参数
        Class returnType = methodSignature.getReturnType();
        Object[] args = point.getArgs();
        for (Object arg : args) {
            cacheKey = cacheKey + ":" +arg;
        }
        //拿到注解信息
        GmallCache gmallCache = methodSignature.getMethod().getAnnotation(GmallCache.class);

        //执行缓存
        result= (SkuInfo) redisTemplate.opsForValue().get(cacheKey);
        //如果缓存没有就去执行查数据库 即被代理方法
        if (result==null){
            try {
            String uuid = UUID.randomUUID().toString();
            Boolean ok = redisTemplate.opsForValue().setIfAbsent(cacheKey+":lock",uuid, 2, TimeUnit.SECONDS);
            if (ok){
                //缓存里面没有分布式锁在操作
                try {
                    result = point.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                //判断mysql里面有没有防止缓存穿透
                  if (result == null){
                      //放空缓存进去
                      redisTemplate.opsForValue().set(cacheKey,result,5,TimeUnit.SECONDS);
                  }else {
                      //操作完应该放到redis
                      redisTemplate.opsForValue().set(cacheKey,result);
                  }
                  //把分布式锁删掉
                String opneKey = (String)redisTemplate.opsForValue().get(cacheKey + ":lock");
                  if (uuid.equals(opneKey)){
                      redisTemplate.delete(cacheKey + ":lock");
                  }


            }else {
                //缓存里面有分布式锁在操作 睡眠 等操作完然后再去查缓存
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();

                }
              return redisTemplate.opsForValue().get(cacheKey);

            }
            }catch (Throwable throwable){
                     throwable.printStackTrace();
            }


        }



        return result;
    }


}
