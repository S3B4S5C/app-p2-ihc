package com.ihc.despensa.backend.health;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HealthControllerTest {

    private final JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
    private final HealthController controller = new HealthController(jdbcTemplate);

    @Test
    void returnsUpWhenPostgresqlResponds() {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class)).thenReturn(1);

        ResponseEntity<HealthController.HealthResponse> response = controller.health();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new HealthController.HealthResponse("UP", "UP"), response.getBody());
    }

    @Test
    void returns503WhenDatabaseIsUnavailable() {
        when(jdbcTemplate.queryForObject("SELECT 1", Integer.class))
                .thenThrow(new DataAccessResourceFailureException("Connection refused"));

        ResponseEntity<HealthController.HealthResponse> response = controller.health();

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertEquals(new HealthController.HealthResponse("DOWN", "DOWN"), response.getBody());
    }
}
