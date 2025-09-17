package com.unluki.models;

import jakarta.persistence.*;

@Entity
@Table(
        name = "sucursal",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "Id")
        }
)
public class Sucursal {

    private static final int stringLength = 40;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sucursal")
    private int id_sucursal;

    @Column(name = "descripcion", length = stringLength)
    private String descripcion;

    @Column(name = "direccion", length = stringLength)
    private String direccion;

    public Sucursal() {}

    public Sucursal(int id_sucursal, String descripcion, String direccion) {
        this.id_sucursal = id_sucursal;
        this.descripcion = descripcion;
        this.direccion = direccion;
    }

    public int getId_sucursal() {
        return this.id_sucursal;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public String getDireccion() {
        return this.direccion;
    }

    public void setId_sucursal(int id_sucursal) {
        this.id_sucursal = id_sucursal;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return "id_sucursal=" + id_sucursal + " descripcion=" + descripcion + " direccion=" + direccion;
    }
}
