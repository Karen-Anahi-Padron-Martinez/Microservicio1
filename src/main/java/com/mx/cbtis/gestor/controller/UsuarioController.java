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

import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

@RestController
@CrossOrigin(origins = "*")
public class UsuarioController {

	@Autowired
	UsuarioRepository usuariotable;

	@PostMapping(value = "/getUsuarioLogin", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Usuario> getUsuarioLogin(@RequestBody(required = false) Usuario userParam) {
		Usuario user = new Usuario();

		if (userParam == null || userParam.getCorreo_electronico() == null || userParam.getContrasena() == null) {
			user.setNombre("000");
			return ResponseEntity.ok(user);
		}

		Optional<Usuario> optionaluser = usuariotable.findByCorreoPass(
				userParam.getCorreo_electronico().trim(),
				userParam.getContrasena().trim());

		if (optionaluser.isPresent()) {
			user = optionaluser.get();
		} else {
			user.setNombre("000");
		}

		return ResponseEntity.ok(user);
	}

	@GetMapping(value = "/buscarUsuario", produces = "application/json;charset=UTF-8")
	public ResponseEntity<?> getUsuario(
			@RequestParam(name = "id", required = false) Integer id,
			@RequestParam(name = "id_usuario", required = false) Integer id_usuario) {

		Integer targetId = (id != null) ? id : id_usuario;
		if (targetId == null) {
			Map<String, Object> error = new LinkedHashMap<>();
			error.put("status", HttpStatus.BAD_REQUEST.value());
			error.put("error", "Bad Request");
			error.put("mensaje", "El parámetro 'id' o 'id_usuario' es requerido");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
		}

		Optional<Usuario> usuario = usuariotable.findById(targetId);
		if (usuario.isPresent()) {
			return ResponseEntity.ok(usuario.get());
		} else {
			Map<String, Object> error = new LinkedHashMap<>();
			error.put("status", HttpStatus.NOT_FOUND.value());
			error.put("error", "Not Found");
			error.put("mensaje", "El usuario con ID " + targetId + " no existe");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
		}
	}

	@PostMapping(value = "/addUsuario", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Integer> addUsuario(@RequestBody(required = false) Usuario usuario) {
		if (usuario == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
		}
		try {
			if (usuario.getId_usuario() != null && usuario.getId_usuario() == 0) {
				usuario.setId_usuario(null);
			}
			usuariotable.save(usuario);
			return ResponseEntity.status(HttpStatus.CREATED).body(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
		}
	}

	@PostMapping(value = "/updateUsuario", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Integer> updateUsuario(@RequestBody(required = false) Usuario usuario) {
		if (usuario == null || usuario.getId_usuario() == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
		}
		if (!usuariotable.existsById(usuario.getId_usuario())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
		try {
			usuariotable.save(usuario);
			return ResponseEntity.ok(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(0);
		}
	}

	@DeleteMapping(value = "/deleteUsuario", produces = "application/json;charset=UTF-8")
	public ResponseEntity<Integer> deleteUsuario(
			@RequestBody(required = false) Usuario usuario,
			@RequestParam(name = "id_usuario", required = false) Integer id_usuario,
			@RequestParam(name = "id", required = false) Integer id) {

		Integer targetId = null;
		if (usuario != null && usuario.getId_usuario() != null) {
			targetId = usuario.getId_usuario();
		} else if (id_usuario != null) {
			targetId = id_usuario;
		} else if (id != null) {
			targetId = id;
		}

		if (targetId == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(0);
		}
		if (!usuariotable.existsById(targetId)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(0);
		}
		try {
			usuariotable.deleteById(targetId);
			return ResponseEntity.ok(1);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(0);
		}
	}

	@RequestMapping(value = "/buscarUsuarios", method = {RequestMethod.GET, RequestMethod.POST}, produces = "application/json;charset=UTF-8")
	public ResponseEntity<List<Usuario>> getUsuarios() {
		List<Usuario> listaUsuarios = new ArrayList<>();
		usuariotable.findAll().forEach(listaUsuarios::add);
		return ResponseEntity.ok(listaUsuarios);
	}
}
