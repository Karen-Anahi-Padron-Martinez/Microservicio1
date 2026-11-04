package com.mx.cbtis.gestor.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Comentarios {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id_comentario;
	private String id_usuario;
	private Integer id_tarea;
	private String texto_comentario;
	private String fecha_hora;
	
	public Comentarios() {
		super();
	}

	public Comentarios(Integer idComentario, String idUsuario, Integer idTarea, String textoComentario,
			String fechaHora) {
		super();
		this.id_comentario = idComentario;
		this.id_usuario = idUsuario;
		this.id_tarea = idTarea;
		this.texto_comentario = textoComentario;
		this.fecha_hora = fechaHora;
	}

	public Integer getId_comentario() {
		return id_comentario;
	}

	public void setId_comentario(Integer idComentario) {
		this.id_comentario = idComentario;
	}

	public String getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(String idUsuario) {
		this.id_usuario = idUsuario;
	}

	public Integer getId_tarea() {
		return id_tarea;
	}

	public void setId_tarea(Integer idTarea) {
		this.id_tarea = idTarea;
	}

	public String getTexto_comentario() {
		return texto_comentario;
	}

	public void setTexto_comentario(String textoComentario) {
		this.texto_comentario = textoComentario;
	}

	public String getFecha_hora() {
		return fecha_hora;
	}

	public void setFecha_hora(String fechaHora) {
		this.fecha_hora = fechaHora;
	}
}
