package net.jkcode.jkbenchmark.orm

interface MyTests<D, E> {

    /**
     * 添加部门+员工
     */
    fun addDepAndEmps(dep: D, emp: E)

    /**
     * 修改员工
     */
    fun updateEmp(emp: E)

    /**
     * 删除员工
     */
    fun delEmp(empId: Int)

    /**
     * 部门联查员工
     */
    fun getDepWithEmps(depId: Int)



}