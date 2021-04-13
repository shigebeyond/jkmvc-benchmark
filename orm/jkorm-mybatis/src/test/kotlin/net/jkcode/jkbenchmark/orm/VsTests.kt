package net.jkcode.jkbenchmark.orm

import net.jkcode.jkbenchmark.IBenchmarkPlayer
import net.jkcode.jkbenchmark.orm.jkorm.JkormBenchmarkPlayer
import net.jkcode.jkbenchmark.orm.mybatis.MybatisBenchmarkPlayer
import net.jkcode.jkmvc.db.Db
import org.junit.Test

class VsTests {
    val action = "add"

    @Test
    fun runJkormPlayer(){
        runPlayer(JkormBenchmarkPlayer())
    }

    @Test
    fun runMybatisPlayer(){
        runPlayer(MybatisBenchmarkPlayer())
    }

    fun runPlayer(player: IBenchmarkPlayer){
        println("${player.name} test")
        var alltime = 0L
        var total = 0L
        val n =  30
        for(i in 1..n) {
            if(action == "add"){
                Db.instance().execute("truncate table employee")
                Db.instance().execute("truncate table employee2")
                Db.instance().execute("truncate table department")
            }

            val f = player.getSyncAction(action)
            val start = System.currentTimeMillis()
            val num = f(0) as Int // 结果是db操作次数
            val costtime = System.currentTimeMillis() - start
            alltime += costtime
            total += num
            println("第${i}次执行[$action]动作耗时: " + costtime + " ms")

            if(action == "delete"){
                Db.instance().execute("insert into employee select * from employee2")
            }
        }
        if(action == "add") {
            Db.instance().execute("insert into employee2 select * from employee")
        }

        println("共耗时: " + alltime + " ms")
        println("动作次数: " + n)
        println("db操作次数: " + total)
        println("每次动作平均耗时: " + (alltime.toDouble() / n) + " ms")
        println("每次db操作平均耗时: " + (alltime.toDouble() / total) + " ms")
    }
}