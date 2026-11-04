package com.mx.cbtis.gestor.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Tareas {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id_tarea;
	private String nombre;
	private String descripcion;
	private String fecha_vencimiento;
	private String lista;
	private String estado;
	private Integer id_proyecto;

	public Tareas() {
		super();
	}

	public Tareas(Integer idTarea, String nombre, String descripcion, String fechaVencimiento, String lista,
			String estado, Integer idProyecto) {
		super();
		this.id_tarea = idTarea;
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.fecha_vencimiento = fechaVencimiento;
		this.lista = lista;
		this.estado = estado;
		this.id_proyecto = idProyecto;
	}

	public Integer getId_tarea() {
		return id_tarea;
	}

	public void setId_tarea(Integer idTarea) {
		this.id_tarea = idTarea;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getFecha_vencimiento() {
		return fecha_vencimiento;
	}

	public void setFecha_vencimiento(String fechaVencimiento) {
		this.fecha_vencimiento = fechaVencimiento;
	}

	public String getLista() {
		return lista;
	}

	public void setLista(String lista) {
		this.lista = lista;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Integer getId_proyecto() {
		return id_proyecto;
	}

	public void setId_proyecto(Integer idProyecto) {
		this.id_proyecto = idProyecto;
	}
}
