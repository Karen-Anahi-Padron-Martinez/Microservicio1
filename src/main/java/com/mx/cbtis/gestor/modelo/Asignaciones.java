package com.mx.cbtis.gestor.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Asignaciones {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id_asignacion;
	private Integer id_usuario;
	private Integer id_tarea;
	private String fecha_asignacion;
	
	public Asignaciones() {
		super();
	}

	public Asignaciones(Integer idAsignacion, Integer idUsuario, Integer idTarea, String fechaAsignacion) {
		super();
		this.id_asignacion = idAsignacion;
		this.id_usuario = idUsuario;
		this.id_tarea = idTarea;
		this.fecha_asignacion = fechaAsignacion;
	}

	public Integer getId_asignacion() {
		return id_asignacion;
	}

	public void setId_asignacion(Integer idAsignacion) {
		this.id_asignacion = idAsignacion;
	}

	public Integer getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(Integer idUsuario) {
		this.id_usuario = idUsuario;
	}

	public Integer getId_tarea() {
		return id_tarea;
	}

	public void setId_tarea(Integer idTarea) {
		this.id_tarea = idTarea;
	}

	public String getFecha_asignacion() {
		return fecha_asignacion;
	}

	public void setFecha_asignacion(String fechaAsignacion) {
		this.fecha_asignacion = fechaAsignacion;
	}
}
