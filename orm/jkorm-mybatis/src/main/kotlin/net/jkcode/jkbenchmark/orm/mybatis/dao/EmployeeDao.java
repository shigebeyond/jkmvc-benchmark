package net.jkcode.jkbenchmark.orm.mybatis.dao;

import net.jkcode.jkbenchmark.orm.mybatis.model.Employee;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

public interface EmployeeDao {
    /**
     * 增
     * @param emp
     * @return
     */
    Long addEmp(Employee emp);

    /**
     * 查多个
     * @return
     */
    List<Employee> getAllEmps();

    /**
     * 查单个
     * @param id
     * @return
     */
    Employee getEmpById(Integer id);

    /**
     * 改
     * @param emp
     * @return
     */
    Long updateEmp(Employee emp);

    /**
     * 删
     * @param id
     * @return
     */
    Long delEmpById(Integer id);

    /**
     * 员工联查部门
     * @param id
     * @return
     */
    Employee getEmpByIdWithDep(Integer id);

    /**
     * 根据部门id查员工
     * @param depId
     * @return
     */
    Employee getEmpByDepId(Integer depId);

    /**
     * 条件查询
     * @param emp
     * @param offSet
     * @param limit
     * @return
     */
    List<Employee> getEmpsByConditionIf(Employee emp, int offSet, int limit);

    /**
     * 更新动态字段
     * @param emp
     * @return
     */
    boolean updateEmpOnDynFields(Employee emp);

    /**
     * 单个动态条件查询
     * @param emp
     * @return
     */
    List<Employee> getEmpsByConditionChoose(Employee emp);

    /**
     * 多id查询(循环多id拼where in)
     * @param ids
     * @return
     */
    List<Employee> getEmpsByConditionForeach(@Param("ids") List<Integer> ids);

    /**
     * 批量插入
     * @param emps
     */
    void addEmps(@Param("emps") List<Employee> emps);

    /**
     * 通过定义变量, 来修正参数值
     * @param title
     * @return
     */
    List<Employee> getEmpsByInnerParameter(String title);
}
