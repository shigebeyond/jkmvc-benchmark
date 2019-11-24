package net.jkcode.jkbenchmark.orm

import net.jkcode.jkbenchmark.BenchmarkApp
import net.jkcode.jkbenchmark.IBenchmarkPlayer
import net.jkcode.jkbenchmark.orm.orm.common.MessageEntity
import net.jkcode.jkbenchmark.orm.orm.common.MessageModel
import net.jkcode.jkmvc.db.Db
import net.jkcode.jkmvc.db.queryResult
import net.jkcode.jkmvc.query.CompiledSql
import net.jkcode.jkmvc.query.DbExpr
import net.jkcode.jkmvc.query.DbQueryBuilder
import net.jkcode.jkutil.common.randomInt

/**
 * jkorm的玩家
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-11-22 2:53 PM
 */
class JkormBenchmarkPlayer: IBenchmarkPlayer{

    /**
     * 玩家名
     */
    override val name: String = "jkorm"

    /**
     * 获得同步动作
     */
    override fun getSyncAction(action: String): (Int) -> Any? {
        return when(action){
            "native"-> this::getMessageByNative
            "db"-> this::getMessageByDb
            "orm"-> this::getMessageByOrm
            "query"-> this::getMessageByQuery
            "queryReuse"-> this::getMessageByQueryReuse
            "queryCompiled"-> this::getMessageByQueryCompiled
            else -> throw Exception("不能识别action配置: " + action)
        }
    }

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

}