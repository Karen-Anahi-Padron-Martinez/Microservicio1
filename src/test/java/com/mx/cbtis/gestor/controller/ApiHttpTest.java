package com.mx.cbtis.gestor.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.mx.cbtis.gestor.config.SeguridadConfig;
import com.mx.cbtis.gestor.modelo.Usuario;
import com.mx.cbtis.gestor.repository.UsuarioRepository;

/** Pruebas de la capa HTTP: serialización JSON, códigos de estado y política CORS. */
@WebMvcTest(UsuarioController.class)
@Import(SeguridadConfig.class)
class ApiHttpTest {

    private static final String HASH = "$2a$10$abcdefghijklmnopqrstuuJ8m1v4bq9yYyIu1sT8tC8Q0R1Qa7l2G";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void laRespuestaJsonNoExponeLaContrasena() throws Exception {
        when(usuarioRepository.findAll()).thenReturn(List.of(
                new Usuario(1, "Ana", "ana@example.com", HASH, "Estudiante", "#111111")));

        mockMvc.perform(get("/buscarUsuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Ana"))
                .andExpect(jsonPath("$[0].contrasena").doesNotExist())
                .andExpect(content().string(not(containsString("$2a$"))));
    }

    @Test
    void laContrasenaSiSeAceptaEnLaPeticion() throws Exception {
        mockMvc.perform(post("/addUsuario")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana\",\"correo_electronico\":\"ana@example.com\",\"contrasena\":\"clave\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));

        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void buscarUsuarioSinParametroDevuelve400Json() throws Exception {
        mockMvc.perform(get("/buscarUsuario"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void buscarUsuarioExistenteDevuelve200() throws Exception {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(new Usuario(1, "Ana", "a", HASH, "r", "i")));

        mockMvc.perform(get("/buscarUsuario").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_usuario").value(1))
                .andExpect(jsonPath("$.contrasena").doesNotExist());
    }

    @Test
    void corsPermiteElOrigenDelCliente() throws Exception {
        mockMvc.perform(options("/buscarUsuarios")
                        .header("Origin", "http://localhost:8082")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:8082"));
    }

    @Test
    void corsRechazaOrigenesNoAutorizados() throws Exception {
        mockMvc.perform(options("/buscarUsuarios")
                        .header("Origin", "http://sitio-malicioso.example")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}
