package net.jkcode.jkbenchmark.orm.mybatis.dao;


import net.jkcode.jkbenchmark.orm.mybatis.model.Department;
import net.jkcode.jkbenchmark.orm.mybatis.model.Employee;

public interface DepartmentDao {

    /**
     * 添加部门
     * @param dep
     * @return
     */
    Long addDep(Department dep);

    /**
     * 根据id查部门
     * @param id
     * @return
     */
    Department getDepById(Integer id);

    /**
     * 部门联查员工, 用1条sql
     * @param id
     * @return
     */
    Department getDepByIdWithEmps1sql(Integer id);

    /**
     * 部门联查员工, 用2条sql
     * @param id
     * @return
     */
    Department getDepByIdWithEmps2sql(Integer id);
}
