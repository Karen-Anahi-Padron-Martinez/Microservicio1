package com.mx.cbtis.gestor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mx.cbtis.gestor.modelo.Comentarios;
import com.mx.cbtis.gestor.repository.ComentariosRepository;

@ExtendWith(MockitoExtension.class)
class ComentariosControllerTest {

    @Mock
    private ComentariosRepository comentariosRepository;

    @InjectMocks
    private ComentariosController comentariosController;

    private Comentarios comentario;

    @BeforeEach
    void setUp() {
        comentario = new Comentarios(201, "Usuario Demo", 199, "Subirlo ya", "2024-06-26 13:48:00");
    }

    // ---------- getComentario (comentarios de una tarea) ----------

    @Test
    void comentariosDeTareaTomaElIdDelCuerpoPrimero() {
        when(comentariosRepository.findByTarea(199)).thenReturn(List.of(comentario));

        ResponseEntity<List<Comentarios>> r = comentariosController.getComentario(
                new Comentarios(null, null, 199, null, null), 1, 2);

        assertEquals(List.of(comentario), r.getBody());
        verify(comentariosRepository).findByTarea(199);
    }

    @Test
    void comentariosDeTareaUsaIdTareaYLuegoId() {
        when(comentariosRepository.findByTarea(anyInt())).thenReturn(List.of());

        comentariosController.getComentario(new Comentarios(), 1, 2);
        comentariosController.getComentario(null, null, 2);

        verify(comentariosRepository).findByTarea(1);
        verify(comentariosRepository).findByTarea(2);
    }

    @Test
    void comentariosDeTareaSinIdDevuelveListaVacia() {
        ResponseEntity<List<Comentarios>> r = comentariosController.getComentario(null, null, null);

        assertTrue(r.getBody().isEmpty());
        verify(comentariosRepository, never()).findByTarea(anyInt());
    }

    // ---------- buscarComentario ----------

    @Test
    void buscarComentarioPorIdOPorIdComentario() {
        when(comentariosRepository.findById(201)).thenReturn(Optional.of(comentario));

        assertSame(comentario, comentariosController.getComentario(201, (Integer) null).getBody());
        assertSame(comentario, comentariosController.getComentario(null, 201).getBody());
        assertEquals(HttpStatus.OK, comentariosController.getComentario(201, 3).getStatusCode());
    }

    @Test
    void buscarComentarioInexistenteDevuelve404() {
        when(comentariosRepository.findById(3)).thenReturn(Optional.empty());

        ResponseEntity<?> r = comentariosController.getComentario(3, (Integer) null);

        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
        assertEquals("El comentario con ID 3 no existe", ((Map<?, ?>) r.getBody()).get("mensaje"));
    }

    @Test
    void buscarComentarioSinParametrosDevuelve400() {
        ResponseEntity<?> r = comentariosController.getComentario((Integer) null, (Integer) null);

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        verify(comentariosRepository, never()).findById(anyInt());
    }

    // ---------- addComentario ----------

    @Test
    void addComentarioConIdCeroLoGuardaComoNuevo() {
        comentario.setId_comentario(0);

        ResponseEntity<Integer> r = comentariosController.addComentario(comentario);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertNull(comentario.getId_comentario());
        verify(comentariosRepository).save(comentario);
    }

    @Test
    void addComentarioConIdPositivoNoLoModifica() {
        comentariosController.addComentario(comentario);

        assertEquals(201, comentario.getId_comentario());
    }

    @Test
    void addComentarioSinIdLoGuardaSinModificar() {
        Comentarios nuevo = new Comentarios(null, "Ana", 199, "Hola", "24/06/26 13:48");

        assertEquals(HttpStatus.CREATED, comentariosController.addComentario(nuevo).getStatusCode());
        assertNull(nuevo.getId_comentario());
        verify(comentariosRepository).save(nuevo);
    }

    @Test
    void addComentarioNuloDevuelve400() {
        assertEquals(HttpStatus.BAD_REQUEST, comentariosController.addComentario(null).getStatusCode());
        verify(comentariosRepository, never()).save(any());
    }

    @Test
    void addComentarioConErrorDeBdDevuelve500() {
        when(comentariosRepository.save(any(Comentarios.class))).thenThrow(new RuntimeException("BD"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, comentariosController.addComentario(comentario).getStatusCode());
    }

    // ---------- updateComentario ----------

    @Test
    void updateComentarioExistenteDevuelve200() {
        when(comentariosRepository.existsById(201)).thenReturn(true);

        assertEquals(HttpStatus.OK, comentariosController.updateComentario(comentario).getStatusCode());
        verify(comentariosRepository).save(comentario);
    }

    @Test
    void updateComentarioNuloOSinIdDevuelve400() {
        assertEquals(HttpStatus.BAD_REQUEST, comentariosController.updateComentario(null).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, comentariosController.updateComentario(new Comentarios()).getStatusCode());
    }

    @Test
    void updateComentarioInexistenteDevuelve404() {
        when(comentariosRepository.existsById(201)).thenReturn(false);

        assertEquals(HttpStatus.NOT_FOUND, comentariosController.updateComentario(comentario).getStatusCode());
        verify(comentariosRepository, never()).save(any());
    }

    @Test
    void updateComentarioConErrorDeBdDevuelve500() {
        when(comentariosRepository.existsById(201)).thenReturn(true);
        when(comentariosRepository.save(comentario)).thenThrow(new RuntimeException("BD"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, comentariosController.updateComentario(comentario).getStatusCode());
    }

    // ---------- deleteComentario ----------

    @Test
    void deleteComentarioResuelveIdPorPrioridad() {
        when(comentariosRepository.existsById(anyInt())).thenReturn(true);

        comentariosController.deleteComentario(comentario, 2, 3);
        comentariosController.deleteComentario(new Comentarios(), 2, 3);
        comentariosController.deleteComentario(null, null, 3);

        verify(comentariosRepository).deleteById(201);
        verify(comentariosRepository).deleteById(2);
        verify(comentariosRepository).deleteById(3);
    }

    @Test
    void deleteComentarioSinIdDevuelve400() {
        assertEquals(HttpStatus.BAD_REQUEST, comentariosController.deleteComentario(null, null, null).getStatusCode());
    }

    @Test
    void deleteComentarioInexistenteDevuelve404() {
        when(comentariosRepository.existsById(2)).thenReturn(false);

        assertEquals(HttpStatus.NOT_FOUND, comentariosController.deleteComentario(null, 2, null).getStatusCode());
        verify(comentariosRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteComentarioConErrorDeBdDevuelve409() {
        when(comentariosRepository.existsById(201)).thenReturn(true);
        doThrow(new RuntimeException("BD")).when(comentariosRepository).deleteById(201);

        assertEquals(HttpStatus.CONFLICT, comentariosController.deleteComentario(comentario, null, null).getStatusCode());
    }

    // ---------- buscarComentarios ----------

    @Test
    void buscarComentariosDevuelveTodos() {
        when(comentariosRepository.findAll()).thenReturn(List.of(comentario));

        assertEquals(List.of(comentario), comentariosController.getComentarios().getBody());
    }
}
