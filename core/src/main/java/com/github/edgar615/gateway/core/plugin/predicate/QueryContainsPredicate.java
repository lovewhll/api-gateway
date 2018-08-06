package com.github.edgar615.gateway.core.plugin.predicate;

import com.github.edgar615.gateway.core.dispatch.ApiContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class QueryContainsPredicate implements ApiPredicate {

  private final List<String> headers = new ArrayList<>();

  public QueryContainsPredicate(List<String> headers) {
    Objects.requireNonNull(headers);
    this.headers.addAll(headers);
  }

  public boolean test(ApiContext context) {
    return false;
  }
}