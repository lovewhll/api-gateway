package com.github.edgar615.gateway.plugin.version;

import com.google.common.base.Preconditions;

import com.github.edgar615.gateway.core.dispatch.ApiContext;

import java.util.List;
import java.util.Optional;

/**
 * Created by Edgar on 2018/4/3.
 *
 * @author Edgar  Date 2018/4/3
 */
public class ClientIpTraffic implements VersionTraffic {
    private final List<IpPolicy> policies;

    public ClientIpTraffic(List<IpPolicy> policies) {
        Preconditions.checkNotNull(policies);
        this.policies = policies;
    }

    public List<IpPolicy> policies() {
        return policies;
    }

    @Override
    public String decision(ApiContext apiContext) {
        String clientIp = (String) apiContext.variables().get("request_clientIp");
        Optional<IpPolicy> optional =
                policies.stream()
                        .filter(p -> p.satisfy(clientIp))
                        .findFirst();
        if (optional.isPresent()) {
            return optional.get().version();
        }
        return null;
    }
}
