package com.edgar.direwolves.core.definition;

import com.edgar.direwolves.core.spi.JsonObjectCodec;

/**
 * Created by Edgar on 2016/10/21.
 *
 * @author Edgar  Date 2016/10/21
 */
public interface ApiPluginFactory<T extends ApiPlugin> extends JsonObjectCodec<T> {
  /**
   * @return 插件名称
   */
  String name();

  ApiPlugin create();
}