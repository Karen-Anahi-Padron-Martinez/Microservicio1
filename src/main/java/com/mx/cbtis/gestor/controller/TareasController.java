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

import com.mx.cbtis.gestor.modelo.Tareas;
import com.mx.cbtis.gestor.repository.TareasRepository;

@RestController
@CrossOrigin(origins = "*")
public class TareasController {

	@Autowired
	TareasRepository tareastable;

	@RequestMapping(value = "/getTarea", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Tareas>> getTarea(
			@RequestBody(required = false) Tareas body,
			@RequestParam(name = "id_proyecto", required = false) Integer paramIdProyecto,
			@RequestParam(name = "id", required = false) Integer paramId) {

		Integer targetId = null;
		if (body != null && body.getId_proyecto() != null) {
			targetId = body.getId_proyecto();
		} else if (paramIdProyecto != null) {
			targetId = paramIdProyecto;
		} else if (paramId != null) {
			targetId = paramId;
		}

		if (targetId == null) {
			return ResponseEntity.ok(new ArrayList<>());
		}

		List<Tareas> listaTareas = tareastable.findByIdProyecto(targetId);
		return ResponseEntity.ok(listaTareas);
	}

	@GetMapping(value = "/buscarTarea", produces = "application/json;charset=UTF-8")
	public ResponseEntity<?> getTarea(
			@RequestParam(name = "id_tarea", required = false) Integer id_tarea,
			@RequestParam(name = "id", required = false) Integer id) {

		Integer targetId = (id_tarea != null) ? id_tarea : id;
		if (targetId == null) {
			Map<String, Object> error = new LinkedHashMap<>();
			error.put("status", HttpStatus.BAD_REQUEST.value());
			error.put("error", "Bad Request");
			error.put("mensaje", "El parámetro 'id_tarea' o 'id' es requerido");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}

		Optional<Tareas> tarea = tareastable.findById(targetId);
		if (tarea.isPresent()) {
			return ResponseEntity.ok(tarea.get());
		} else {
			Map<String, Object> error = new LinkedHashMap<>();
			error.put("status", HttpStatus.NOT_FOUND.value());
			error.put("error", "Not Found");
			error.put("mensaje", "La tarea con ID " + targetId + " no existe");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
	}

	@PostMapping(value = "/addTareas", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Integer> addTareas(@RequestBody(required = false) Tareas tareas) {
		if (tareas == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
		}
		try {
			if (tareas.getId_tarea() != null && tareas.getId_tarea() == 0) {
				tareas.setId_tarea(null);
			}
			tareastable.save(tareas);
			return ResponseEntity.status(HttpStatus.CREATED).body(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
		}
	}

	@PostMapping(value = "/updateTareas", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Integer> updateTareas(@RequestBody(required = false) Tareas tareas) {
		if (tareas == null || tareas.getId_tarea() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
		}
		if (!tareastable.existsById(tareas.getId_tarea())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
		try {
			tareastable.save(tareas);
			return ResponseEntity.ok(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
		}
	}

	@DeleteMapping(value = "/deleteTareas", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Integer> deleteTareas(
			@RequestBody(required = false) Tareas tareas,
			@RequestParam(name = "id_tarea", required = false) Integer id_tarea,
			@RequestParam(name = "id", required = false) Integer id) {

		Integer targetId = null;
		if (tareas != null && tareas.getId_tarea() != null) {
			targetId = tareas.getId_tarea();
		} else if (id_tarea != null) {
			targetId = id_tarea;
		} else if (id != null) {
			targetId = id;
		}

		if (targetId == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
		}
		if (!tareastable.existsById(targetId)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
		try {
			tareastable.deleteById(targetId);
			return ResponseEntity.ok(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(0);
		}
	}

	@RequestMapping(value = "/buscarTareas", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Tareas>> getTareas() {
		List<Tareas> listaTareas = new ArrayList<>();
		tareastable.findAll().forEach(listaTareas::add);
		return ResponseEntity.ok(listaTareas);
	}
}
