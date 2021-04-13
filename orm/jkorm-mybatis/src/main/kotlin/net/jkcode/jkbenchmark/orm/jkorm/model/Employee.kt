package net.jkcode.jkbenchmark.orm.jkorm.model

import net.jkcode.jkmvc.orm.* 
import java.util.*

/**
 * 员工
 *
 * @author shijianhang<772910474@qq.com>
 * @date 2021-04-13 14:18:34
 */
class Employee(vararg pks: Any): Orm(*pks) {

	public constructor() : this(*arrayOf())

	// 伴随对象就是元数据
 	companion object m: OrmMeta(Employee::class, "员工", "employee", DbKeyNames("id")){
		init {
			belongsTo("dep", Department::class, "dep_id")
		}
	}

	// 代理属性读写
	public var id:Integer by property() //

	public var title:String by property() //  

	public var email:String by property() //  

	public var gender:String by property() //  

	public var depId:Integer by property() //

	public var dep:Department by property() // 从属于一个部门

}