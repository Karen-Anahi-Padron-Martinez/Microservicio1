package com.mx.cbtis.gestor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    // Encoder real con costo bajo para que las pruebas sean rápidas
    @Spy
    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(4);

    @InjectMocks
    private UsuarioController usuarioController;

    private Usuario usuarioPrueba;
    private String hashPass123;

    @BeforeEach
    void setUp() {
        hashPass123 = new BCryptPasswordEncoder(4).encode("pass123");
        usuarioPrueba = new Usuario(1, "Carlos López", "carlos@example.com", hashPass123, "ADMIN", "#FFFFFF");
    }

    // ---------- getUsuarioLogin ----------

    @Test
    void loginConCredencialesCorrectasDevuelveUsuario() {
        when(usuarioRepository.findByCorreo("carlos@example.com")).thenReturn(Optional.of(usuarioPrueba));

        ResponseEntity<Usuario> r = usuarioController.getUsuarioLogin(
                new Usuario(null, null, "  carlos@example.com ", " pass123 ", null, null));

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals("Carlos López", r.getBody().getNombre());
        verify(usuarioRepository).findByCorreo("carlos@example.com");
    }

    @Test
    void loginConContrasenaIncorrectaDevuelveCodigo000() {
        when(usuarioRepository.findByCorreo("carlos@example.com")).thenReturn(Optional.of(usuarioPrueba));

        ResponseEntity<Usuario> r = usuarioController.getUsuarioLogin(
                new Usuario(null, null, "carlos@example.com", "otra", null, null));

        assertEquals("000", r.getBody().getNombre());
        assertNull(r.getBody().getCorreo_electronico());
    }

    @Test
    void loginConCorreoInexistenteDevuelveCodigo000() {
        when(usuarioRepository.findByCorreo(anyString())).thenReturn(Optional.empty());

        ResponseEntity<Usuario> r = usuarioController.getUsuarioLogin(
                new Usuario(null, null, "nadie@example.com", "x", null, null));

        assertEquals("000", r.getBody().getNombre());
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void loginRechazaContrasenaGuardadaEnTextoPlano() {
        // Un registro heredado sin cifrar no debe permitir el acceso aunque coincida el texto
        Usuario legado = new Usuario(2, "Legado", "legado@example.com", "pass123", "Estudiante", "#000000");
        when(usuarioRepository.findByCorreo("legado@example.com")).thenReturn(Optional.of(legado));

        ResponseEntity<Usuario> r = usuarioController.getUsuarioLogin(
                new Usuario(null, null, "legado@example.com", "pass123", null, null));

        assertEquals("000", r.getBody().getNombre());
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void loginConCuerpoNuloOIncompletoNoConsultaRepositorio() {
        assertEquals("000", usuarioController.getUsuarioLogin(null).getBody().getNombre());
        assertEquals("000", usuarioController.getUsuarioLogin(
                new Usuario(null, null, null, "x", null, null)).getBody().getNombre());
        assertEquals("000", usuarioController.getUsuarioLogin(
                new Usuario(null, null, "a@example.com", null, null, null)).getBody().getNombre());
        verify(usuarioRepository, never()).findByCorreo(anyString());
    }

    // ---------- cifrado ----------

    @Test
    void esHashBcryptDistingueHashesDeTextoPlano() {
        assertTrue(UsuarioController.esHashBcrypt(hashPass123));
        assertFalse(UsuarioController.esHashBcrypt("pass123"));
        assertFalse(UsuarioController.esHashBcrypt(null));
        assertFalse(UsuarioController.esHashBcrypt(""));
    }

    @Test
    void cifrarContrasenaCifraTextoPlanoYRespetaHashesYNulos() {
        String cifrada = usuarioController.cifrarContrasena("secreta");

        assertNotEquals("secreta", cifrada);
        assertTrue(passwordEncoder.matches("secreta", cifrada));
        assertSame(hashPass123, usuarioController.cifrarContrasena(hashPass123));
        assertNull(usuarioController.cifrarContrasena(null));
    }

    // ---------- buscarUsuario ----------

    @Test
    void buscarUsuarioPorIdOPorIdUsuario() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioPrueba));

        assertSame(usuarioPrueba, usuarioController.getUsuario(1, null).getBody());
        assertSame(usuarioPrueba, usuarioController.getUsuario(null, 1).getBody());
        // Si llegan ambos, prevalece "id"
        assertEquals(HttpStatus.OK, usuarioController.getUsuario(1, 99).getStatusCode());
    }

    @Test
    void buscarUsuarioInexistenteDevuelve404ConMensaje() {
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        ResponseEntity<?> r = usuarioController.getUsuario(999, null);

        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
        Map<?, ?> body = (Map<?, ?>) r.getBody();
        assertEquals(404, body.get("status"));
        assertEquals("El usuario con ID 999 no existe", body.get("mensaje"));
    }

    @Test
    void buscarUsuarioSinParametrosDevuelve400() {
        ResponseEntity<?> r = usuarioController.getUsuario(null, null);

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals(400, ((Map<?, ?>) r.getBody()).get("status"));
        verify(usuarioRepository, never()).findById(anyInt());
    }

    // ---------- addUsuario ----------

    @Test
    void addUsuarioCifraContrasenaYNormalizaIdCero() {
        Usuario nuevo = new Usuario(0, "Ana", "ana@example.com", "clave", "Estudiante", "#123456");

        ResponseEntity<Integer> r = usuarioController.addUsuario(nuevo);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertEquals(1, r.getBody());
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertNull(captor.getValue().getId_usuario());
        assertTrue(UsuarioController.esHashBcrypt(captor.getValue().getContrasena()));
        assertTrue(passwordEncoder.matches("clave", captor.getValue().getContrasena()));
    }

    @Test
    void addUsuarioConservaIdDistintoDeCero() {
        Usuario nuevo = new Usuario(5, "Ana", "ana@example.com", "clave", "Estudiante", "#123456");

        usuarioController.addUsuario(nuevo);

        assertEquals(5, nuevo.getId_usuario());
    }

    @Test
    void addUsuarioNuloDevuelve400() {
        ResponseEntity<Integer> r = usuarioController.addUsuario(null);

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals(0, r.getBody());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void addUsuarioConErrorDeBdDevuelve500() {
        when(usuarioRepository.save(any(Usuario.class))).thenThrow(new RuntimeException("BD caída"));

        ResponseEntity<Integer> r = usuarioController.addUsuario(usuarioPrueba);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, r.getStatusCode());
        assertEquals(0, r.getBody());
    }

    // ---------- updateUsuario ----------

    @Test
    void updateUsuarioSinContrasenaConservaLaActual() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioPrueba));
        Usuario cambios = new Usuario(1, "Carlos Editado", "carlos@example.com", null, "ADMIN", "#000000");

        ResponseEntity<Integer> r = usuarioController.updateUsuario(cambios);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody());
        assertEquals(hashPass123, cambios.getContrasena());
        verify(usuarioRepository).save(cambios);
    }

    @Test
    void updateUsuarioConContrasenaEnBlancoConservaLaActual() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioPrueba));
        Usuario cambios = new Usuario(1, "Carlos", "carlos@example.com", "   ", "ADMIN", "#000000");

        usuarioController.updateUsuario(cambios);

        assertEquals(hashPass123, cambios.getContrasena());
    }

    @Test
    void updateUsuarioConContrasenaNuevaLaCifra() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioPrueba));
        Usuario cambios = new Usuario(1, "Carlos", "carlos@example.com", "nueva", "ADMIN", "#000000");

        usuarioController.updateUsuario(cambios);

        assertTrue(passwordEncoder.matches("nueva", cambios.getContrasena()));
    }

    @Test
    void updateUsuarioInexistenteDevuelve404() {
        when(usuarioRepository.findById(7)).thenReturn(Optional.empty());

        ResponseEntity<Integer> r = usuarioController.updateUsuario(new Usuario(7, "X", "x", "x", "x", "x"));

        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void updateUsuarioNuloOSinIdDevuelve400() {
        assertEquals(HttpStatus.BAD_REQUEST, usuarioController.updateUsuario(null).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, usuarioController.updateUsuario(new Usuario()).getStatusCode());
        verify(usuarioRepository, never()).findById(anyInt());
    }

    @Test
    void updateUsuarioConErrorDeBdDevuelve500() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioPrueba));
        when(usuarioRepository.save(any(Usuario.class))).thenThrow(new RuntimeException("BD caída"));

        ResponseEntity<Integer> r = usuarioController.updateUsuario(usuarioPrueba);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, r.getStatusCode());
        assertEquals(0, r.getBody());
    }

    // ---------- deleteUsuario ----------

    @Test
    void deleteUsuarioTomaElIdDelCuerpoAntesQueDeLosParametros() {
        when(usuarioRepository.existsById(1)).thenReturn(true);

        ResponseEntity<Integer> r = usuarioController.deleteUsuario(usuarioPrueba, 2, 3);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody());
        verify(usuarioRepository).deleteById(1);
    }

    @Test
    void deleteUsuarioUsaIdUsuarioYLuegoId() {
        when(usuarioRepository.existsById(anyInt())).thenReturn(true);

        usuarioController.deleteUsuario(new Usuario(), 2, 3);
        usuarioController.deleteUsuario(null, null, 3);

        verify(usuarioRepository).deleteById(2);
        verify(usuarioRepository).deleteById(3);
    }

    @Test
    void deleteUsuarioSinIdDevuelve400() {
        ResponseEntity<Integer> r = usuarioController.deleteUsuario(null, null, null);

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        verify(usuarioRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteUsuarioInexistenteDevuelve404() {
        when(usuarioRepository.existsById(9)).thenReturn(false);

        assertEquals(HttpStatus.NOT_FOUND, usuarioController.deleteUsuario(null, 9, null).getStatusCode());
        verify(usuarioRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteUsuarioConRestriccionDeIntegridadDevuelve409() {
        when(usuarioRepository.existsById(1)).thenReturn(true);
        doThrow(new RuntimeException("FK")).when(usuarioRepository).deleteById(1);

        ResponseEntity<Integer> r = usuarioController.deleteUsuario(null, 1, null);

        assertEquals(HttpStatus.CONFLICT, r.getStatusCode());
        assertEquals(0, r.getBody());
    }

    // ---------- buscarUsuarios ----------

    @Test
    void buscarUsuariosDevuelveTodos() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuarioPrueba, new Usuario()));

        ResponseEntity<List<Usuario>> r = usuarioController.getUsuarios();

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(2, r.getBody().size());
        assertSame(usuarioPrueba, r.getBody().get(0));
    }

    @Test
    void buscarUsuariosSinRegistrosDevuelveListaVacia() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        assertTrue(usuarioController.getUsuarios().getBody().isEmpty());
    }
}
