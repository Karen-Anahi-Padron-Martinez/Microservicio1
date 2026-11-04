package com.mx.cbtis.gestor.modelo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

/** Verifica que constructores y accesores de las entidades conserven cada campo sin mezclarlos. */
class ModelosTest {

    @Test
    void usuarioConstructorYAccesores() {
        Usuario u = new Usuario(1, "Ana", "ana@example.com", "hash", "Estudiante", "#111111");
        assertAll(
                () -> assertEquals(1, u.getId_usuario()),
                () -> assertEquals("Ana", u.getNombre()),
                () -> assertEquals("ana@example.com", u.getCorreo_electronico()),
                () -> assertEquals("hash", u.getContrasena()),
                () -> assertEquals("Estudiante", u.getRol()),
                () -> assertEquals("#111111", u.getImagen_usuario()));

        Usuario v = new Usuario();
        assertNull(v.getNombre());
        v.setId_usuario(2);
        v.setNombre("Beto");
        v.setCorreo_electronico("beto@example.com");
        v.setContrasena("otro");
        v.setRol("Docente");
        v.setImagen_usuario("#222222");
        assertAll(
                () -> assertEquals(2, v.getId_usuario()),
                () -> assertEquals("Beto", v.getNombre()),
                () -> assertEquals("beto@example.com", v.getCorreo_electronico()),
                () -> assertEquals("otro", v.getContrasena()),
                () -> assertEquals("Docente", v.getRol()),
                () -> assertEquals("#222222", v.getImagen_usuario()));
    }

    @Test
    void proyectosConstructorYAccesores() {
        Proyectos p = new Proyectos(1, "API", "desc", "2024-05-31", "2024-06-29", "En proceso", "/f.png");
        assertAll(
                () -> assertEquals(1, p.getId_proyecto()),
                () -> assertEquals("API", p.getNombre()),
                () -> assertEquals("desc", p.getDescripcion()),
                () -> assertEquals("2024-05-31", p.getFecha_inicio()),
                () -> assertEquals("2024-06-29", p.getFecha_finalizacion()),
                () -> assertEquals("En proceso", p.getEstado_proyecto()),
                () -> assertEquals("/f.png", p.getFondo()));

        Proyectos q = new Proyectos();
        q.setId_proyecto(2);
        q.setNombre("Web");
        q.setDescripcion("d2");
        q.setFecha_inicio("2025-01-01");
        q.setFecha_finalizacion("2025-02-01");
        q.setEstado_proyecto("Terminado");
        q.setFondo("/g.png");
        assertAll(
                () -> assertEquals(2, q.getId_proyecto()),
                () -> assertEquals("Web", q.getNombre()),
                () -> assertEquals("d2", q.getDescripcion()),
                () -> assertEquals("2025-01-01", q.getFecha_inicio()),
                () -> assertEquals("2025-02-01", q.getFecha_finalizacion()),
                () -> assertEquals("Terminado", q.getEstado_proyecto()),
                () -> assertEquals("/g.png", q.getFondo()));
    }

    @Test
    void tareasConstructorYAccesores() {
        Tareas t = new Tareas(1, "BD", "tablas", "2024-06-13", "pendientes", "danger", 9);
        assertAll(
                () -> assertEquals(1, t.getId_tarea()),
                () -> assertEquals("BD", t.getNombre()),
                () -> assertEquals("tablas", t.getDescripcion()),
                () -> assertEquals("2024-06-13", t.getFecha_vencimiento()),
                () -> assertEquals("pendientes", t.getLista()),
                () -> assertEquals("danger", t.getEstado()),
                () -> assertEquals(9, t.getId_proyecto()));

        Tareas s = new Tareas();
        s.setId_tarea(2);
        s.setNombre("UI");
        s.setDescripcion("vistas");
        s.setFecha_vencimiento("2024-07-01");
        s.setLista("encurso");
        s.setEstado("primary");
        s.setId_proyecto(8);
        assertAll(
                () -> assertEquals(2, s.getId_tarea()),
                () -> assertEquals("UI", s.getNombre()),
                () -> assertEquals("vistas", s.getDescripcion()),
                () -> assertEquals("2024-07-01", s.getFecha_vencimiento()),
                () -> assertEquals("encurso", s.getLista()),
                () -> assertEquals("primary", s.getEstado()),
                () -> assertEquals(8, s.getId_proyecto()));
    }

    @Test
    void comentariosConstructorYAccesores() {
        Comentarios c = new Comentarios(1, "Ana", 5, "Hola", "24/06/26 13:48");
        assertAll(
                () -> assertEquals(1, c.getId_comentario()),
                () -> assertEquals("Ana", c.getId_usuario()),
                () -> assertEquals(5, c.getId_tarea()),
                () -> assertEquals("Hola", c.getTexto_comentario()),
                () -> assertEquals("24/06/26 13:48", c.getFecha_hora()));

        Comentarios d = new Comentarios();
        d.setId_comentario(2);
        d.setId_usuario("Beto");
        d.setId_tarea(6);
        d.setTexto_comentario("Adiós");
        d.setFecha_hora("24/06/27 10:00");
        assertAll(
                () -> assertEquals(2, d.getId_comentario()),
                () -> assertEquals("Beto", d.getId_usuario()),
                () -> assertEquals(6, d.getId_tarea()),
                () -> assertEquals("Adiós", d.getTexto_comentario()),
                () -> assertEquals("24/06/27 10:00", d.getFecha_hora()));
    }

    @Test
    void asignacionesConstructorYAccesores() {
        Asignaciones a = new Asignaciones(1, 2, 3, "2024-06-13");
        assertAll(
                () -> assertEquals(1, a.getId_asignacion()),
                () -> assertEquals(2, a.getId_usuario()),
                () -> assertEquals(3, a.getId_tarea()),
                () -> assertEquals("2024-06-13", a.getFecha_asignacion()));

        Asignaciones b = new Asignaciones();
        b.setId_asignacion(4);
        b.setId_usuario(5);
        b.setId_tarea(6);
        b.setFecha_asignacion("2024-07-01");
        assertAll(
                () -> assertEquals(4, b.getId_asignacion()),
                () -> assertEquals(5, b.getId_usuario()),
                () -> assertEquals(6, b.getId_tarea()),
                () -> assertEquals("2024-07-01", b.getFecha_asignacion()));
    }
}
