package net.jkcode.jkbenchmark.orm.mybatis

import net.jkcode.jkbenchmark.BenchmarkApp
import net.jkcode.jkbenchmark.orm.BaseBenchmarkPlayer
import net.jkcode.jkbenchmark.orm.jkorm.JkormBenchmarkPlayer
import net.jkcode.jkbenchmark.orm.mybatis.dao.DepartmentDao
import net.jkcode.jkbenchmark.orm.mybatis.dao.EmployeeDao
import net.jkcode.jkbenchmark.orm.mybatis.model.Department
import net.jkcode.jkbenchmark.orm.mybatis.model.Employee
import net.jkcode.jkutil.common.randomBoolean
import net.jkcode.jkutil.common.randomInt
import net.jkcode.jkutil.common.randomString
import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.SqlSession
import org.apache.ibatis.session.SqlSessionFactory
import org.apache.ibatis.session.SqlSessionFactoryBuilder
import java.io.Reader

/**
 * mybatis的玩家
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2019-11-22 2:53 PM
 */
class MybatisBenchmarkPlayer : BaseBenchmarkPlayer {

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            // 运行app
            val player = MybatisBenchmarkPlayer()
            BenchmarkApp(player).run()
        }
    }

    /**
     * 玩家名
     */
    override val name: String = "mybatis"

    /**
     * mybatis dao
     */
    private var reader: Reader = Resources.getResourceAsReader("Configuration.xml")
    private var sqlSessionFactory: SqlSessionFactory = SqlSessionFactoryBuilder().build(reader)
    private var session: SqlSession = sqlSessionFactory.openSession()
    val depDao: DepartmentDao = session.getMapper(DepartmentDao::class.java)
    val empDao: EmployeeDao = session.getMapper(EmployeeDao::class.java)

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
        // 新增部门
        val dep = Department(i, "部" + i, "")
        depDao.addDep(dep)

        // 新增员工
        val isMan = randomBoolean()
        val title = (if (isMan) "Mr " else "Miss ") + randomString(5);
        val gender = if (isMan) "男" else "女";
        val emp = Employee(i, title, "$title@qq.com", gender, dep);
        empDao.addEmp(emp)

        session.commit()

        return 2
    }

    /**
     * 更新
     *   1000次
     */
    public fun update(i: Int): Int {
        val emp: Employee = empDao.getEmpById(i);
        val isMan = emp.gender == "男"
        emp.title = (if (isMan) "Mr " else "Miss ") + randomString(5);
        empDao.updateEmp(emp)
        session.commit()

        return 2
    }

    /**
     * 删除
     *    1000次
     */
    public fun delete(i: Int): Int {
        // 先查后删
        val emp = empDao.getEmpById(i)
        if (emp != null)
            empDao.delEmpById(i);
        session.commit()

        return 2
    }

    /**
     * 部门联查员工
     */
    public fun getDepWithEmps(i: Int): Int {
        depDao.getDepByIdWithEmps2sql(i)
        return 2
    }

    /**
     * 条件查询
     */
    public fun getEmpsByConditionIf(i: Int): Int {
        val emp = Employee()
        if (i % 2 == 1)
            emp.id = randomInt(1000)
        else
            emp.gender = if (randomBoolean()) "男" else "女"
        val emps = empDao.getEmpsByConditionIf(emp)

        return 1
    }

    /**
     * 更新动态字段
     */
    public fun updateEmpOnDynFields(i: Int): Int {
        // 先查后改
        val emp0 = empDao.getEmpById(i)
        if (emp0 != null) {
            val emp = Employee()
            emp.id = i

            if (i % 2 == 1) {
                val isMan = emp.gender == "男"
                emp.title = (if (isMan) "Mr " else "Miss ") + randomString(5);
            } else {
                emp.email = randomString(5) + "@qq.com"
            }

            empDao.updateEmpOnDynFields(emp)
            session.commit()
        }

        return 2
    }

    /**
     * 多id查询(循环多id拼where in)
     */
    public fun getEmpsByIds(i: Int): Int {
        val ids = listOf(randomInt(1000), randomInt(1000), randomInt(1000))
        val emps = empDao.getEmpsByConditionForeach(ids)

        return 1
    }

}