package net.jkcode.jkbenchmark.orm.jkorm

import net.jkcode.jkbenchmark.BenchmarkApp
import net.jkcode.jkbenchmark.orm.BaseBenchmarkPlayer
import net.jkcode.jkbenchmark.orm.jkorm.model.Department
import net.jkcode.jkbenchmark.orm.jkorm.model.Employee
import net.jkcode.jkutil.common.randomBoolean
import net.jkcode.jkutil.common.randomInt
import net.jkcode.jkutil.common.randomString

/**
 * jkorm的玩家
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-11-22 2:53 PM
 */
class JkormBenchmarkPlayer : BaseBenchmarkPlayer {

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            // 运行app
            val player = JkormBenchmarkPlayer()
            BenchmarkApp(player).run()
        }
    }

    /**
     * 玩家名
     */
    override val name: String = "jkorm"

    /**
     * 获得同步动作
     */
    override fun getSyncAction(action: String): (Int) -> Any? {
        return when (action) {
            "add" -> this::add
            "update" -> this::update
            "delete" -> this::delete
            "getDepWithEmps" -> this::getDepWithEmps
            "getEmpsByConditionIf" -> this::getEmpsByConditionIf
            "updateEmpOnDynFields" -> this::updateEmpOnDynFields
            "getEmpsByIds" -> this::getEmpsByIds
            else -> throw Exception("不能识别action配置: " + action)
        }
    }

    /**
     * 新增
     *   100个部门+1000个员工
     */
    public fun add(i: Int): Int {
        Department.db.transaction {
            // 新增部门
            val dep = Department()
            dep.id = Integer(i)
            dep.title = "部" + i
            dep.intro = ""
            dep.create()

            // 新增员工
            val isMan = randomBoolean()
            val title = (if (isMan) "Mr " else "Miss ") + randomString(5);
            val gender = if (isMan) "男" else "女";
            val emp = Employee();
            emp.id = Integer(i)
            emp.title = title
            emp.email = "$title@qq.com"
            emp.gender = gender
            emp.depId = dep.id
            emp.create()
        }

        return 2
    }

    /**
     * 更新
     *   1000次
     */
    public fun update(i: Int): Int {
        val emp = Employee.findByPk<Employee>(i)!!
        val isMan = emp.gender == "男"
        val title = (if (isMan) "Mr " else "Miss ") + randomString(5)
        emp.title = title;
        emp.update()
        return 2
    }

    /**
     * 删除
     *    1000次
     */
    public fun delete(i: Int): Int {
        val emp = Employee.findByPk<Employee>(i)
        if (emp != null)
            emp.delete()

        return 2
    }

    /**
     * 部门联查员工
     */
    public fun getDepWithEmps(i: Int): Int {
        val dep = Department.queryBuilder()
                .where("id", i)
                .findModel<Department>()

        val emps = dep?.emps
        return 2
    }

    /**
     * 条件查询
     */
    public fun getEmpsByConditionIf(i: Int): Int {
        val query = Employee.queryBuilder()
        if (i % 2 == 1)
            query.where("id", randomInt(1000))
        else
            query.where("gender", if (randomBoolean()) "男" else "女")
        val emps = query
                .orderBy("id", true)
                .limit(10, 0)
                .findModels<Employee>()

        return 1
    }

    /**
     * 更新动态字段
     */
    public fun updateEmpOnDynFields(i: Int): Int {
        // 先查后改
        val emp = Employee.findByPk<Employee>(i)
        if (emp != null) {
            if (i % 2 == 1) {
                val isMan = emp.gender == "男"
                emp.title = (if (isMan) "Mr " else "Miss ") + randomString(5);
            } else {
                emp.email = randomString(5) + "@qq.com"
            }

            emp.update()
        }

        return 2
    }

    /**
     * 多id查询(循环多id拼where in)
     */
    public fun getEmpsByIds(i: Int): Int {
        val ids = listOf(randomInt(1000), randomInt(1000), randomInt(1000))
        val emps = Employee.queryBuilder().where("id", ids).findModels<Employee>()
        return 1
    }

}