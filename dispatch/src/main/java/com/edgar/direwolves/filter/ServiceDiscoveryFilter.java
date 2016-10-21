package com.edgar.direwolves.filter;

import com.edgar.direwolves.definition.HttpEndpoint;
import com.edgar.direwolves.dispatch.ApiContext;
import com.edgar.util.exception.DefaultErrorCode;
import com.edgar.util.exception.SystemException;
import com.edgar.util.vertx.task.Task;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务发现的过滤器.
 * <p>
 * </pre>
 * <p>
 * Created by edgar on 16-9-20.
 */
public class ServiceDiscoveryFilter implements Filter {

  private static final String NAME = "service_discovery";

  private Vertx vertx;

  @Override
  public String name() {
    return NAME;
  }

  @Override
  public String type() {
    return PRE;
  }

  @Override
  public void config(Vertx vertx, JsonObject config) {
    this.vertx = vertx;
  }

  @Override
  public boolean shouldFilter(ApiContext apiContext) {
    if (apiContext.apiDefinition() == null) {
      return false;
    }
    List<String> filters = apiContext.apiDefinition().filters();
    return filters.contains(NAME);
  }

  @Override
  public void doFilter(ApiContext apiContext, Future<ApiContext> completeFuture) {
    List<Future<Record>> futures = new ArrayList<>();
    apiContext.apiDefinition().endpoints().stream()
            .filter(e -> e instanceof HttpEndpoint)
            .map(e -> ((HttpEndpoint) e).service())
            .collect(Collectors.toSet())
            .forEach(s -> futures.add(serviceFuture(s)));
    Task.par(futures)
            .andThen(records -> records.forEach(r -> apiContext.addRecord(r)))
            .onFailure(throwable -> completeFuture.fail(SystemException.create(
                    DefaultErrorCode.UNKOWN_REMOTE)));
  }

  Future<Record> serviceFuture(String service) {
    //服务发现
    Future<Record> serviceFuture = Future.future();
    vertx.eventBus().<JsonObject>send("service.discovery.select", service, ar -> {
      if (ar.succeeded()) {
        JsonObject serviceJson = ar.result().body();
        Record record = new Record(serviceJson);
        serviceFuture.complete(record);
      } else {
        serviceFuture.fail(ar.cause());
      }
    });
    return serviceFuture;
  }

}