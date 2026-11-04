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

import com.mx.cbtis.gestor.modelo.Proyectos;
import com.mx.cbtis.gestor.repository.ProtectosRepository;

@ExtendWith(MockitoExtension.class)
class ProyectosControllerTest {

    @Mock
    private ProtectosRepository proyectosRepository;

    @InjectMocks
    private ProyectosController proyectosController;

    private Proyectos proyecto;

    @BeforeEach
    void setUp() {
        proyecto = new Proyectos(10, "My API Rest", "Sistema con 5 tablas", "2024-05-31", "2024-06-29",
                "En proceso", "/Images/animacion1.gif");
    }

    @Test
    void buscarProyectoPorIdProyectoOPorId() {
        when(proyectosRepository.findById(10)).thenReturn(Optional.of(proyecto));

        assertSame(proyecto, proyectosController.getProyecto(10, null).getBody());
        assertSame(proyecto, proyectosController.getProyecto(null, 10).getBody());
        // id_proyecto tiene prioridad sobre id
        assertEquals(HttpStatus.OK, proyectosController.getProyecto(10, 77).getStatusCode());
    }

    @Test
    void buscarProyectoInexistenteDevuelve404() {
        when(proyectosRepository.findById(99)).thenReturn(Optional.empty());

        ResponseEntity<?> r = proyectosController.getProyecto(99, null);

        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
        assertEquals("El proyecto con ID 99 no existe", ((Map<?, ?>) r.getBody()).get("mensaje"));
    }

    @Test
    void buscarProyectoSinParametrosDevuelve400() {
        ResponseEntity<?> r = proyectosController.getProyecto(null, null);

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals("Bad Request", ((Map<?, ?>) r.getBody()).get("error"));
        verify(proyectosRepository, never()).findById(anyInt());
    }

    @Test
    void addProyectoConIdCeroLoGuardaComoNuevo() {
        proyecto.setId_proyecto(0);

        ResponseEntity<Integer> r = proyectosController.addProyecto(proyecto);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertEquals(1, r.getBody());
        assertNull(proyecto.getId_proyecto());
        verify(proyectosRepository).save(proyecto);
    }

    @Test
    void addProyectoConIdNuloOPositivoNoLoModifica() {
        Proyectos sinId = new Proyectos();
        proyectosController.addProyecto(sinId);
        proyectosController.addProyecto(proyecto);

        assertNull(sinId.getId_proyecto());
        assertEquals(10, proyecto.getId_proyecto());
    }

    @Test
    void addProyectoNuloDevuelve400() {
        assertEquals(HttpStatus.BAD_REQUEST, proyectosController.addProyecto(null).getStatusCode());
        verify(proyectosRepository, never()).save(any());
    }

    @Test
    void addProyectoConErrorDeBdDevuelve500() {
        when(proyectosRepository.save(any(Proyectos.class))).thenThrow(new RuntimeException("BD"));

        ResponseEntity<Integer> r = proyectosController.addProyecto(proyecto);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, r.getStatusCode());
        assertEquals(0, r.getBody());
    }

    @Test
    void updateProyectoExistenteDevuelve200() {
        when(proyectosRepository.existsById(10)).thenReturn(true);

        ResponseEntity<Integer> r = proyectosController.updateProyecto(proyecto);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody());
        verify(proyectosRepository).save(proyecto);
    }

    @Test
    void updateProyectoNuloOSinIdDevuelve400() {
        assertEquals(HttpStatus.BAD_REQUEST, proyectosController.updateProyecto(null).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, proyectosController.updateProyecto(new Proyectos()).getStatusCode());
        verify(proyectosRepository, never()).existsById(anyInt());
    }

    @Test
    void updateProyectoInexistenteDevuelve404() {
        when(proyectosRepository.existsById(10)).thenReturn(false);

        assertEquals(HttpStatus.NOT_FOUND, proyectosController.updateProyecto(proyecto).getStatusCode());
        verify(proyectosRepository, never()).save(any());
    }

    @Test
    void updateProyectoConErrorDeBdDevuelve500() {
        when(proyectosRepository.existsById(10)).thenReturn(true);
        when(proyectosRepository.save(proyecto)).thenThrow(new RuntimeException("BD"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, proyectosController.updateProyecto(proyecto).getStatusCode());
    }

    @Test
    void deleteProyectoResuelveIdPorPrioridad() {
        when(proyectosRepository.existsById(anyInt())).thenReturn(true);

        proyectosController.deleteProyecto(proyecto, 20, 30);
        proyectosController.deleteProyecto(new Proyectos(), 20, 30);
        proyectosController.deleteProyecto(null, null, 30);

        verify(proyectosRepository).deleteById(10);
        verify(proyectosRepository).deleteById(20);
        verify(proyectosRepository).deleteById(30);
    }

    @Test
    void deleteProyectoSinIdDevuelve400() {
        assertEquals(HttpStatus.BAD_REQUEST, proyectosController.deleteProyecto(null, null, null).getStatusCode());
        verify(proyectosRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteProyectoInexistenteDevuelve404() {
        when(proyectosRepository.existsById(10)).thenReturn(false);

        assertEquals(HttpStatus.NOT_FOUND, proyectosController.deleteProyecto(proyecto, null, null).getStatusCode());
        verify(proyectosRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteProyectoConTareasAsociadasDevuelve409() {
        when(proyectosRepository.existsById(10)).thenReturn(true);
        doThrow(new RuntimeException("FK")).when(proyectosRepository).deleteById(10);

        ResponseEntity<Integer> r = proyectosController.deleteProyecto(proyecto, null, null);

        assertEquals(HttpStatus.CONFLICT, r.getStatusCode());
        assertEquals(0, r.getBody());
    }

    @Test
    void buscarProyectosDevuelveTodos() {
        when(proyectosRepository.findAll()).thenReturn(List.of(proyecto));

        ResponseEntity<List<Proyectos>> r = proyectosController.getProyectos();

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(List.of(proyecto), r.getBody());
    }

    @Test
    void buscarProyectosVacioDevuelveListaVacia() {
        when(proyectosRepository.findAll()).thenReturn(List.of());

        assertTrue(proyectosController.getProyectos().getBody().isEmpty());
    }
}
