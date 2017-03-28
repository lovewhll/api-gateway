package com.edgar.direwolves.web;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.templ.HandlebarsTemplateEngine;
import io.vertx.ext.web.templ.TemplateEngine;

public class WebVerticle extends AbstractVerticle {

  @Override
  public void start() throws Exception {

    Router router = Router.router(vertx);
    router.route().handler(StaticHandler.create());
    vertx.createHttpServer().requestHandler(router::accept).listen(8080);
  }
}