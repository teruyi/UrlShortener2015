package urlshortener.bangladeshgreen.domain;

/**
 * Created by teruyi on 29/12/15.
 * This class represent a Map<(country|region|city),number> number its a adder of clicks by country, region or city
 */
public class ClickAdds {

    private String nombre;
    private int numero;

    public ClickAdds(String nombre, int numero) {
        this.nombre = nombre;
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        return "ClickAdds{" +
                "nombre='" + nombre + '\'' +
                ", numero=" + numero +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClickAdds)) return false;

        ClickAdds clickPais = (ClickAdds) o;

        if (numero != clickPais.numero) return false;
        return !(nombre != null ? !nombre.equals(clickPais.nombre) : clickPais.nombre != null);

    }

    @Override
    public int hashCode() {
        int result = nombre != null ? nombre.hashCode() : 0;
        result = 31 * result + numero;
        return result;
    }
}
