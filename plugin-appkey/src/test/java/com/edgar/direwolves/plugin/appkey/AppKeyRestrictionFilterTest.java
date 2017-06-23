package com.edgar.direwolves.plugin.appkey;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import com.edgar.direwolves.core.definition.ApiDefinition;
import com.edgar.direwolves.core.definition.ApiPlugin;
import com.edgar.direwolves.core.definition.HttpEndpoint;
import com.edgar.direwolves.core.dispatch.ApiContext;
import com.edgar.direwolves.core.dispatch.Filter;
import com.edgar.direwolves.core.utils.Filters;
import com.edgar.util.exception.DefaultErrorCode;
import com.edgar.util.exception.SystemException;
import com.edgar.util.vertx.task.Task;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by edgar on 16-10-28.
 */
@RunWith(VertxUnitRunner.class)
public class AppKeyRestrictionFilterTest {

  private final List<Filter> filters = new ArrayList<>();

  Filter filter;

  private ApiContext apiContext;

  private String groupKey = "roles";

  @Before
  public void setUp() {
    Multimap<String, String> params = ArrayListMultimap.create();
    params.put("q3", "v3");
    Multimap<String, String> headers = ArrayListMultimap.create();
    headers.put("h3", "v3");
    headers.put("h3", "v3.2");

    apiContext =
            ApiContext.create(HttpMethod.GET, "/devices", headers, params, null);
    HttpEndpoint httpEndpoint =
            HttpEndpoint.http("get_device", HttpMethod.GET, "devices/", "device");

    groupKey = UUID.randomUUID().toString();
    ApiDefinition definition = ApiDefinition
            .create("get_device", HttpMethod.GET, "devices/", Lists.newArrayList(httpEndpoint));
    apiContext.setApiDefinition(definition);
    apiContext.addVariable("app.appKey", "testGroup");

    filter = Filter.create(AppKeyRestrictionFilter.class.getSimpleName(), Vertx.vertx(),
                           new JsonObject().put("appkey.blacklist", new JsonArray().add("guest"))
                                   .put("appkey.whitelist", new JsonArray().add("group1")));

    filters.clear();
    filters.add(filter);
  }

  @Test
  public void testNoAppKeyShouldSuccess(TestContext testContext) {
    AppKeyRestriction plugin =
            (AppKeyRestriction) ApiPlugin.create(AppKeyRestriction.class.getSimpleName());
    plugin.addBlacklist("testGroup");
    apiContext.variables().remove("app.appKey");
    apiContext.apiDefinition().addPlugin(plugin);
    Task<ApiContext> task = Task.create();
    task.complete(apiContext);
    Async async = testContext.async();
    Filters.doFilter(task, filters)
            .andThen(context -> {
              async.complete();
            }).onFailure(t -> {
      testContext.fail();
    });
  }

  @Test
  public void testGlobalBlackGroupShouldForbidden(TestContext testContext) {
//    AppKeyRestriction plugin =
//            (AppKeyRestriction) ApiPlugin.create(AppKeyRestriction.class.getSimpleName());
//    plugin.addBlacklist("testGroup");
//    apiContext.apiDefinition().addPlugin(plugin);
    apiContext.addVariable("app.appKey", "guest");
    Task<ApiContext> task = Task.create();
    task.complete(apiContext);
    Async async = testContext.async();
    Filters.doFilter(task, filters)
            .andThen(context -> {
              testContext.fail();
            }).onFailure(t -> {
      SystemException ex = (SystemException) t;
      testContext.assertEquals(DefaultErrorCode.PERMISSION_DENIED, ex.getErrorCode());
      async.complete();
    });
  }

  @Test
  public void testGlobalWhiteShouldAlwaysAllow(TestContext testContext) {
    AppKeyRestriction plugin =
            (AppKeyRestriction) ApiPlugin.create(AppKeyRestriction.class.getSimpleName());
    plugin.addBlacklist("group1");
    apiContext.addVariable("app.appKey", "group1");
    apiContext.apiDefinition().addPlugin(plugin);
    Task<ApiContext> task = Task.create();
    task.complete(apiContext);
    Async async = testContext.async();
    Filters.doFilter(task, filters)
            .andThen(context -> {
              async.complete();
            }).onFailure(t -> {
      testContext.fail();
    });
  }

  @Test
  public void testBlackGroupShouldForbidden(TestContext testContext) {
    AppKeyRestriction plugin =
            (AppKeyRestriction) ApiPlugin.create(AppKeyRestriction.class.getSimpleName());
    plugin.addBlacklist("testGroup");
    apiContext.apiDefinition().addPlugin(plugin);
    Task<ApiContext> task = Task.create();
    task.complete(apiContext);
    Async async = testContext.async();
    Filters.doFilter(task, filters)
            .andThen(context -> {
              testContext.fail();
            }).onFailure(t -> {
      SystemException ex = (SystemException) t;
      testContext.assertEquals(DefaultErrorCode.PERMISSION_DENIED, ex.getErrorCode());
      async.complete();
    });
  }

  @Test
  public void testWildcardBlackShouldForbidden(TestContext testContext) {
    AppKeyRestriction plugin =
            (AppKeyRestriction) ApiPlugin.create(AppKeyRestriction.class.getSimpleName());
    plugin.addBlacklist("*");
    apiContext.apiDefinition().addPlugin(plugin);
    Task<ApiContext> task = Task.create();
    task.complete(apiContext);
    Async async = testContext.async();
    Filters.doFilter(task, filters)
            .andThen(context -> {
              testContext.fail();
            }).onFailure(t -> {
      SystemException ex = (SystemException) t;
      testContext.assertEquals(DefaultErrorCode.PERMISSION_DENIED, ex.getErrorCode());
      async.complete();
    });
  }

  @Test
  public void testWhiteShouldAlwaysAllow(TestContext testContext) {
    AppKeyRestriction plugin =
            (AppKeyRestriction) ApiPlugin.create(AppKeyRestriction.class.getSimpleName());
    plugin.addBlacklist("testGroup");
    plugin.addWhitelist("testGroup");
    apiContext.apiDefinition().addPlugin(plugin);
    Task<ApiContext> task = Task.create();
    task.complete(apiContext);
    Async async = testContext.async();
    Filters.doFilter(task, filters)
            .andThen(context -> {
              async.complete();
            }).onFailure(t -> {
      testContext.fail();
    });
  }

  @Test
  public void testWildcardWhiteShouldAlwaysAllow(TestContext testContext) {
    AppKeyRestriction plugin =
            (AppKeyRestriction) ApiPlugin.create(AppKeyRestriction.class.getSimpleName());
    plugin.addBlacklist("testGroup");
    plugin.addWhitelist("*");
    Task<ApiContext> task = Task.create();
    task.complete(apiContext);
    Async async = testContext.async();
    Filters.doFilter(task, filters)
            .andThen(context -> {
              async.complete();
            }).onFailure(t -> {
      testContext.fail();
    });
  }

}