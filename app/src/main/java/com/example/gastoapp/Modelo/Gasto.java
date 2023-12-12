package com.example.gastoapp.Modelo;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Gasto {
    private Integer id;
    private String nombre;
    private Integer monto;
    private Calendar fecha;

    public Gasto() {
        this.fecha = Calendar.getInstance();
    }

    public Gasto(Integer id, String nombre, Integer monto, Calendar fecha) {
        this.id = id;
        this.nombre = nombre;
        this.monto = monto;
        this.fecha = fecha;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getMonto() {
        return monto;
    }

    public void setMonto(Integer monto) {
        this.monto = monto;
    }

    public Calendar getFecha() {
        return fecha;
    }

    public void setFecha(Calendar fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
        String fechaComoTexto = formatoFecha.format(fecha.getTime());

        return "Gasto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", monto='" + monto + '\'' +
                ", fecha=" + fechaComoTexto +
                '}';
    }
}