package com.mx.cbtis.gestor.modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Usuario {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id_usuario;
	private String nombre;
	private String correo_electronico;
	// Se acepta al recibir JSON pero nunca se devuelve en las respuestas (evita exponer el hash)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String contrasena;
	private String rol;
	private String imagen_usuario;
	
	public Usuario() {
		super();
	}

	public Usuario(Integer idUsuario, String nombre, String correoElectronico, String contrasena, String rol,
			String imagenUsuario) {
		super();
		this.id_usuario = idUsuario;
		this.nombre = nombre;
		this.correo_electronico = correoElectronico;
		this.contrasena = contrasena;
		this.rol = rol;
		this.imagen_usuario = imagenUsuario;
	}

	public Integer getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(Integer idUsuario) {
		this.id_usuario = idUsuario;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCorreo_electronico() {
		return correo_electronico;
	}

	public void setCorreo_electronico(String correoElectronico) {
		this.correo_electronico = correoElectronico;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public String getImagen_usuario() {
		return imagen_usuario;
	}

	public void setImagen_usuario(String imagenUsuario) {
		this.imagen_usuario = imagenUsuario;
	}
}
