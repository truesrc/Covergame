package csv;

import com.opencsv.bean.CsvBindByPosition;

import java.util.Objects;

public class Product implements Comparable<Product> {
    @CsvBindByPosition(position = 0)
    private int ID;
    @CsvBindByPosition(position = 1)
    private String name;
    @CsvBindByPosition(position = 2)
    private String condition;
    @CsvBindByPosition(position = 3)
    private String state;
    @CsvBindByPosition(position = 4)
    private float price;


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return ID + " " + name + " " + condition + " " + state + " " + price + "\n";
    }

/*
    @Override
    public String toString() {
        return "Product{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", condition='" + condition + '\'' +
                ", state='" + state + '\'' +
                ", price=" + price +
                '}';
    }
*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return ID == product.ID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }

    public void setState(String state) {
        this.state = state;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public int compareTo(Product o) {
        return Float.compare(price, o.getPrice());
    }
}

