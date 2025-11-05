package top.mygld.aimocker.pojo;

// public record Animal(String name, int age, double weight, double height, double length) {}

public class Animal {
    private String name;
    private int age;
    private double weight;
    private double height;
    private double length;

    public Animal(){}

    // 构造方法
    public Animal(String name, int age, double weight, double height, double length) {
        this.name = name;
        this.age = age;
        this.weight = weight;
        this.height = height;
        this.length = length;
    }

    // Getter 方法
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public double getWeight() {
        return weight;
    }

    public double getHeight() {
        return height;
    }

    public double getLength() {
        return length;
    }

    // Setter 方法（如果需要可写）
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setLength(double length) {
        this.length = length;
    }

    // toString 方法
    @Override
    public String toString() {
        return "Animal{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                ", height=" + height +
                ", length=" + length +
                '}';
    }

    // equals 和 hashCode 如果需要也可以加
}
