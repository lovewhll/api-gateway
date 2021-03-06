package com.github.edgar615.gateway.benchmark.apidiscovery;

import io.vertx.core.json.JsonObject;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Edgar on 2017/7/12.
 *
 * @author Edgar  Date 2017/7/12
 */
@State(Scope.Benchmark)
//@BenchmarkMode(Mode.Throughput)
//@OutputTimeUnit(TimeUnit.MILLISECONDS)
//@Fork(1)
public class ApiDiscoveryBenchmarks1 {
//  public static void main(String[] args) throws RunnerException {
//    Options opt = new OptionsBuilder()
//            .include(ApiDiscoveryBenchmarks1.class.getSimpleName())
//            .forks(1)
//            .param("count", "1", "10", "50", "100")
//            .build();
//    new Runner(opt).run();
//  }

//  @Param({"1", "10", "50", "100"})
//  private int count = 1;

    @TearDown(Level.Trial)
    public void tearDown(ApiBackend pool) {
        pool.close();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Fork(1)
    @OperationsPerInvocation(100000)
    public void testApi(ApiBackend pool) {
        final CountDownLatch latch = new CountDownLatch(1);
        pool.getDefinitions(new JsonObject().put("method", "GET").put("path", "/devices/1"), ar -> {
            latch.countDown();
        });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @Fork(1)
    @OperationsPerInvocation(100000)
    public void testAverage(ApiBackend backend) {
        final CountDownLatch latch = new CountDownLatch(1);
        backend.getDefinitions(new JsonObject().put("method", "GET").put("path", "/devices"),
                               ar -> {
                                   latch.countDown();
                               });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

//  @Benchmark
//  @BenchmarkMode(Mode.SampleTime)
//  @OutputTimeUnit(TimeUnit.MILLISECONDS)
//  @Fork(1)
//  @OperationsPerInvocation(10000)
//  public void testSampleTime(ApiBackend backend) {
//    final CountDownLatch latch = new CountDownLatch(1);
//    backend.getDefinitions(new JsonObject().put("method", "GET").put("path", "/devices/1"), ar
// -> {
//      latch.countDown();
//    });
//    try {
//      latch.await();
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
//  }
}
