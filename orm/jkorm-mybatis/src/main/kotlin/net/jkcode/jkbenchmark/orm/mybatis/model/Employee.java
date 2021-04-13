package net.jkcode.jkbenchmark.orm.mybatis.model;

public class Employee {
    private Integer id;
    private String title;
    private String email;
    private String gender;
    private Department dep;

    public Employee(){

    }

    public Employee(Integer id, String title, String email, String gender, Department dep) {
        this.id = id;
        this.title = title;
        this.email = email;
        this.gender = gender;
        this.dep = dep;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Department getDep() {
        return dep;
    }

    public void setDep(Department dep) {
        this.dep = dep;
    }
}
