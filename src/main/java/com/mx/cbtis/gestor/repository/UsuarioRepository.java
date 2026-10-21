package com.mx.cbtis.gestor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.mx.cbtis.gestor.modelo.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario,Integer> {
	
	@Query(value = "select id_usuario, nombre, correo_electronico, contrasena, rol, imagen_usuario " 
			+ "from usuario u where correo_electronico = :correo_electronico" 
			+ " and contrasena = :contrasena", nativeQuery = true)
	Optional <Usuario> findByCorreoPass(@Param("correo_electronico") String correo_electronico,
			@Param("contrasena") String contrasena);
                
}
