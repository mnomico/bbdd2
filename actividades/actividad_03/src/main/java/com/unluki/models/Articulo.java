package com.unluki.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;


@Entity
@Table(
        name = "articulo",
        uniqueConstraints = {
            @UniqueConstraint(columnNames = "Id")
        }
)
public class Articulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_articulo")
    private int id_articulo;

    @Column(name = "descripcion", length = 200)
    private String descripcion;

    @Min(0)
    @Column(name = "stock", nullable = false)
    private int stock;

    public Articulo() {}

    public Articulo(int id_articulo, String descripcion, int stock) {
        this.id_articulo = id_articulo;
        this.descripcion = descripcion;
        this.stock = stock;
    }

    public int getId_articulo() {
        return id_articulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getStock() {
        return stock;
    }

    public void setId_articulo(int id_articulo) {
        this.id_articulo = id_articulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "articulo [id_articulo=" + id_articulo +", descripcion=" + descripcion + ", stock=" + stock + "]";
    }
}
