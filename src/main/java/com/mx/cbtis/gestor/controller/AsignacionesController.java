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

import com.mx.cbtis.gestor.modelo.Asignaciones;
import com.mx.cbtis.gestor.repository.AsignacionesRepository;

@RestController
@CrossOrigin(origins = "*")
public class AsignacionesController {

	@Autowired
	AsignacionesRepository asignacionestable;

	@GetMapping(value = "/buscarAsignacion", produces = "application/json;charset=UTF-8")
	public ResponseEntity<?> getAsignacion(
			@RequestParam(name = "id", required = false) Integer id,
			@RequestParam(name = "id_asignacion", required = false) Integer id_asignacion) {

		Integer targetId = (id != null) ? id : id_asignacion;
		if (targetId == null) {
			Map<String, Object> error = new LinkedHashMap<>();
			error.put("status", HttpStatus.BAD_REQUEST.value());
			error.put("error", "Bad Request");
			error.put("mensaje", "El parámetro 'id' o 'id_asignacion' es requerido");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}

		Optional<Asignaciones> asignacion = asignacionestable.findById(targetId);
		if (asignacion.isPresent()) {
			return ResponseEntity.ok(asignacion.get());
		} else {
			Map<String, Object> error = new LinkedHashMap<>();
			error.put("status", HttpStatus.NOT_FOUND.value());
			error.put("error", "Not Found");
			error.put("mensaje", "La asignación con ID " + targetId + " no existe");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
	}

	@PostMapping(value = "/addAsignacion", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Integer> addAsignacion(@RequestBody(required = false) Asignaciones asignacion) {
		if (asignacion == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
		}
		try {
			if (asignacion.getId_asignacion() != null && asignacion.getId_asignacion() == 0) {
				asignacion.setId_asignacion(null);
			}
			asignacionestable.save(asignacion);
			return ResponseEntity.status(HttpStatus.CREATED).body(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
		}
	}

	@PostMapping(value = "/updateAsignacion", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Integer> updateAsignacion(@RequestBody(required = false) Asignaciones asignacion) {
		if (asignacion == null || asignacion.getId_asignacion() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
		}
		if (!asignacionestable.existsById(asignacion.getId_asignacion())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
		try {
			asignacionestable.save(asignacion);
			return ResponseEntity.ok(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
		}
	}

	@DeleteMapping(value = "/deleteAsignacion", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Integer> deleteAsignacion(
			@RequestBody(required = false) Asignaciones asignacion,
			@RequestParam(name = "id_asignacion", required = false) Integer id_asignacion,
			@RequestParam(name = "id", required = false) Integer id) {

		Integer targetId = null;
		if (asignacion != null && asignacion.getId_asignacion() != null) {
			targetId = asignacion.getId_asignacion();
		} else if (id_asignacion != null) {
			targetId = id_asignacion;
		} else if (id != null) {
			targetId = id;
		}

		if (targetId == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
		}
		if (!asignacionestable.existsById(targetId)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
		try {
			asignacionestable.deleteById(targetId);
			return ResponseEntity.ok(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(0);
		}
	}

	@RequestMapping(value = "/buscarAsignaciones", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Asignaciones>> getAsignaciones() {
		List<Asignaciones> listaAsignaciones = new ArrayList<>();
		asignacionestable.findAll().forEach(listaAsignaciones::add);
		return ResponseEntity.ok(listaAsignaciones);
	}
}
