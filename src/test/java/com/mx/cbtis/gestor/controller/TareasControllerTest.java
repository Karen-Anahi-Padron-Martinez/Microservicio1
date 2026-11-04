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

import com.mx.cbtis.gestor.modelo.Tareas;
import com.mx.cbtis.gestor.repository.TareasRepository;

@ExtendWith(MockitoExtension.class)
class TareasControllerTest {

    @Mock
    private TareasRepository tareasRepository;

    @InjectMocks
    private TareasController tareasController;

    private Tareas tarea;

    @BeforeEach
    void setUp() {
        tarea = new Tareas(199, "Base de datos", "Estructura de tablas", "2024-06-13", "pendientes", "danger", 197);
    }

    // ---------- getTarea (tareas de un proyecto) ----------

    @Test
    void tareasDeProyectoTomaElIdDelCuerpoPrimero() {
        when(tareasRepository.findByIdProyecto(197)).thenReturn(List.of(tarea));

        ResponseEntity<List<Tareas>> r = tareasController.getTarea(new Tareas(null, null, null, null, null, null, 197), 1, 2);

        assertEquals(List.of(tarea), r.getBody());
        verify(tareasRepository).findByIdProyecto(197);
    }

    @Test
    void tareasDeProyectoUsaIdProyectoYLuegoId() {
        when(tareasRepository.findByIdProyecto(anyInt())).thenReturn(List.of());

        tareasController.getTarea(new Tareas(), 1, 2);
        tareasController.getTarea(null, null, 2);

        verify(tareasRepository).findByIdProyecto(1);
        verify(tareasRepository).findByIdProyecto(2);
    }

    @Test
    void tareasDeProyectoSinIdDevuelveListaVaciaSinConsultar() {
        ResponseEntity<List<Tareas>> r = tareasController.getTarea(null, null, null);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertTrue(r.getBody().isEmpty());
        verify(tareasRepository, never()).findByIdProyecto(anyInt());
    }

    // ---------- buscarTarea ----------

    @Test
    void buscarTareaPorIdTareaOPorId() {
        when(tareasRepository.findById(199)).thenReturn(Optional.of(tarea));

        assertSame(tarea, tareasController.getTarea(199, null).getBody());
        assertSame(tarea, tareasController.getTarea(null, 199).getBody());
        assertEquals(HttpStatus.OK, tareasController.getTarea(199, 5).getStatusCode());
    }

    @Test
    void buscarTareaInexistenteDevuelve404() {
        when(tareasRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> r = tareasController.getTarea(1, (Integer) null);

        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
        assertEquals("La tarea con ID 1 no existe", ((Map<?, ?>) r.getBody()).get("mensaje"));
    }

    @Test
    void buscarTareaSinParametrosDevuelve400() {
        ResponseEntity<?> r = tareasController.getTarea((Integer) null, (Integer) null);

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        verify(tareasRepository, never()).findById(anyInt());
    }

    // ---------- addTareas ----------

    @Test
    void addTareaConIdCeroLaGuardaComoNueva() {
        tarea.setId_tarea(0);

        ResponseEntity<Integer> r = tareasController.addTareas(tarea);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertEquals(1, r.getBody());
        assertNull(tarea.getId_tarea());
        verify(tareasRepository).save(tarea);
    }

    @Test
    void addTareaConIdPositivoNoLoModifica() {
        tareasController.addTareas(tarea);

        assertEquals(199, tarea.getId_tarea());
    }

    @Test
    void addTareaSinIdLaGuardaSinModificar() {
        Tareas nueva = new Tareas(null, "UI", "vistas", "2024-07-01", "encurso", "primary", 197);

        assertEquals(HttpStatus.CREATED, tareasController.addTareas(nueva).getStatusCode());
        assertNull(nueva.getId_tarea());
        verify(tareasRepository).save(nueva);
    }

    @Test
    void addTareaNulaDevuelve400() {
        assertEquals(HttpStatus.BAD_REQUEST, tareasController.addTareas(null).getStatusCode());
        verify(tareasRepository, never()).save(any());
    }

    @Test
    void addTareaConErrorDeBdDevuelve500() {
        when(tareasRepository.save(any(Tareas.class))).thenThrow(new RuntimeException("BD"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, tareasController.addTareas(tarea).getStatusCode());
    }

    // ---------- updateTareas ----------

    @Test
    void updateTareaExistenteDevuelve200() {
        when(tareasRepository.existsById(199)).thenReturn(true);

        ResponseEntity<Integer> r = tareasController.updateTareas(tarea);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        verify(tareasRepository).save(tarea);
    }

    @Test
    void updateTareaNulaOSinIdDevuelve400() {
        assertEquals(HttpStatus.BAD_REQUEST, tareasController.updateTareas(null).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, tareasController.updateTareas(new Tareas()).getStatusCode());
    }

    @Test
    void updateTareaInexistenteDevuelve404() {
        when(tareasRepository.existsById(199)).thenReturn(false);

        assertEquals(HttpStatus.NOT_FOUND, tareasController.updateTareas(tarea).getStatusCode());
        verify(tareasRepository, never()).save(any());
    }

    @Test
    void updateTareaConErrorDeBdDevuelve500() {
        when(tareasRepository.existsById(199)).thenReturn(true);
        when(tareasRepository.save(tarea)).thenThrow(new RuntimeException("BD"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, tareasController.updateTareas(tarea).getStatusCode());
    }

    // ---------- deleteTareas ----------

    @Test
    void deleteTareaResuelveIdPorPrioridad() {
        when(tareasRepository.existsById(anyInt())).thenReturn(true);

        tareasController.deleteTareas(tarea, 2, 3);
        tareasController.deleteTareas(new Tareas(), 2, 3);
        tareasController.deleteTareas(null, null, 3);

        verify(tareasRepository).deleteById(199);
        verify(tareasRepository).deleteById(2);
        verify(tareasRepository).deleteById(3);
    }

    @Test
    void deleteTareaSinIdDevuelve400() {
        assertEquals(HttpStatus.BAD_REQUEST, tareasController.deleteTareas(null, null, null).getStatusCode());
    }

    @Test
    void deleteTareaInexistenteDevuelve404() {
        when(tareasRepository.existsById(2)).thenReturn(false);

        assertEquals(HttpStatus.NOT_FOUND, tareasController.deleteTareas(null, 2, null).getStatusCode());
        verify(tareasRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteTareaConComentariosAsociadosDevuelve409() {
        when(tareasRepository.existsById(199)).thenReturn(true);
        doThrow(new RuntimeException("FK")).when(tareasRepository).deleteById(199);

        assertEquals(HttpStatus.CONFLICT, tareasController.deleteTareas(tarea, null, null).getStatusCode());
    }

    // ---------- buscarTareas ----------

    @Test
    void buscarTareasDevuelveTodas() {
        when(tareasRepository.findAll()).thenReturn(List.of(tarea));

        assertEquals(List.of(tarea), tareasController.getTareas().getBody());
    }
}
