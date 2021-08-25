
# JMH(Java Microbenchmark Harness)

>`JMH`是`OpenJDK`团队开发的一款基准测试工具，一般用于代码的性能调优，精度甚至可以达到纳秒级别;
`fastjson`是否正如它自己所说的那样至今性能未遇对手？
`Fork/Join`框架真的有提高性能吗？


## Get Started

### Maven Dependency

```
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-core</artifactId>
    <version>1.28</version>
</dependency>
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-generator-annprocess</artifactId>
    <version>1.28</version>
</dependency>

```
### 入门example:

```java
public class JMHStarted {


    /**
     * 这个 runner 类的作用，就是启动基准测试
     *
     * <p>1.通过命令行使用maven命令执行,这种适合对于大型基准测试</p>
     * <p>2.还提供了一种通过 Main方法运行的方式</p>
     * org.openjdk.jmh.runner.Runner 类去运行 org.openjdk.jmh.runner.options.Options 实例
     */
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(JMHStarted.class.getSimpleName())
                .build();
        new Runner(options).run();
    }


    @Benchmark
    @Warmup(iterations = 1, time = 2)
    @Threads(2)
    @Fork(2)
    @BenchmarkMode(Mode.Throughput)
    @Measurement(iterations = 2, time = 2)
    public void testForEach() {
        int counter = 0;
        for (int i = 0; i < 10; i++) {
            counter += i;
        }
    }

}
```


### 配置详解


- `@Warmup`: 预热运行1次，每次2s
- `@Threads`: 每次多少线程来执行
- `@Fork`: 代表启动多个单独的进程分别测试
- `@Measurement`： 运行2次，每次2s
- `@BenchmarkMode`: 基准测试的模式
1.AverageTime，表示每次执行时间<br>
2.SampleTime表示采样时间<br>
3.SingleShotTime表示只运行一次，用于测试冷启动消耗时间<br>
4.All表示统计前面的所有指标<br>
- `@State`(value = Scope.Benchmark)：基准测试内共享对象
1.Scope.Group)：同一个线程组内共享<br>
2.Scope.Thread)：同一个线程内共享<br>
- `@Setup` & `@TearDown`: 初始化和销毁

> PS: 相关注解也可以直接加载类上，或者直接在main方法的Option中配置

```
public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                // 导入要测试的类
                .include(StringBuilderRunner.class.getSimpleName())
                // 预热2轮
                .warmupIterations(1)
                // 度量3轮
                .measurementIterations(1)
                .mode(Mode.Throughput)
                .forks(1)
                // 输出json文件：可以生成可视化UI https://jmh.morethan.io/
                .result("./StringBuilderRunner.json")
                .resultFormat(ResultFormatType.JSON)
                .build();

        new Runner(opt).run();


    }
```

**导出的json文件可以通过在线进行可视化**：

- https://jmh.morethan.io/
- http://deepoove.com/jmh-visual-chart/


### Options properties

方法名|参数|对应注解
---|---|---
include|要运行基准测试类的简单名称 eg. StringConnectBenchmark/-
exclude|不要运行基准测试类的简单名称 eg. StringConnectBenchmark|-
warmupIterations|预热的迭代次数|@Warmup
warmupBatchSize|预热批量的大小|@Warmup
warmupForks|预热模式：INDI，BULK，BULK_INDI|@Warmup
warmupMode|预热的模式|@Warmup
warmupTime|预热的时间|@Warmup
measurementIterations|测试的迭代次数|@Measurement
measurementBatchSize|测试批量的大小|@Measurement
measurementTime|测试的时间|@Measurement
mode|测试模式： Throughput（吞吐量）<br>， AverageTime（平均时间<br>），SampleTime（在测试中，随机进行采样执行的时间）<br>，SingleShotTime（在每次执行中计算耗时）<br>，All|@BenchmarkMode



### 示例`com.dean.started.actuator.StringBuilderRunner`执行结果

Benchmark                                | Mode|  Cnt|  Score |   Error|  Units
|:---|:---|:---|:---|:---|:---|
基准测试执行的方法|测试模式，这里是吞吐量|运行多少次|分数|错误|单位
StringConnectBenchmark.testStringAdd     | thrpt|    9|  547692723.688 ± 52100477.750|  |ops/s
StringConnectBenchmark.testStringBuffer  | thrpt|    9|  115049736.783 ±  5756397.679|  |ops/s
StringConnectBenchmark.testStringBuilder | thrpt|    9|  113579788.402 ± 26933870.488|  |ops/s
StringConnectBenchmark.testStringConcat  | thrpt|    9|   44802466.814 ±  5988576.716|  |ops/s
StringConnectBenchmark.testStringFormat  | thrpt|    9|    1062773.654 ±    70380.452|  |ops/s

![jmh-ui](https://note.youdao.com/yws/api/personal/file/WEBd7b4f523e96772021a9fb8bada348e87?method=download&shareKey=744c24105b7655927eda8a047bc48d1d)



### 结论
String直接相加 >StringBuffer >= StringBuilder >  StringConcat >> StringFormat
可见 StringBuffer 与 StringBuilder 大致性能相同，都比concat高几个数量级。
但是这里不管哪种都比 StringFormat 高N个数量级。所以String的Format方法一定要慎用、不用、禁用！！！


### 完整输出日志
```
# JMH version: 1.28
# VM version: JDK 1.8.0_162, Java HotSpot(TM) 64-Bit Server VM, 25.162-b12
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_162.jdk/Contents/Home/jre/bin/java
# VM options: -Dvisualvm.id=32707147562994 -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=59056:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Blackhole mode: full + dont-inline hint
# Warmup: 2 iterations, 10 s each
# Measurement: 3 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: com.dean.started.actuator.StringConnectBenchmark.testStringAdd

# Run progress: 0.00% complete, ETA 00:12:30
# Fork: 1 of 3
# Warmup Iteration   1: 449434430.095 ops/s
# Warmup Iteration   2: 453236773.408 ops/s
Iteration   1: 559378542.286 ops/s
Iteration   2: 542518226.725 ops/s
Iteration   3: 563011180.623 ops/s

# Run progress: 6.67% complete, ETA 00:11:56
# Fork: 2 of 3
# Warmup Iteration   1: 488375106.813 ops/s
# Warmup Iteration   2: 482878841.331 ops/s
Iteration   1: 586063325.886 ops/s
Iteration   2: 561139045.077 ops/s
Iteration   3: 488415824.044 ops/s

# Run progress: 13.33% complete, ETA 00:11:02
# Fork: 3 of 3
# Warmup Iteration   1: 475523485.748 ops/s
# Warmup Iteration   2: 463037797.851 ops/s
Iteration   1: 506106034.983 ops/s
Iteration   2: 564863769.926 ops/s
Iteration   3: 557738563.646 ops/s


Result "com.dean.started.actuator.StringConnectBenchmark.testStringAdd":
  547692723.688 ±(99.9%) 52100477.750 ops/s [Average]
  (min, avg, max) = (488415824.044, 547692723.688, 586063325.886), stdev = 31004158.608
  CI (99.9%): [495592245.938, 599793201.438] (assumes normal distribution)


# JMH version: 1.28
# VM version: JDK 1.8.0_162, Java HotSpot(TM) 64-Bit Server VM, 25.162-b12
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_162.jdk/Contents/Home/jre/bin/java
# VM options: -Dvisualvm.id=32707147562994 -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=59056:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Blackhole mode: full + dont-inline hint
# Warmup: 2 iterations, 10 s each
# Measurement: 3 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: com.dean.started.actuator.StringConnectBenchmark.testStringBuffer

# Run progress: 20.00% complete, ETA 00:10:12
# Fork: 1 of 3
# Warmup Iteration   1: 85243544.034 ops/s
# Warmup Iteration   2: 108307504.074 ops/s
Iteration   1: 111212300.939 ops/s
Iteration   2: 119824086.521 ops/s
Iteration   3: 116539554.790 ops/s

# Run progress: 26.67% complete, ETA 00:09:21
# Fork: 2 of 3
# Warmup Iteration   1: 96248003.612 ops/s
# Warmup Iteration   2: 115855461.406 ops/s
Iteration   1: 110685018.014 ops/s
Iteration   2: 114718627.704 ops/s
Iteration   3: 111106704.634 ops/s

# Run progress: 33.33% complete, ETA 00:08:29
# Fork: 3 of 3
# Warmup Iteration   1: 103236126.299 ops/s
# Warmup Iteration   2: 115640773.832 ops/s
Iteration   1: 115200635.842 ops/s
Iteration   2: 117361053.596 ops/s
Iteration   3: 118799649.003 ops/s


Result "com.dean.started.actuator.StringConnectBenchmark.testStringBuffer":
  115049736.783 ±(99.9%) 5756397.679 ops/s [Average]
  (min, avg, max) = (110685018.014, 115049736.783, 119824086.521), stdev = 3425539.925
  CI (99.9%): [109293339.103, 120806134.462] (assumes normal distribution)


# JMH version: 1.28
# VM version: JDK 1.8.0_162, Java HotSpot(TM) 64-Bit Server VM, 25.162-b12
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_162.jdk/Contents/Home/jre/bin/java
# VM options: -Dvisualvm.id=32707147562994 -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=59056:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Blackhole mode: full + dont-inline hint
# Warmup: 2 iterations, 10 s each
# Measurement: 3 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: com.dean.started.actuator.StringConnectBenchmark.testStringBuilder

# Run progress: 40.00% complete, ETA 00:07:38
# Fork: 1 of 3
# Warmup Iteration   1: 96294047.316 ops/s
# Warmup Iteration   2: 113728621.626 ops/s
Iteration   1: 117949540.850 ops/s
Iteration   2: 115034054.507 ops/s
Iteration   3: 123837179.651 ops/s

# Run progress: 46.67% complete, ETA 00:06:47
# Fork: 2 of 3
# Warmup Iteration   1: 101213561.930 ops/s
# Warmup Iteration   2: 113670599.606 ops/s
Iteration   1: 120737544.338 ops/s
Iteration   2: 119566475.755 ops/s
Iteration   3: 123736788.593 ops/s

# Run progress: 53.33% complete, ETA 00:05:56
# Fork: 3 of 3
# Warmup Iteration   1: 98926363.335 ops/s
# Warmup Iteration   2: 104592925.445 ops/s
Iteration   1: 109517378.073 ops/s
Iteration   2: 72486470.433 ops/s
Iteration   3: 119352663.418 ops/s


Result "com.dean.started.actuator.StringConnectBenchmark.testStringBuilder":
  113579788.402 ±(99.9%) 26933870.488 ops/s [Average]
  (min, avg, max) = (72486470.433, 113579788.402, 123837179.651), stdev = 16027914.303
  CI (99.9%): [86645917.914, 140513658.890] (assumes normal distribution)


# JMH version: 1.28
# VM version: JDK 1.8.0_162, Java HotSpot(TM) 64-Bit Server VM, 25.162-b12
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_162.jdk/Contents/Home/jre/bin/java
# VM options: -Dvisualvm.id=32707147562994 -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=59056:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Blackhole mode: full + dont-inline hint
# Warmup: 2 iterations, 10 s each
# Measurement: 3 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: com.dean.started.actuator.StringConnectBenchmark.testStringConcat

# Run progress: 60.00% complete, ETA 00:05:05
# Fork: 1 of 3
# Warmup Iteration   1: 45415475.764 ops/s
# Warmup Iteration   2: 46471740.595 ops/s
Iteration   1: 48371636.420 ops/s
Iteration   2: 45759743.213 ops/s
Iteration   3: 39793570.857 ops/s

# Run progress: 66.67% complete, ETA 00:04:14
# Fork: 2 of 3
# Warmup Iteration   1: 41010512.315 ops/s
# Warmup Iteration   2: 40725113.520 ops/s
Iteration   1: 42269767.734 ops/s
Iteration   2: 47677426.787 ops/s
Iteration   3: 48581580.503 ops/s

# Run progress: 73.33% complete, ETA 00:03:23
# Fork: 3 of 3
# Warmup Iteration   1: 36930379.586 ops/s
# Warmup Iteration   2: 40975651.621 ops/s
Iteration   1: 46468384.110 ops/s
Iteration   2: 39230457.015 ops/s
Iteration   3: 45069634.683 ops/s


Result "com.dean.started.actuator.StringConnectBenchmark.testStringConcat":
  44802466.814 ±(99.9%) 5988576.716 ops/s [Average]
  (min, avg, max) = (39230457.015, 44802466.814, 48581580.503), stdev = 3563705.946
  CI (99.9%): [38813890.097, 50791043.530] (assumes normal distribution)


# JMH version: 1.28
# VM version: JDK 1.8.0_162, Java HotSpot(TM) 64-Bit Server VM, 25.162-b12
# VM invoker: /Library/Java/JavaVirtualMachines/jdk1.8.0_162.jdk/Contents/Home/jre/bin/java
# VM options: -Dvisualvm.id=32707147562994 -javaagent:/Applications/IntelliJ IDEA.app/Contents/lib/idea_rt.jar=59056:/Applications/IntelliJ IDEA.app/Contents/bin -Dfile.encoding=UTF-8
# Blackhole mode: full + dont-inline hint
# Warmup: 2 iterations, 10 s each
# Measurement: 3 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: com.dean.started.actuator.StringConnectBenchmark.testStringFormat

# Run progress: 80.00% complete, ETA 00:02:32
# Fork: 1 of 3
# Warmup Iteration   1: 577245.042 ops/s
# Warmup Iteration   2: 891512.956 ops/s
Iteration   1: 1019538.279 ops/s
Iteration   2: 1065980.378 ops/s
Iteration   3: 1094662.080 ops/s

# Run progress: 86.67% complete, ETA 00:01:41
# Fork: 2 of 3
# Warmup Iteration   1: 983771.770 ops/s
# Warmup Iteration   2: 1089531.634 ops/s
Iteration   1: 1052870.323 ops/s
Iteration   2: 1135759.669 ops/s
Iteration   3: 1068728.586 ops/s

# Run progress: 93.33% complete, ETA 00:00:50
# Fork: 3 of 3
# Warmup Iteration   1: 1022711.988 ops/s
# Warmup Iteration   2: 1009245.680 ops/s
Iteration   1: 990767.579 ops/s
Iteration   2: 1082981.965 ops/s
Iteration   3: 1053674.028 ops/s


Result "com.dean.started.actuator.StringConnectBenchmark.testStringFormat":
  1062773.654 ±(99.9%) 70380.452 ops/s [Average]
  (min, avg, max) = (990767.579, 1062773.654, 1135759.669), stdev = 41882.278
  CI (99.9%): [992393.202, 1133154.106] (assumes normal distribution)


# Run complete. Total time: 00:12:43

REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
experiments, perform baseline and negative tests that provide experimental control, make sure
the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
Do not assume the numbers tell you what you want them to tell.

Benchmark                                  Mode  Cnt          Score          Error  Units
StringConnectBenchmark.testStringAdd      thrpt    9  547692723.688 ± 52100477.750  ops/s
StringConnectBenchmark.testStringBuffer   thrpt    9  115049736.783 ±  5756397.679  ops/s
StringConnectBenchmark.testStringBuilder  thrpt    9  113579788.402 ± 26933870.488  ops/s
StringConnectBenchmark.testStringConcat   thrpt    9   44802466.814 ±  5988576.716  ops/s
StringConnectBenchmark.testStringFormat   thrpt    9    1062773.654 ±    70380.452  ops/s
```


### refer to 

- https://cloud.tencent.com/developer/article/1610483
- https://www.zhihu.com/question/276455629
- https://dunwu.github.io/javatech/test/jmh.html#%E4%BB%80%E4%B9%88%E6%98%AF%E5%9F%BA%E5%87%86%E6%B5%8B%E8%AF%95
