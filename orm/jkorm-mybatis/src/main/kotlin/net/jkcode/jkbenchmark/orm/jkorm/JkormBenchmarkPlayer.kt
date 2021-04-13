package net.jkcode.jkbenchmark.orm.jkorm

import net.jkcode.jkbenchmark.IBenchmarkPlayer
import net.jkcode.jkbenchmark.orm.jkorm.model.Department
import net.jkcode.jkbenchmark.orm.jkorm.model.Employee
import net.jkcode.jkmvc.db.Db
import net.jkcode.jkutil.common.randomBoolean
import net.jkcode.jkutil.common.randomInt
import net.jkcode.jkutil.common.randomString

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

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            val player = JkormBenchmarkPlayer()
            val action = "update"
            val f = player.getSyncAction(action)
            val start = System.currentTimeMillis()
            f(0)
            val costtime = System.currentTimeMillis() - start
            println("执行[$action]动作耗时: " + costtime + " ms")
        }
    }

    /**
     * 获得同步动作
     */
    override fun getSyncAction(action: String): (Int) -> Any? {
        return when(action){
            "add"-> this::add
            "update"-> this::update
            "delete"-> this::delete
            "getDepWithEmp"-> this::getDepWithEmp
            "getEmpsByConditionIf"-> this::getEmpsByConditionIf
            "updateEmpOnDynFields"-> this::updateEmpOnDynFields
            "getEmpsByIds"-> this::getEmpsByIds
            else -> throw Exception("不能识别action配置: " + action)
        }
    }

    /**
     * 新增
     *   100个部门+1000个员工
     */
    public fun add(i: Int){
        // 清空表
        Db.instance().execute(" truncate table department")
        Db.instance().execute(" truncate table employee")

        Department.db.transaction {
            for(i in 1..100) {
                // 新增部门
                val dep = Department()
                dep.id = Integer(i)
                dep.title = "部" + i
                dep.intro  = ""
                dep.create()

                // 新增员工
                for(j in 1..10) {
                    val isMan = randomBoolean()
                    val title = (if(isMan) "Mr " else "Miss ") + randomString(5);
                    val gender = if(isMan) "男" else "女";
                    val emp = Employee();
                    emp.id = Integer((i-1)*100+j)
                    emp.title = title
                    emp.email = "$title@qq.com"
                    emp.gender = gender
                    emp.depId = dep.id
                    emp.create()
                }
            }
        }
    }

    /**
     * 更新
     *   1000次
     */
    public fun update(i: Int){
        Employee.db.transaction {
            for (i in 1..1000) {
                val emp = Employee.findByPk<Employee>(i)!!
                val isMan = emp.gender == "男"
                emp.title = (if (isMan) "Mr " else "Miss ") + randomString(5);
                emp.update()
            }
        }
    }

    /**
     * 删除
     *    1000次
     */
    public fun delete(i: Int){
        Employee.db.transaction {
            for (i in 1..1000) {
                val emp = Employee.findByPk<Employee>(1)
                if(emp != null)
                    emp.delete()
            }
        }
    }

    /**
     * 部门联查员工
     */
    public fun getDepWithEmp(i: Int){
        for(i in 1..100) {
            Department.queryBuilder()
                    .with("emps")
                    .findModels<Department>()
        }
    }

    /**
     * 条件查询
     */
    public fun getEmpsByConditionIf(i: Int){
        for(i in 1..100) {
            val query = Employee.queryBuilder()
            if (i % 2 == 1)
                query.where("id", randomInt(1000))
            else
                query.where("gender", if (randomBoolean()) "男" else "女")
            val emps = query.findModels<Employee>()
        }
    }

    /**
     * 更新动态字段
     */
    public fun updateEmpOnDynFields(i: Int){
        Employee.db.transaction {
            for (i in 1..100) {
                // 先查后改
                val emp = Employee.findByPk<Employee>(i)
                if (emp == null)
                    continue

                if (i % 2 == 1) {
                    val isMan = emp.gender == "男"
                    emp.title = (if (isMan) "Mr " else "Miss ") + randomString(5);
                } else {
                    emp.email = randomString(5) + "@qq.com"
                }

                emp.update()
            }
        }
    }

    /**
     * 多id查询(循环多id拼where in)
     */
    public fun getEmpsByIds(i: Int){
        for(i in 1..100) {
            val ids = listOf(randomInt(1000), randomInt(1000), randomInt(1000))
            val emps = Employee.queryBuilder().where("id", ids).findModels<Employee>()
        }
    }

}