package com.ltst.schoolapp.parent.data.model;


import com.ltst.schoolapp.parent.data.response.schoolinfo.ChildForInfoResponse;
import com.ltst.schoolapp.parent.data.response.schoolinfo.SchoolForInfoResponse;
import com.ltst.schoolapp.parent.data.response.schoolinfo.SchoolInfoResponse;
import com.ltst.schoolapp.parent.data.response.schoolinfo.TeacherForInfoResponse;

import java.util.ArrayList;
import java.util.List;

public class SchoolInfo {
    private List<InfoSchool> schools;
    private List<Teacher> teachers;
    private List<Child> children;

    private SchoolInfo(List<InfoSchool> schools, List<Teacher> teachers, List<Child> children) {
        this.schools = schools;
        this.teachers = teachers;
        this.children = children;
    }

    public static SchoolInfo fromResponse(SchoolInfoResponse response) {
        List<InfoSchool> infoSchools = InfoSchool.fromResponse(response.schools);
        List<Teacher> teachers = Teacher.fromResponse(response.teachers);
        List<Child> children = Child.fromResponse(response.children);
        return new SchoolInfo(infoSchools, teachers, children);
    }

    public List<InfoSchool> getSchools() {
        return schools;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public List<Child> getChildren() {
        return children;
    }

    public static class InfoSchool {
        protected long id;
        protected String avatarUrl;
        protected String title;
        protected String address;
        protected String phoneNumber;
        protected String additionalPhoneNumber;
        protected String email;

        private static InfoSchool fromResponse(SchoolForInfoResponse response) {
            InfoSchool school = new InfoSchool();
            school.id = response.id;
            school.avatarUrl = response.avatarUrl;
            school.title = response.title;
            school.address = response.address;
            school.phoneNumber = response.phone;
            school.additionalPhoneNumber = response.additionalPhone;
            school.email = response.email;
            return school;
        }

        public long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public String getAddress() {
            return address;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getAdditionalPhoneNumber() {
            return additionalPhoneNumber;
        }

        public String getEmail() {
            return email;
        }

        private static List<InfoSchool> fromResponse(List<SchoolForInfoResponse> responses) {
            List<InfoSchool> schools = new ArrayList<>(responses.size());
            for (SchoolForInfoResponse response : responses) {
                schools.add(fromResponse(response));
            }
            return schools;
        }
    }

    public static class Teacher {
        private long id;
        private String avatarUrl;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String additionalPhoneNumber;
        private String email;
        private List<Long> groupIds;
        private long schoolId;


        public long getId() {
            return id;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getAdditionalPhoneNumber() {
            return additionalPhoneNumber;
        }

        public String getEmail() {
            return email;
        }

        public long getSchoolId() {
            return schoolId;
        }

        public List<Long> getGroupIds() {
            return groupIds;
        }

        private static Teacher fromResponse(TeacherForInfoResponse response) {
            Teacher teacher = new Teacher();
            teacher.id = response.id;
            teacher.avatarUrl = response.avatarUrl;
            teacher.firstName = response.firstName;
            teacher.lastName = response.lastName;
            teacher.phoneNumber = response.phone;
            teacher.additionalPhoneNumber = response.additionalPhone;
            teacher.email = response.email;
            teacher.schoolId = response.schoolId;
            teacher.groupIds = response.groupIds;
            return teacher;
        }

        private static List<Teacher> fromResponse(List<TeacherForInfoResponse> responses) {
            List<Teacher> result = new ArrayList<>(responses.size());
            for (TeacherForInfoResponse response : responses) {
                result.add(fromResponse(response));
            }
            return result;
        }


    }

    public static class Child {
        private long id;
        private String firstName;
        private String lastName;
        private List<Long> groupIds;
        private List<Long> schoolIds;

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public List<Long> getGroupIds() {
            return groupIds;
        }

        public List<Long> getSchoolIds() {
            return schoolIds;
        }

        private static Child fromResponse(ChildForInfoResponse response) {
            Child result = new Child();
            result.id = response.getServerId();
            result.firstName = response.getFirstName();
            result.lastName = response.getLastName();
            result.groupIds = response.getGroupIds();
            result.schoolIds = response.schoolIds;
            return result;
        }

        private static List<Child> fromResponse(List<ChildForInfoResponse> responses) {
            List<Child> result = new ArrayList<>(responses.size());
            for (ChildForInfoResponse response : responses) {
                result.add(fromResponse(response));
            }
            return result;
        }
    }

}
