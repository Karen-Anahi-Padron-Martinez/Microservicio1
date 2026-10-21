package com.mx.cbtis.gestor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.mx.cbtis.gestor.modelo.Tareas;

public interface TareasRepository extends CrudRepository<Tareas,Integer>{
	
	@Query(value = "select id_tarea, nombre, descripcion, fecha_vencimiento, lista, estado, id_proyecto " 
			+ "from tareas t where id_proyecto = :id_proyecto", nativeQuery = true) 
	List<Tareas> findByIdProyecto(@Param("id_proyecto") Integer id_proyecto);

}
