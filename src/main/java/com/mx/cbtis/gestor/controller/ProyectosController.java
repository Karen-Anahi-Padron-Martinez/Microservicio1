package com.mx.cbtis.gestor.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

@RestController
@CrossOrigin(origins = "*")
public class ProyectosController {

	@Autowired
	ProtectosRepository proyectostable;

	@GetMapping(value = "/buscarProyecto", produces = "application/json;charset=UTF-8")
	public ResponseEntity<?> getProyecto(
			@RequestParam(name = "id_proyecto", required = false) Integer id_proyecto,
			@RequestParam(name = "id", required = false) Integer id) {

		Integer targetId = (id_proyecto != null) ? id_proyecto : id;
		if (targetId == null) {
			Map<String, Object> error = new LinkedHashMap<>();
			error.put("status", HttpStatus.BAD_REQUEST.value());
			error.put("error", "Bad Request");
			error.put("mensaje", "El parámetro 'id_proyecto' o 'id' es requerido");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}

		Optional<Proyectos> proyecto = proyectostable.findById(targetId);
		if (proyecto.isPresent()) {
			return ResponseEntity.ok(proyecto.get());
		} else {
			Map<String, Object> error = new LinkedHashMap<>();
			error.put("status", HttpStatus.NOT_FOUND.value());
			error.put("error", "Not Found");
			error.put("mensaje", "El proyecto con ID " + targetId + " no existe");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
	}

	@PostMapping(value = "/addProyecto", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Integer> addProyecto(@RequestBody(required = false) Proyectos proyecto) {
		if (proyecto == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
		}
		try {
			if (proyecto.getId_proyecto() != null && proyecto.getId_proyecto() == 0) {
				proyecto.setId_proyecto(null);
			}
			proyectostable.save(proyecto);
			return ResponseEntity.status(HttpStatus.CREATED).body(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
		}
	}

	@PostMapping(value = "/updateProyecto", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Integer> updateProyecto(@RequestBody(required = false) Proyectos proyecto) {
		if (proyecto == null || proyecto.getId_proyecto() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
		}
		if (!proyectostable.existsById(proyecto.getId_proyecto())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
		try {
			proyectostable.save(proyecto);
			return ResponseEntity.ok(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
		}
	}

	@DeleteMapping(value = "/deleteProyecto", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Integer> deleteProyecto(
			@RequestBody(required = false) Proyectos proyecto,
			@RequestParam(name = "id_proyecto", required = false) Integer id_proyecto,
			@RequestParam(name = "id", required = false) Integer id) {

		Integer targetId = null;
		if (proyecto != null && proyecto.getId_proyecto() != null) {
			targetId = proyecto.getId_proyecto();
		} else if (id_proyecto != null) {
			targetId = id_proyecto;
		} else if (id != null) {
			targetId = id;
		}

		if (targetId == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
		}
		if (!proyectostable.existsById(targetId)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
		try {
			proyectostable.deleteById(targetId);
			return ResponseEntity.ok(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(0);
		}
	}

	@RequestMapping(value = "/buscarProyectos", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Proyectos>> getProyectos() {
		List<Proyectos> listaProyectos = new ArrayList<>();
		proyectostable.findAll().forEach(listaProyectos::add);
		return ResponseEntity.ok(listaProyectos);
	}
}
