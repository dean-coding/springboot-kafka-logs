package com.dean.started.actuator;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * 入门使用
 *
 * @author Dean
 * @date 2021-08-24
 */
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


    /**
     * 也可以加在方法上
     * Warmup: 预热运行1次，每次2s
     * Threads: 每次多少线程来执行
     * Fork: 代表启动多个单独的进程分别测试
     * Measurement： 运行2次，每次2s
     * BenchmarkMode: 基准测试的模式
     * <ul>
     * AverageTime，表示每次执行时间
     * SampleTime表示采样时间
     * SingleShotTime表示只运行一次，用于测试冷启动消耗时间
     * All表示统计前面的所有指标
     * </ul>
     * State(value = Scope.Benchmark)：基准测试内共享对象
     * Scope.Group)：同一个线程组内共享
     * Scope.Thread)：同一个线程内共享
     * <p>
     * Setup & TearDown: 初始化和销毁
     */
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
