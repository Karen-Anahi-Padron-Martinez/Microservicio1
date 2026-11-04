package com.mx.cbtis.gestor.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.mx.cbtis.gestor.modelo.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario,Integer> {
	
	// La contraseña ya no se compara en SQL: se obtiene el hash BCrypt y se valida en el controlador
	@Query(value = "select id_usuario, nombre, correo_electronico, contrasena, rol, imagen_usuario " 
			+ "from usuario u where correo_electronico = :correo_electronico limit 1", nativeQuery = true)
	Optional <Usuario> findByCorreo(@Param("correo_electronico") String correo_electronico);
                
}
