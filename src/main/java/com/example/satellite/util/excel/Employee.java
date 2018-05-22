package com.example.satellite.util.excel;

/**
 * Created by Gaoxinwen on 2016/6/14.
 */
public class Employee {
    @ExcelField(align = 2, value= "你好哈哈哈哈哈哈哈哈哈哈哈哈")
    private String name;
    @ExcelField(align = 2, value= "啊啊啊啊")
    private Integer age;
    @ExcelField(align = 2, value= "啊")
    private String school;

    public Employee() {

    }

    public Employee(String name, Integer age, String school) {
        this.name = name;
        this.age = age;
        this.school = school;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", school='" + school + '\'' +
                '}';
    }
}
