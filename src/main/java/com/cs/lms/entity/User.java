package com.cs.lms.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;

@Entity
@Table(name = "lms_users", uniqueConstraints = @UniqueConstraint(columnNames = {"phone_number", "role"}))
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "user_lms_id", nullable = false, unique = true)
    @NotEmpty(message = "user lms id cannot be empty")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userLmsId;

    @Column(name = "dob")
    private Date dateOfBirth;

    /*@NotEmpty(message = "Password cannot be empty")
    @Size(min = 6, max = 15, message = "Password must between 6 and 15 characters")
    @Column(name = "password", nullable = false)
    private String password;*/

    @Column(name = "role")
    private String role;

    @Column(name = "active")
    private boolean active;

    public User() {
        this.active = false;
    }

    public User(String name, String phoneNumber, String userLmsId, /*String password,*/ String role, boolean active) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.userLmsId = userLmsId;
        /*this.password = password;*/
        this.role = role;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserLmsId() {
        return userLmsId;
    }

    public void setUserLmsId(String userLmsId) {
        this.userLmsId = userLmsId;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return phoneNumber.equals(user.phoneNumber) && userLmsId.equals(user.userLmsId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumber, userLmsId);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("phoneNumber='" + phoneNumber + "'")
                .add("userLmsId='" + userLmsId + "'")
                .add("dateOfBirth=" + dateOfBirth)
                .add("role='" + role + "'")
                .add("active=" + active)
                .toString();
    }
}
