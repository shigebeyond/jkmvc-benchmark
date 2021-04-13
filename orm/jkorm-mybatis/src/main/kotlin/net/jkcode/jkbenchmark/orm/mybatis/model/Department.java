package net.jkcode.jkbenchmark.orm.mybatis.model;

import java.util.List;

public class Department {
    private Integer id;
    private String title;
    private String intro;
    private List<Employee> emps;

    public Department(){

    }

    public Department(Integer id, String title, String intro) {
        this.id = id;
        this.title = title;
        this.intro = intro;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public List<Employee> getEmps() {
        return emps;
    }

    public void setEmps(List<Employee> emps) {
        this.emps = emps;
    }
}
