package com.edgar.direwolves.core.dispatch;

import com.google.common.collect.Multimap;

import com.edgar.util.exception.DefaultErrorCode;
import com.edgar.util.exception.SystemException;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by Edgar on 2016/9/21.
 *
 * @author Edgar  Date 2016/9/21
 */
public interface Result {

  /**
   * @return 响应码.
   */
  int statusCode();

  /**
   * @return true:返回的结果是json数组, false:返回的结果是json对象.
   */
  boolean isArray();

  /**
   * @return json对象
   */
  JsonObject responseObject();

  /**
   * @return json数组
   */
  JsonArray responseArray();

  /**
   * 返回result的id.
   *
   * @return
   */
  String id();

  /**
   * @return json对象
   */
  Multimap<String, String> header();

  /**
   * 创建一个JsonObject的响应.
   *
   * @param id           id
   * @param statusCode   响应码
   * @param responseBody 响应
   * @param header       响应头
   * @return Result
   */
  static Result createJsonObject(String id, int statusCode,
                                 JsonObject responseBody, Multimap<String, String> header) {
    return new ResultImpl(id, statusCode, responseBody, header);
  }

  /**
   * 创建一个JsonArray的响应.
   *
   * @param id            id
   * @param statusCode    响应码
   * @param responseArray 响应
   * @param header        响应头
   * @return Result
   */
  static Result createJsonArray(String id, int statusCode,
                                JsonArray responseArray, Multimap<String, String> header) {
    return new ResultImpl(id, statusCode, responseArray, header);
  }

  /**
   * 将buffer转换为AsyncResult，如果转换为JSON数组失败，尝试转换为JSON对象，
   * 同理，如果转换为JSON对象失败，尝试转换为JSON数组.
   *
   * @param id         id
   * @param statusCode 响应码
   * @param data       响应数据
   * @param header     响应头
   * @return Result
   */
  static Result create(String id, int statusCode, Buffer data, Multimap<String, String> header) {
    String str = data.toString().trim();
    return create(id, statusCode, str, header);
  }

  /**
   * 将buffer转换为AsyncResult，如果转换为JSON数组失败，尝试转换为JSON对象，
   * 同理，如果转换为JSON对象失败，尝试转换为JSON数组.
   *
   * @param id         id
   * @param statusCode 响应码
   * @param data       响应数据
   * @param header     响应头
   * @return Result
   */
  static Result create(String id, int statusCode, String data, Multimap<String, String> header) {
    if (data.startsWith("{") && data.endsWith("}")) {
      try {
        return createJsonObject(id, statusCode,
                                Buffer.buffer(data).toJsonObject(), header);
      } catch (Exception e) {
        throw SystemException.wrap(DefaultErrorCode.INVALID_JSON, e);
      }
    }
    if (data.startsWith("[") && data.endsWith("]")) {
      try {
        return createJsonArray(id, statusCode,
                               Buffer.buffer(data).toJsonArray(), header);
      } catch (Exception e) {
        throw SystemException.wrap(DefaultErrorCode.INVALID_JSON, e);
      }
    }
    throw SystemException.create(DefaultErrorCode.INVALID_JSON);
  }

  default Result copy() {
    if (isArray()) {
      return Result.createJsonArray(id(), statusCode(), responseArray().copy(), header());
    }
    return Result.createJsonObject(id(), statusCode(), responseObject(), header());
  }

  default JsonObject toJson() {
    JsonObject result = new JsonObject()
            .put("id", id())
            .put("statusCode", statusCode())
            .put("isArray", isArray())
            .put("header", header().asMap());
    if (isArray()) {
      result.put("responseArray", responseArray());
    } else {
      result.put("responseBody", responseObject());
    }
    return result;
  }
}
