package net.jkcode.jkmvc.benchmark.orm.analyze

import net.jkcode.jkmvc.common.travel
import java.io.File
import java.lang.IllegalArgumentException

/**
 * 测试结果分析
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-11-20 21:20:33
 */
object BenchmarkResultAnalyzer {

    /**
     * 解析结果日志
     * 格式: 2019-11-22 17:31:53 [INFO] db-n10000 | RunTime: 518.22 ms, Avg TPS: 19296.8, Avg RT: 0.05 ms
     * @param log
     * @return
     */
    public fun parseResultLog(logDir: String): List<BenchmarkResultModel> {
        val dir = File(logDir)
        if(!dir.exists())
            throw IllegalArgumentException("目录不存在: logDir")

        val results = ArrayList<BenchmarkResultModel>()
        dir.travel {f ->
            if(f.isFile && f.name.startsWith("result.log")){
                println("解析文件: $f")
                val reg = "[^\\[]+\\[(TRACE|DEBUG|INFO|WARN|ERROR)\\] (\\w+)-n(\\d+) \\| RunTime: ([\\d\\.]+) ms, Avg TPS: ([\\d\\.]+), Avg RT: ([\\d\\.]+) ms".toRegex()
                f.forEachLine { line ->
                    val m = reg.find(line)!!
                    val result = BenchmarkResultModel()
                    result.action = m.groups[2]!!.value
                    result.requests = m.groups[3]!!.value.toInt()
                    result.runTime = m.groups[4]!!.value.toDouble()
                    result.tps = m.groups[5]!!.value.toDouble()
                    result.rt = m.groups[6]!!.value.toDouble()
                    result.create()
                    results.add(result)
                }
            }
        }

        return results
    }

    @JvmStatic
    fun main(args: Array<String>) {
        //parseResultLog("/oldhome/shi/code/java/benchmark-log/jkorm")
        parseResultLog("/oldhome/shi/code/java/benchmark-log/jkorm2")
    }
}