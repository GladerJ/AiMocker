package top.mygld.aimocker.pojo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;

/**
 * Test Pojo
 */
public class User {

    private String name;
    private int age;
    private String email;
    private String city;
    private top.mygld.aimocker.pojo.BackPack backPack;
    private ArrayList<String> nickname;
    private Date birthday;
    private LocalDateTime localDateTime;
    private LocalTime localTime;

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                ", city='" + city + '\'' +
                ", backPack=" + backPack +
                ", nickname=" + nickname +
                ", birthday=" + birthday +
                ", localDateTime=" + localDateTime +
                ", localTime=" + localTime +
                ", localDate=" + localDate +
                '}';
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }

    private LocalDate localDate;


    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

    public ArrayList<String> getNickname() {
        return nickname;
    }

    public void setNickname(ArrayList<String> nickname) {
        this.nickname = nickname;
    }

    public top.mygld.aimocker.pojo.BackPack getBackPack() {
        return backPack;
    }

    public void setBackPack(top.mygld.aimocker.pojo.BackPack backPack) {
        this.backPack = backPack;
    }

    public User() {
    }

    // ----------- Getter & Setter -----------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    // ----------- toString -----------


    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
