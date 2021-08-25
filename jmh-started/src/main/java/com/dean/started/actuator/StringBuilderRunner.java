package com.dean.started.actuator;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * 这个 runner 类的作用，就是启动基准测试
 *
 * <p>1.通过命令行使用maven命令执行,这种适合对于大型基准测试</p>
 * <p>2.还提供了一种通过 Main方法运行的方式</p>
 * org.openjdk.jmh.runner.Runner 类去运行 org.openjdk.jmh.runner.options.Options 实例
 *
 * @author Dean
 * @date 2021-08-20
 */
public class StringBuilderRunner {

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

    /**
     * 字符串拼接之 StringBuilder 基准测试
     */
    @Benchmark
    public void testStringBuilder() {
        print(new StringBuilder().append(1).append(2).append(3).toString());
    }

    /**
     * 字符串拼接之直接相加基准测试
     */
    @Benchmark
    public void testStringAdd() {
        print("" + 1 + 2 + 3);
    }

    /**
     * 字符串拼接之String Concat基准测试
     */
    @Benchmark
    public void testStringConcat() {
        print("".concat("1").concat("2").concat("3"));
    }

    /**
     * 字符串拼接之 StringBuffer 基准测试
     */
    @Benchmark
    public void testStringBuffer() {
        print(new StringBuffer().append(1).append(2).append(3).toString());
    }

    /**
     * 字符串拼接之 StringFormat 基准测试
     */
    @Benchmark
    public void testStringFormat() {
        print(String.format("%s%s%s", 1, 2, 3));
    }

    private void print(String str) {
    }

}