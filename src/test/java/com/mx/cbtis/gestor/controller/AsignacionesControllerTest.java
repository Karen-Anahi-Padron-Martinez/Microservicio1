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

import com.mx.cbtis.gestor.modelo.Asignaciones;
import com.mx.cbtis.gestor.repository.AsignacionesRepository;

@ExtendWith(MockitoExtension.class)
class AsignacionesControllerTest {

    @Mock
    private AsignacionesRepository asignacionesRepository;

    @InjectMocks
    private AsignacionesController asignacionesController;

    private Asignaciones asignacion;

    @BeforeEach
    void setUp() {
        asignacion = new Asignaciones(5, 131, 199, "2024-06-13");
    }

    @Test
    void buscarAsignacionPorIdOPorIdAsignacion() {
        when(asignacionesRepository.findById(5)).thenReturn(Optional.of(asignacion));

        assertSame(asignacion, asignacionesController.getAsignacion(5, null).getBody());
        assertSame(asignacion, asignacionesController.getAsignacion(null, 5).getBody());
        // "id" tiene prioridad sobre "id_asignacion"
        assertEquals(HttpStatus.OK, asignacionesController.getAsignacion(5, 88).getStatusCode());
    }

    @Test
    void buscarAsignacionInexistenteDevuelve404() {
        when(asignacionesRepository.findById(50)).thenReturn(Optional.empty());

        ResponseEntity<?> r = asignacionesController.getAsignacion(50, null);

        assertEquals(HttpStatus.NOT_FOUND, r.getStatusCode());
        assertEquals("La asignación con ID 50 no existe", ((Map<?, ?>) r.getBody()).get("mensaje"));
    }

    @Test
    void buscarAsignacionSinParametrosDevuelve400() {
        ResponseEntity<?> r = asignacionesController.getAsignacion(null, null);

        assertEquals(HttpStatus.BAD_REQUEST, r.getStatusCode());
        assertEquals(400, ((Map<?, ?>) r.getBody()).get("status"));
        verify(asignacionesRepository, never()).findById(anyInt());
    }

    @Test
    void addAsignacionConIdCeroLoGuardaComoNueva() {
        asignacion.setId_asignacion(0);

        ResponseEntity<Integer> r = asignacionesController.addAsignacion(asignacion);

        assertEquals(HttpStatus.CREATED, r.getStatusCode());
        assertEquals(1, r.getBody());
        assertNull(asignacion.getId_asignacion());
        verify(asignacionesRepository).save(asignacion);
    }

    @Test
    void addAsignacionConIdPositivoNoLoModifica() {
        asignacionesController.addAsignacion(asignacion);

        assertEquals(5, asignacion.getId_asignacion());
    }

    @Test
    void addAsignacionSinIdLaGuardaSinModificar() {
        Asignaciones nueva = new Asignaciones(null, 131, 199, "2024-06-13");

        assertEquals(HttpStatus.CREATED, asignacionesController.addAsignacion(nueva).getStatusCode());
        assertNull(nueva.getId_asignacion());
        verify(asignacionesRepository).save(nueva);
    }

    @Test
    void addAsignacionNulaDevuelve400() {
        assertEquals(HttpStatus.BAD_REQUEST, asignacionesController.addAsignacion(null).getStatusCode());
        verify(asignacionesRepository, never()).save(any());
    }

    @Test
    void addAsignacionConErrorDeBdDevuelve500() {
        when(asignacionesRepository.save(any(Asignaciones.class))).thenThrow(new RuntimeException("BD"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, asignacionesController.addAsignacion(asignacion).getStatusCode());
    }

    @Test
    void updateAsignacionExistenteDevuelve200() {
        when(asignacionesRepository.existsById(5)).thenReturn(true);

        ResponseEntity<Integer> r = asignacionesController.updateAsignacion(asignacion);

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(1, r.getBody());
        verify(asignacionesRepository).save(asignacion);
    }

    @Test
    void updateAsignacionNulaOSinIdDevuelve400() {
        assertEquals(HttpStatus.BAD_REQUEST, asignacionesController.updateAsignacion(null).getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, asignacionesController.updateAsignacion(new Asignaciones()).getStatusCode());
    }

    @Test
    void updateAsignacionInexistenteDevuelve404() {
        when(asignacionesRepository.existsById(5)).thenReturn(false);

        assertEquals(HttpStatus.NOT_FOUND, asignacionesController.updateAsignacion(asignacion).getStatusCode());
        verify(asignacionesRepository, never()).save(any());
    }

    @Test
    void updateAsignacionConErrorDeBdDevuelve500() {
        when(asignacionesRepository.existsById(5)).thenReturn(true);
        when(asignacionesRepository.save(asignacion)).thenThrow(new RuntimeException("BD"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, asignacionesController.updateAsignacion(asignacion).getStatusCode());
    }

    @Test
    void deleteAsignacionResuelveIdPorPrioridad() {
        when(asignacionesRepository.existsById(anyInt())).thenReturn(true);

        asignacionesController.deleteAsignacion(asignacion, 6, 7);
        asignacionesController.deleteAsignacion(new Asignaciones(), 6, 7);
        asignacionesController.deleteAsignacion(null, null, 7);

        verify(asignacionesRepository).deleteById(5);
        verify(asignacionesRepository).deleteById(6);
        verify(asignacionesRepository).deleteById(7);
    }

    @Test
    void deleteAsignacionSinIdDevuelve400() {
        assertEquals(HttpStatus.BAD_REQUEST, asignacionesController.deleteAsignacion(null, null, null).getStatusCode());
    }

    @Test
    void deleteAsignacionInexistenteDevuelve404() {
        when(asignacionesRepository.existsById(6)).thenReturn(false);

        assertEquals(HttpStatus.NOT_FOUND, asignacionesController.deleteAsignacion(null, 6, null).getStatusCode());
        verify(asignacionesRepository, never()).deleteById(anyInt());
    }

    @Test
    void deleteAsignacionConErrorDeBdDevuelve409() {
        when(asignacionesRepository.existsById(5)).thenReturn(true);
        doThrow(new RuntimeException("FK")).when(asignacionesRepository).deleteById(5);

        assertEquals(HttpStatus.CONFLICT, asignacionesController.deleteAsignacion(asignacion, null, null).getStatusCode());
    }

    @Test
    void buscarAsignacionesDevuelveTodas() {
        when(asignacionesRepository.findAll()).thenReturn(List.of(asignacion));

        ResponseEntity<List<Asignaciones>> r = asignacionesController.getAsignaciones();

        assertEquals(HttpStatus.OK, r.getStatusCode());
        assertEquals(List.of(asignacion), r.getBody());
    }

    @Test
    void buscarAsignacionesVacioDevuelveListaVacia() {
        when(asignacionesRepository.findAll()).thenReturn(List.of());

        assertTrue(asignacionesController.getAsignaciones().getBody().isEmpty());
    }
}
