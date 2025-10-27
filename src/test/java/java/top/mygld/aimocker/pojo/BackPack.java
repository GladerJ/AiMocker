package top.mygld.aimocker.pojo;

/**
 * Test Pojo
 */
public class BackPack {
    private int weight;
    private String brand;

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    @Override
    public String toString() {
        return "BackPack{" +
                "weight=" + weight +
                ", brand='" + brand + '\'' +
                '}';
    }
}
