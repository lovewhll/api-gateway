package com.github.edgar615.gateway.filter;

import com.google.common.collect.Lists;

import com.github.edgar615.gateway.ApiUtils;
import com.github.edgar615.gateway.core.apidiscovery.ApiDiscovery;
import com.github.edgar615.gateway.core.apidiscovery.ApiDiscoveryOptions;
import com.github.edgar615.gateway.core.dispatch.ApiContext;
import com.github.edgar615.gateway.core.dispatch.Filter;
import com.github.edgar615.gateway.core.utils.Filters;
import com.github.edgar615.util.base.Randoms;
import com.github.edgar615.util.exception.DefaultErrorCode;
import com.github.edgar615.util.exception.SystemException;
import com.github.edgar615.util.vertx.task.Task;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Edgar on 2017/1/9.
 *
 * @author Edgar  Date 2017/1/9
 */
@RunWith(VertxUnitRunner.class)
public class ApiFindFilterTest {

    ApiDiscovery apiDiscovery;

    int devicePort = Integer.parseInt(Randoms.randomNumber(4));

    private Vertx vertx;

    private String namespace = UUID.randomUUID().toString();

    private JsonObject config = new JsonObject().put("api.discovery", new JsonObject()
            .put("name", namespace));

    @Before
    public void setUp(TestContext testContext) {
        vertx = Vertx.vertx();
        apiDiscovery = ApiDiscovery.create(vertx, new ApiDiscoveryOptions().setName(namespace));
        ApiUtils.registerApi(apiDiscovery, devicePort);
    }

    @Test
    public void testFoundApi(TestContext testContext) {
        ApiContext apiContext =
                ApiContext.create(HttpMethod.POST, "/devices", null, null, null);
        Task<ApiContext> task = Task.create();
        task.complete(apiContext);
        Async async = testContext.async();
        Filter filter = Filter.create(ApiFindFilter.class.getSimpleName(), vertx, config);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Filters.doFilter(task, Lists.newArrayList(filter))
                .andThen(context -> {
                    testContext.assertNotNull(context.apiDefinition());
                    testContext.assertEquals("add_device", context.apiDefinition().name());
                    async.complete();
                }).onFailure(throwable -> {
            throwable.printStackTrace();
            testContext.fail();
        });
    }

    @Test
    public void testNotFoundApi(TestContext testContext) {
        ApiContext apiContext =
                ApiContext.create(HttpMethod.GET, "/users", null, null, null);
        Task<ApiContext> task = Task.create();
        task.complete(apiContext);
        Async async = testContext.async();
        Filter filter = Filter.create(ApiFindFilter.class.getSimpleName(), vertx, config);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Filters.doFilter(task, Lists.newArrayList(filter))
                .andThen(context -> {
                    testContext.fail();
                }).onFailure(throwable -> {
            testContext.assertTrue(throwable instanceof SystemException);
            SystemException se = (SystemException) throwable;
            testContext.assertEquals(DefaultErrorCode.RESOURCE_NOT_FOUND, se.getErrorCode());
            async.complete();
        });
    }

    @Test
    public void testConflictApi(TestContext testContext) {
        ApiContext apiContext =
                ApiContext.create(HttpMethod.GET, "/devices", null, null, null);
        Task<ApiContext> task = Task.create();
        task.complete(apiContext);
        Async async = testContext.async();
        Filter filter = Filter.create(ApiFindFilter.class.getSimpleName(), vertx, config);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Filters.doFilter(task, Lists.newArrayList(filter))
                .andThen(context -> {
                    testContext.fail();
                }).onFailure(throwable -> {
            throwable.printStackTrace();
            testContext.assertTrue(throwable instanceof SystemException);
            SystemException se = (SystemException) throwable;
            testContext.assertEquals(DefaultErrorCode.CONFLICT, se.getErrorCode());
            async.complete();
        });
    }

    @Test
    public void testRegexBeforeAnt(TestContext testContext) {
        ApiContext apiContext =
                ApiContext.create(HttpMethod.GET, "/user/1", null, null, null);
        Task<ApiContext> task = Task.create();
        task.complete(apiContext);
        Async async = testContext.async();
        Filter filter = Filter.create(ApiFindFilter.class.getSimpleName(), vertx, config);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Filters.doFilter(task, Lists.newArrayList(filter))
                .andThen(context -> {
                    testContext.assertNotNull(context.apiDefinition());
                    testContext.assertEquals("get_user", context.apiDefinition().name());
                    async.complete();
                }).onFailure(throwable -> {
            throwable.printStackTrace();
            testContext.fail();
        });
    }

    @Test
    public void testAnt(TestContext testContext) {
        ApiContext apiContext =
                ApiContext.create(HttpMethod.GET, "/user/abc", null, null, null);
        Task<ApiContext> task = Task.create();
        task.complete(apiContext);
        Async async = testContext.async();
        Filter filter = Filter.create(ApiFindFilter.class.getSimpleName(), vertx, config);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Filters.doFilter(task, Lists.newArrayList(filter))
                .andThen(context -> {
                    testContext.assertNotNull(context.apiDefinition());
                    testContext.assertEquals("userAnt", context.apiDefinition().name());
                    async.complete();
                }).onFailure(throwable -> {
            throwable.printStackTrace();
            testContext.fail();
        });
    }
}
