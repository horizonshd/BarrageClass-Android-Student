/**
 * Course类
 */
package com.horizonshd.www.barrageclassstudent;

public class Course {
    private String courseid;
    private String coursename;
    private Boolean isactive;

    public Course(String courseid,String coursename,Boolean isactive){
        this.courseid = courseid;
        this.coursename = coursename;
        this.isactive = isactive;
    }

    public String getCourseid() {
        return courseid;
    }

    public String getCoursename() {
        return coursename;
    }

    public Boolean getIsactive() {
        return isactive;
    }
}
