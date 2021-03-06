package com.github.edgar615.gateway.plugin.ratelimit;

import com.github.edgar615.gateway.core.dispatch.Filter;
import com.github.edgar615.gateway.core.dispatch.FilterFactory;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * RateLimiterFilter的工厂类.
 * Created by edgar on 16-12-11.
 */
public class RateLimiterFilterFactory implements FilterFactory {
    @Override
    public String name() {
        return RateLimiterFilter.class.getSimpleName();
    }

    @Override
    public Filter create(Vertx vertx, JsonObject config) {
        return new RateLimiterFilter(vertx, config);
    }
}
