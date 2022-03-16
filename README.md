# 一、性能测试: jkorm vs jdbc
主要是对orm框架`jkorm`的查询处理进行性能测试，并与jdbc的查询处理做性能对比。

## 测试环境

### 硬件配置

一台老的测试机, 机器上还跑着其他服务, 凑合测试

     CPU：model name:Intel(R) Xeon(R) CPU E5-2620 v2 @ 2.10GHz,cache size: 15360 KB,processor_count : 24
     内存：16G
     网络：千兆网卡
     硬盘：900GB

### 软件配置

     mysql版本： 做db server
     5.6.21

     JDK版本：做db client
     java version "1.8.0_172"
     Java(TM) SE Runtime Environment (build 1.8.0_172-b11)
     Java HotSpot(TM) 64-Bit Server VM (build 25.172-b11, mixed mode)

     JVM参数：
     java -Djava.net.preferIPv4Stack=true -server -Xms1g -Xmx1g -XX:PermSize=128m

## 测试脚本(db client)

单线程同步调用, 请求数分别为1w/5w/10w的情况下，分别进行如下场景测试, 每种测试进行5轮, 最优结果(耗时最短)：
  - native: 使用原生jdbc来查询
  - db：使用 `net.jkcode.jkmvc.db.Db` 来查询, 对原生jdbc进行封装并简化调用, 理论上性能接近于但小于 native
  - query: 使用 `net.jkcode.jkmvc.query.DbQueryBuilder` 来查询, 用查询构建器来生成sql, 每次调用都创建新的 DbQueryBuilder 实例
  - orm: 使用 `net.jkcode.jkmvc.orm.OrmQueryBuilder` 来查询, 对 DbQueryBuilder 进行封装并添加orm特性, 理论上性能接近于但小于 query
  - queryReuse: 复用 `net.jkcode.jkmvc.query.DbQueryBuilder` 来查询, 复用 DbQueryBuilder 实例, 不用频繁创建, 理论上性能优于 query
  - queryCompiled: 编译好 `net.jkcode.jkmvc.query.DbQueryBuilder` 来查询, 编译好sql直接调用, 理论上性能接近于但小于 db

## 测试代码

```
    /**
     * 原生 | sql
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
     * db | sql
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
```

## 测试结果

`select action as `测试场景`, requests as `请求数`, run_time as `执行时间(ms)`, tps as `平均tps`, rt as `平均响应时间(ms)` from benchmark_result;`

### 场景1: db server/client跑在同一台机器

| 测试场景      | 请求数    | 执行时间(ms)     | 平均tps   | 平均响应时间(ms)       |
|---------------|-----------|------------------|-----------|------------------------|
| native        |     10000 |           499.15 |  20034.21 |                   0.05 |
| db            |     10000 |           585.95 |  17066.34 |                   0.06 |
| orm           |     10000 |          1523.95 |   6561.90 |                   0.15 |
| query         |     10000 |          1611.32 |   6206.10 |                   0.16 |
| queryReuse    |     10000 |           676.88 |  14773.63 |                   0.07 |
| queryCompiled |     10000 |           697.97 |  14327.28 |                   0.07 |
| native        |     50000 |          2460.43 |  20321.65 |                   0.05 |
| db            |     50000 |          2407.91 |  20764.88 |                   0.05 |
| orm           |     50000 |          7224.39 |   6921.00 |                   0.14 |
| query         |     50000 |          7521.75 |   6647.39 |                   0.15 |
| queryReuse    |     50000 |          3318.47 |  15067.17 |                   0.07 |
| queryCompiled |     50000 |          2500.36 |  19997.10 |                   0.05 |
| native        |    100000 |          4895.44 |  20427.17 |                   0.05 |
| db            |    100000 |          5304.62 |  18851.51 |                   0.05 |
| orm           |    100000 |         15228.74 |   6566.53 |                   0.15 |
| query         |    100000 |         15666.36 |   6383.10 |                   0.16 |
| queryReuse    |    100000 |          6549.95 |  15267.28 |                   0.07 |
| queryCompiled |    100000 |          5314.00 |  18818.22 |                   0.05 |

### 结论

直接看请求数据为 10w 的结果, 性能比较为:

native > db > queryCompiled > queryReuse > query > orm

其中 native / db / queryCompiled 性能比较接近, 是性能最好的

其中 queryCompiled 最好时 tps 为2w, 响应时间为 0.05 ms, 跟 native 是一个量级

### 场景2: db server/client在不同机器

| 测试场景      | 请求数    | 执行时间(ms)     | 平均tps   | 平均响应时间(ms)       |
|---------------|-----------|------------------|-----------|------------------------|
| native        |     10000 |          1376.62 |   7264.18 |                   0.14 |
| db            |     10000 |          1347.01 |   7423.83 |                   0.13 |
| orm           |     10000 |          1901.89 |   5257.92 |                   0.19 |
| query         |     10000 |          1885.46 |   5303.74 |                   0.19 |
| queryReuse    |     10000 |          1441.66 |   6936.47 |                   0.14 |
| queryCompiled |     10000 |          1357.16 |   7368.32 |                   0.14 |
| native        |     50000 |          6811.39 |   7340.65 |                   0.14 |
| db            |     50000 |          6747.65 |   7409.99 |                   0.13 |
| orm           |     50000 |          9496.13 |   5265.30 |                   0.19 |
| query         |     50000 |          9478.73 |   5274.97 |                   0.19 |
| queryReuse    |     50000 |          7187.33 |   6956.68 |                   0.14 |
| queryCompiled |     50000 |          6784.60 |   7369.63 |                   0.14 |
| native        |    100000 |         13669.10 |   7315.77 |                   0.14 |
| db            |    100000 |         13488.98 |   7413.46 |                   0.13 |
| orm           |    100000 |         19001.80 |   5262.66 |                   0.19 |
| query         |    100000 |         18922.94 |   5284.59 |                   0.19 |
| queryReuse    |    100000 |         14414.19 |   6937.61 |                   0.14 |
| queryCompiled |    100000 |         13587.09 |   7359.93 |                   0.14 |

注:

网络IO果然是大头, 不过该测试主要是针对各类 db client(原生jdbc/Db/DbQuery/Orm) 来进行性能测试, 主要目的是看jkmvc封装高级的 db client 跟原生jdbc的差距. 但如果网络IO占大头, 反而不容易评估各个 db client 的性能差异, 因此该测试结果没啥用

两个结果一对比, 得到的各个阶段的时间有些冲突的, 但是注意的是场景1(一台机器), 还跑着其他服务, 这能解释时间冲突

从场景2的响应时间可知: native=0.14ms, query=0.19ms, 也就是 query - native = 0.05ms, 就表示 query 比 native 多了 0.05ms 的运算, query 多出来的运算大多是生成sql的字符串运算

代入到场景1: 由 native=0.05ms, 应该推到 query=0.10ms, 但实际是 query=0.16ms, 实际是 query 比 native 多了 0.11ms 的运算

=> 由 db server的那台机器运行着较多进程(本身就是一个mysql server), 而 query 多运算, 属于计算密集型, 而较多进程导致进程切换, 从而导致 query 所分到的cpu时间少了, 导致 query 运算时间相对延长

响应时间中, 网络耗时大概是 0.07ms

# 二、性能测试: jkorm vs mybatis
主要是对2个orm框架`jkorm`与`mybatis`做性能测试与对比
