package com.atguigu.gmall.config;

public @interface GmallCache {
    public String SkuPrefix() default "sku:";
    public String SpuPrefix() default "spu:";
    public String prefix() default "GmallCache:";
}
