package com.mx.cbtis.gestor.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.mx.cbtis.gestor.modelo.Comentarios;
import com.mx.cbtis.gestor.modelo.Tareas;
import com.mx.cbtis.gestor.modelo.Usuario;

/** Verifica las consultas nativas contra una BD H2 en modo MySQL. */
@DataJpaTest
class RepositoriosConsultasTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TareasRepository tareasRepository;

    @Autowired
    private ComentariosRepository comentariosRepository;

    @Test
    void findByCorreoDevuelveElUsuarioConSuHash() {
        usuarioRepository.save(new Usuario(null, "Ana", "ana@example.com", "$2a$10$hash", "Estudiante", "#111111"));
        usuarioRepository.save(new Usuario(null, "Beto", "beto@example.com", "$2a$10$otro", "Estudiante", "#222222"));

        Optional<Usuario> encontrado = usuarioRepository.findByCorreo("ana@example.com");

        assertTrue(encontrado.isPresent());
        assertEquals("Ana", encontrado.get().getNombre());
        assertEquals("$2a$10$hash", encontrado.get().getContrasena());
        assertTrue(usuarioRepository.findByCorreo("nadie@example.com").isEmpty());
    }

    @Test
    void findByIdProyectoFiltraPorProyecto() {
        tareasRepository.save(new Tareas(null, "T1", "d", "2024-06-13", "pendientes", "danger", 1));
        tareasRepository.save(new Tareas(null, "T2", "d", "2024-06-14", "encurso", "primary", 1));
        tareasRepository.save(new Tareas(null, "T3", "d", "2024-06-15", "terminado", "success", 2));

        List<Tareas> delProyecto1 = tareasRepository.findByIdProyecto(1);

        assertEquals(2, delProyecto1.size());
        assertTrue(delProyecto1.stream().allMatch(t -> t.getId_proyecto() == 1));
        assertTrue(tareasRepository.findByIdProyecto(99).isEmpty());
    }

    @Test
    void findByTareaFiltraPorTarea() {
        comentariosRepository.save(new Comentarios(null, "Ana", 10, "Hola", "24/06/26 13:48"));
        comentariosRepository.save(new Comentarios(null, "Beto", 11, "Otro", "24/06/26 13:49"));

        List<Comentarios> deTarea10 = comentariosRepository.findByTarea(10);

        assertEquals(1, deTarea10.size());
        assertEquals("Hola", deTarea10.get(0).getTexto_comentario());
        assertTrue(comentariosRepository.findByTarea(12).isEmpty());
    }
}
