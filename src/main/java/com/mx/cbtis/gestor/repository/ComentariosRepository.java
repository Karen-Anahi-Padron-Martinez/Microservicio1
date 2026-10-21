package com.mx.cbtis.gestor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.mx.cbtis.gestor.modelo.Comentarios;

public interface ComentariosRepository extends CrudRepository<Comentarios,Integer>{
	
	@Query(value = "select id_comentario, id_tarea, texto_comentario, fecha_hora, id_usuario " 
			+ "from comentarios c where id_tarea = :id_tarea", nativeQuery = true)
	List<Comentarios> findByTarea(@Param("id_tarea") Integer id_tarea);

}
