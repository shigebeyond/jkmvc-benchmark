package net.jkcode.jkbenchmark.orm

import net.jkcode.jkbenchmark.BenchmarkScene
import net.jkcode.jkbenchmark.IBenchmarkPlayer
import net.jkcode.jkmvc.db.Db
import org.slf4j.LoggerFactory

/**
 * 基础玩家
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-11-22 2:53 PM
 */
interface BaseBenchmarkPlayer: IBenchmarkPlayer{

    companion object {
        /**
         * 日志
         */
        public val logger = LoggerFactory.getLogger(this.javaClass)
    }

    override fun beforeScene(action: String) {
        if(action == "add"){
            logger.info("清表employee/employee2/department")
            Db.instance().execute("truncate table employee")
            Db.instance().execute("truncate table employee2")
            Db.instance().execute("truncate table department")
        }
    }

    override fun afterScene(action: String) {
        if(action == "delete"){
            logger.info("复制数据到表employee")
            Db.instance().execute("insert into employee select * from employee2 where id not in (select id from employee) ")
        }
        if(action == "add") {
            logger.info("复制数据到表employee2")
            Db.instance().execute("insert into employee2 select * from employee")
        }
    }
}