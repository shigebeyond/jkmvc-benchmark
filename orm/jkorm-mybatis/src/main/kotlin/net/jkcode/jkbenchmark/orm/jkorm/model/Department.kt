package net.jkcode.jkbenchmark.orm.jkorm.model

import net.jkcode.jkmvc.orm.* 
import java.util.*

/**
 * 部门
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2021-04-13 14:18:07
 */
class Department(vararg pks: Any): Orm(*pks) {

	public constructor() : this(*arrayOf())

	// 伴随对象就是元数据
 	companion object m: OrmMeta(Department::class, "部门", "department", DbKeyNames("id")){
		init {
			hasMany("emps", Employee::class, "dep_id")
		}
	}

	// 代理属性读写
	public var id:Integer by property() //

	public var title:String by property() //  

	public var intro:String by property() //

	public var emps: List<Employee> by listProperty() // 有多个员工

}