package net.jkcode.jkbenchmark.orm.mybatis

import net.jkcode.jkbenchmark.IBenchmarkPlayer
import net.jkcode.jkbenchmark.orm.mybatis.dao.DepartmentDao
import net.jkcode.jkbenchmark.orm.mybatis.dao.EmployeeDao
import net.jkcode.jkbenchmark.orm.mybatis.model.Department
import net.jkcode.jkbenchmark.orm.mybatis.model.Employee
import net.jkcode.jkmvc.db.Db
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
class MybatisBenchmarkPlayer: IBenchmarkPlayer{

    /**
     * 玩家名
     */
    override val name: String = "mybatis"

    /**
     * mybatis dao
     */
    private var reader: Reader = Resources.getResourceAsReader("Configuration.xml")
    private var sqlSessionFactory: SqlSessionFactory= SqlSessionFactoryBuilder().build(reader)
    private var session: SqlSession = sqlSessionFactory.openSession()
    val depDao: DepartmentDao = session.getMapper(DepartmentDao::class.java)
    val empDao: EmployeeDao = session.getMapper(EmployeeDao::class.java)

    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            val player = MybatisBenchmarkPlayer()
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

        for(i in 1..100) {
            // 新增部门
            val dep = Department(i, "部" + i, "")
            depDao.addDep(dep)

            // 新增员工
            for(j in 1..10) {
                val isMan = randomBoolean()
                val title = (if(isMan) "Mr " else "Miss ") + randomString(5);
                val gender = if(isMan) "男" else "女";
                val emp = Employee((i-1)*100+j, title, "$title@qq.com", gender, dep);
                empDao.addEmp(emp)
            }
        }

        session.commit()
    }

    /**
     * 更新
     *   1000次
     */
    public fun update(i: Int){
        for(i in 1..1000) {
            val emp: Employee = empDao.getEmpById(i);
            val isMan = emp.gender == "男"
            emp.title = (if (isMan) "Mr " else "Miss ") + randomString(5);
            empDao.updateEmp(emp)
        }
        session.commit()
    }

    /**
     * 删除
     *    1000次
     */
    public fun delete(i: Int){
        for(i in 1..1000) {
            // 先查后删
            val emp = empDao.getEmpById(i)
            if(emp != null)
                empDao.delEmpById(i);
        }
        session.commit()
    }

    /**
     * 部门联查员工
     */
    public fun getDepWithEmp(i: Int){
        for(i in 1..100) {
            depDao.getDepByIdWithEmps2sql(i)
        }
    }

    /**
     * 条件查询
     */
    public fun getEmpsByConditionIf(i: Int){
        for(i in 1..100) {
            val emp = Employee()
            if (i % 2 == 1)
                emp.id = randomInt(1000)
            else
                emp.gender = if (randomBoolean()) "男" else "女"
            val emps = empDao.getEmpsByConditionIf(emp, 0, 10)
        }
    }

    /**
     * 更新动态字段
     */
    public fun updateEmpOnDynFields(i: Int){
        for(i in 1..100) {
            // 先查后改
            val emp0 = empDao.getEmpById(i)
            if(emp0 == null){
                continue
            }

            val emp = Employee()
            emp.id = i

            if (i % 2 == 1) {
                val isMan = emp.gender == "男"
                emp.title = (if (isMan) "Mr " else "Miss ") + randomString(5);
            } else {
                emp.email = randomString(5) + "@qq.com"
            }

            empDao.updateEmpOnDynFields(emp)
        }
        session.commit()
    }

    /**
     * 多id查询(循环多id拼where in)
     */
    public fun getEmpsByIds(i: Int){
        for(i in 1..100) {
            val ids = listOf(randomInt(1000), randomInt(1000), randomInt(1000))
            val emps = empDao.getEmpsByConditionForeach(ids)
        }
    }

}