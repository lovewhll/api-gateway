package com.edgar.direvolves.plugin.authentication;

import com.edgar.direwolves.core.definition.ApiPlugin;
import com.edgar.direwolves.core.definition.ApiPluginFactory;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by Edgar on 2016/10/31.
 *
 * @author Edgar  Date 2016/10/31
 */
public class AuthenticationPluginFactory implements ApiPluginFactory<AuthenticationPlugin> {
  @Override
  public String name() {
    return AuthenticationPlugin.class.getSimpleName();
  }

  @Override
  public ApiPlugin create() {
    return new AuthenticationPluginImpl();
  }

  @Override
  public AuthenticationPlugin decode(JsonObject jsonObject) {

    if (jsonObject.getBoolean("authentication", true)) {
      return new AuthenticationPluginImpl();
    }
    return null;
  }

  @Override
  public JsonObject encode(AuthenticationPlugin plugin) {
    if (plugin == null) {
      return new JsonObject();
    }
    return new JsonObject().put("authentication", true);
  }
}