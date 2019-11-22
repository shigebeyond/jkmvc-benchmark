package net.jkcode.jkmvc.benchmark.orm

import net.jkcode.jkmvc.common.Config
import net.jkcode.jkmvc.benchmark.orm.common.MessageEntity
import net.jkcode.jkmvc.benchmark.orm.common.MessageModel
import net.jkcode.jkmvc.db.Db
import net.jkcode.jkmvc.db.queryResult
import net.jkcode.jkmvc.query.CompiledSql
import net.jkcode.jkmvc.query.DbExpr
import net.jkcode.jkmvc.query.DbQueryBuilder
import org.slf4j.LoggerFactory
import java.text.MessageFormat

/**
 * 性能测试结果
 */
class BenchmarkResult(
        public val total: Int,
        public val runTime: Double
): Comparable<BenchmarkResult>{

    override fun toString(): String {
        return MessageFormat.format("RunTime: {0,number,#.##} ms, Avg TPS: {1,number,#.##}, Avg RT: {2,number,#.##} ms", runTime, total.toDouble() / runTime * 1000, runTime / total)
    }

    override fun compareTo(other: BenchmarkResult): Int {
        return (this.runTime - other.runTime).toInt()
    }
}

/**
 * orm性能测试
 *    纯sql vs orm : 执行10w次, 看执行时间差
 *
 *    纯sql: 22164.07 ms
 *    orm:  68296.76 ms
 */
class BenchmarkTest(public val config: Config){

    /**
     * 日志
     */
    public val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * 原生 + sql
     */
    public fun getMessageByNative(id: Int): MessageEntity?{
        return Db.instance().conn.queryResult("select * from message where id = $id", emptyList()) { rs ->
            if(rs.next()) {
                val msg = MessageEntity()
                msg.id = rs.getInt("id")
                msg.fromUid = rs.getInt("from_uid")
                msg.toUid = rs.getInt("to_uid")
                msg.content = rs.getString("content")
                msg
            }else
                null
        }
    }

    /**
     * db + sql
     */
    public fun getMessageByDb(id: Int): MessageEntity?{
        return Db.instance().queryRow("select * from message where id = $id", emptyList()) { row ->
            val msg = MessageEntity()
            msg.fromRow(row, true)
            msg
        }
    }

    /**
     * orm query
     *
     * 通过jprofile得知, 相对于纯sql, 内存消耗大
     * 其中 char是5w个(是纯sql的5倍), String
     */
    public fun getMessageByOrm(id: Int): MessageEntity?{
        return MessageModel.queryBuilder().where("id", "=", id).findEntity<MessageModel, MessageEntity>()
    }

    /**
     * db query
     *
     * 通过jprofile得知, 内存跟orm差不多
     * findEntity() 跟 findRow{} 差不多
     */
    public fun getMessageByQuery(id: Int): MessageEntity?{
        return DbQueryBuilder().table("message").where("id", "=", id).findRow { row ->
            val msg = MessageEntity()
            msg.fromRow(row, true)
            msg
        }
    }

    /**
     * 复用的db query
     *
     * 通过jprofile得知, 内存跟纯sql差不多
     * 性能在 纯sql 与 orm 之间
     */
    val query: DbQueryBuilder by lazy {
        DbQueryBuilder()
    }
    public fun getMessageByQueryReuse(id: Int): MessageEntity?{
        query.clear()
        return query.table("message").where("id", "=", id).findRow { row ->
            val msg = MessageEntity()
            msg.fromRow(row, true)
            msg
        }
    }

    /**
     * 编译后的 db query
     */
    val csql: CompiledSql by lazy{
        DbQueryBuilder().table("message").where("id", "=", DbExpr.question).compileSelectOne()
    }
    public fun getMessageByQueryCompiled(id: Int): MessageEntity?{
        return csql.findRow(listOf(id)) { row ->
            val msg = MessageEntity()
            msg.fromRow(row, true)
            msg
        }
    }

    /**
     * 执行测试
     * @return 运行时间
     */
    private fun test(action: (Int)-> MessageEntity?): BenchmarkResult {
        logger.info("Test ${config.props}")

        // 热身
        warmup(action)

        // 正式测试
        val start = System.nanoTime()
        val requests: Int = config["requests"]!!
        for(i in 0..requests) {
            val msg = action.invoke(i % 10 + 1)
        }
        val runTime = System.nanoTime() - start
        val runMs = runTime.toDouble() / 1000000L
        val result = BenchmarkResult(requests, runMs)
        logger.info("----------Benchmark Statistics--------------\n${config.props}\n$result\n")
        return result
    }

    /**
     * 热身
     */
    private fun warmup(action: (Int)-> MessageEntity?) {
        val start = System.nanoTime()
        for(i in 0..1000) {
            val msg = action.invoke(i % 10 + 1)
        }
        val runTime = System.nanoTime() - start
        val runMs = runTime.toDouble() / 1000000L
        logger.info(MessageFormat.format("Warmup cost: {0,number,#.##} ms", runMs))
    }

    /**
     * 运行测试场景
     * @return 运行时间
     */
    public fun run(): BenchmarkResult {
        val action: (Int)-> MessageEntity? = when(config.getString("action")!!){
            "native"-> this::getMessageByNative
            "db"-> this::getMessageByDb
            "orm"-> this::getMessageByOrm
            "query"-> this::getMessageByQuery
            "queryReuse"-> this::getMessageByQueryReuse
            "queryCompiled"-> this::getMessageByQueryCompiled
            else -> throw Exception("不能识别action配置: " + config.getString("action"))
        }

        return test(action)
    }
}



