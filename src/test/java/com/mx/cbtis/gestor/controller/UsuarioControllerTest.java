package com.mx.cbtis.gestor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioController usuarioController;

    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp() {
        usuarioPrueba = new Usuario(1, "Carlos López", "carlos@ejemplo.com", "pass123", "ADMIN", "avatar.jpg");
    }

    @Test
    void testBuscarUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(Arrays.asList(usuarioPrueba));

        ResponseEntity<List<Usuario>> resultado = usuarioController.getUsuarios();

        assertNotNull(resultado);
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(1, resultado.getBody().size());
        assertEquals("Carlos López", resultado.getBody().get(0).getNombre());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    void testBuscarUsuarioPorIdExitoso() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioPrueba));

        ResponseEntity<?> resultado = usuarioController.getUsuario(1, null);

        assertNotNull(resultado);
        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        Usuario body = (Usuario) resultado.getBody();
        assertEquals("Carlos López", body.getNombre());
        verify(usuarioRepository, times(1)).findById(1);
    }

    @Test
    void testBuscarUsuarioPorIdNoEncontrado() {
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<?> resultado = usuarioController.getUsuario(999, null);

        assertNotNull(resultado);
        assertEquals(HttpStatus.NOT_FOUND, resultado.getStatusCode());
        verify(usuarioRepository, times(1)).findById(999);
    }

    @Test
    void testAddUsuario() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);

        ResponseEntity<Integer> status = usuarioController.addUsuario(usuarioPrueba);

        assertEquals(HttpStatus.CREATED, status.getStatusCode());
        assertEquals(1, status.getBody());
        verify(usuarioRepository, times(1)).save(usuarioPrueba);
    }

    @Test
    void testDeleteUsuario() {
        when(usuarioRepository.existsById(1)).thenReturn(true);

        ResponseEntity<Integer> status = usuarioController.deleteUsuario(usuarioPrueba, null, null);

        assertEquals(HttpStatus.OK, status.getStatusCode());
        assertEquals(1, status.getBody());
        verify(usuarioRepository, times(1)).deleteById(1);
    }
}
