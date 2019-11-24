package net.jkcode.jkbenchmark.orm.orm

import net.jkcode.jkutil.common.Config
import org.slf4j.LoggerFactory
import java.text.MessageFormat

/**
 * 测试入口
 * @author shijianhang<772910474@qq.com>
 * @date 2019-11-22 12:02 PM
 */
object BenchmarkLauncher {

    /**
     * 调试的配置
     */
    public val appConfig: Config = Config.instance("app", "yaml")

    /**
     * 所有场景测试的过程日志
     */
    public val roundLogger = LoggerFactory.getLogger("net.jkcode.jkbenchmark.orm.round")

    /**
     * 所有场景测试的结果日志
     * 格式: 2019-11-20 17:00:28 [INFO] file-c10-n40000-syn | Runtime: 1431.09 ms, Avg TPS: 27950.64, Avg RT: 0.36ms
     */
    public val resultLogger = LoggerFactory.getLogger("net.jkcode.jkbenchmark.orm.result")

    /**
     * 列出所有场景的配置
     * @return
     */
    public fun listAllConfigs(): ArrayList<Config> {
        val allConfig = Config.instance("benchmarks", "yaml")
        val configs = ArrayList<Config>()

        // 构建每个场景的配置
        val requestses:List<Int> = allConfig["requests"]!!
        val actions:List<String> = allConfig["action"]!!

        for (requests in requestses)
            for (action in actions){
                    val map = mapOf<String, Any>(
                            "requests" to requests, // 请求数
                            "action" to action // 动作
                    )
                    configs.add(Config(map))
                }
        return configs
    }

    /**
     * 运行性能测试
     */
    public fun runTest(){
        if(appConfig["all"]!!)
            runAllTest()
        else
            run1Test()
    }

    /**
     * 运行 benchmark.yaml 单一场景的性能测试
     */
    private fun run1Test(){
        val test = BenchmarkTest(Config.instance("benchmark", "yaml"))
        val result = test.run()
    }

    /**
     * 运行 benchmarks.yaml 所有场景的性能测试
     *    全自动化测试, 多测几遍, 取最优
     *
     */
    private fun runAllTest(){
        for(config in listAllConfigs()) {
            roundLogger.info("----------Benchmark Statistics--------------\n${config.props}\n")
            // 尝试多遍
            val results = ArrayList<BenchmarkResult>()
            val roundCount: Int = appConfig["roundCount"]!!
            if(roundCount < 1)
                throw Exception("配置项[roundCount]必须为正整数")
            for(i in 0 until roundCount) {
                // 测试
                val test = BenchmarkTest(config)
                val result = test.run()
                results.add(result)
                // 直接打印
                roundLogger.info("+++ Round ${i + 1} result: \n$result\n")
                Thread.sleep(2000)
            }
            // 取最优结果: 耗时最短
            val bestResult = results.min()!!
            roundLogger.info(">>> Best result: \n$bestResult\n")
            resultLogger.info(toSummary(config) + " | " + bestResult)
        }
    }

    /**
     * 简写配置名
     * @param config
     * @return
     */
    private fun toSummary(config: Config): String {
        val action: String = config["action"]!! // 动作
        val requests: Int = config["requests"]!! // 请求数
        return "$action-n$requests"
    }

    @JvmStatic
    fun main(args: Array<String>) {
        runTest()
    }

}