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

import com.mx.cbtis.gestor.modelo.Comentarios;
import com.mx.cbtis.gestor.repository.ComentariosRepository;

@RestController
@CrossOrigin(origins = "*")
public class ComentariosController {

	@Autowired
	ComentariosRepository comentariostable;

	@RequestMapping(value = "/getComentario", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Comentarios>> getComentario(
			@RequestBody(required = false) Comentarios body,
			@RequestParam(name = "id_tarea", required = false) Integer paramIdTarea,
			@RequestParam(name = "id", required = false) Integer paramId) {

		Integer targetId = null;
		if (body != null && body.getId_tarea() != null) {
			targetId = body.getId_tarea();
		} else if (paramIdTarea != null) {
			targetId = paramIdTarea;
		} else if (paramId != null) {
			targetId = paramId;
		}

		if (targetId == null) {
			return ResponseEntity.ok(new ArrayList<>());
		}

		List<Comentarios> listaComentarios = comentariostable.findByTarea(targetId);
		return ResponseEntity.ok(listaComentarios);
	}

	@GetMapping(value = "/buscarComentario", produces = "application/json;charset=UTF-8")
	public ResponseEntity<?> getComentario(
			@RequestParam(name = "id", required = false) Integer id,
			@RequestParam(name = "id_comentario", required = false) Integer id_comentario) {

		Integer targetId = (id != null) ? id : id_comentario;
		if (targetId == null) {
			Map<String, Object> error = new LinkedHashMap<>();
			error.put("status", HttpStatus.BAD_REQUEST.value());
			error.put("error", "Bad Request");
			error.put("mensaje", "El parámetro 'id' o 'id_comentario' es requerido");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}

		Optional<Comentarios> comentario = comentariostable.findById(targetId);
		if (comentario.isPresent()) {
			return ResponseEntity.ok(comentario.get());
		} else {
			Map<String, Object> error = new LinkedHashMap<>();
			error.put("status", HttpStatus.NOT_FOUND.value());
			error.put("error", "Not Found");
			error.put("mensaje", "El comentario con ID " + targetId + " no existe");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
	}

	@PostMapping(value = "/addComentario", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Integer> addComentario(@RequestBody(required = false) Comentarios comentario) {
		if (comentario == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
		}
		try {
			if (comentario.getId_comentario() != null && comentario.getId_comentario() == 0) {
				comentario.setId_comentario(null);
			}
			comentariostable.save(comentario);
			return ResponseEntity.status(HttpStatus.CREATED).body(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
		}
	}

	@PostMapping(value = "/updateComentario", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Integer> updateComentario(@RequestBody(required = false) Comentarios comentario) {
		if (comentario == null || comentario.getId_comentario() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
		}
		if (!comentariostable.existsById(comentario.getId_comentario())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
		try {
			comentariostable.save(comentario);
			return ResponseEntity.ok(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
		}
	}

	@DeleteMapping(value = "/deleteComentario", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Integer> deleteComentario(
			@RequestBody(required = false) Comentarios comentario,
			@RequestParam(name = "id_comentario", required = false) Integer id_comentario,
			@RequestParam(name = "id", required = false) Integer id) {

		Integer targetId = null;
		if (comentario != null && comentario.getId_comentario() != null) {
			targetId = comentario.getId_comentario();
		} else if (id_comentario != null) {
			targetId = id_comentario;
		} else if (id != null) {
			targetId = id;
		}

		if (targetId == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
		}
		if (!comentariostable.existsById(targetId)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
		try {
			comentariostable.deleteById(targetId);
			return ResponseEntity.ok(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(0);
		}
	}

	@RequestMapping(value = "/buscarComentarios", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Comentarios>> getComentarios() {
		List<Comentarios> listaComentarios = new ArrayList<>();
		comentariostable.findAll().forEach(listaComentarios::add);
		return ResponseEntity.ok(listaComentarios);
	}
}
