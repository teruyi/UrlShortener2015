package urlshortener.bangladeshgreen.domain;

/**
 * Created by teruyi on 29/12/15.
 * This class represent a Map<(country|region|city),number> number its a adder of clicks by country, region or city
 */
public class ClickAdds {

    private String name;
    private int number;

    public ClickAdds(String name, int number) {
        this.name = name;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String nombre) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int numero) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "ClickAdds{" +
                "name='" + name + '\'' +
                ", number=" + number +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClickAdds)) return false;

        ClickAdds clickPais = (ClickAdds) o;

        if (number != clickPais.number) return false;
        return !(name != null ? !name.equals(clickPais.name) : clickPais.name != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + number;
        return result;
    }
}
