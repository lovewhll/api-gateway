package com.github.edgar615.gateway;

import com.github.edgar615.gateway.verticle.ApiDefinitionVerticle;
import io.vertx.core.Launcher;

/**
 * Created by Edgar on 2017/6/20.
 *
 * @author Edgar  Date 2017/6/20
 */
public class DefinitionMain2 {
    public static void main(String[] args) {
        new Launcher().execute("run", ApiDefinitionVerticle.class.getName(),
                               "--cluster");
    }
}
