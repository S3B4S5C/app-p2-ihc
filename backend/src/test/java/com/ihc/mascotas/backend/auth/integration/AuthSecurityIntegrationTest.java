package com.ihc.mascotas.backend.auth.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.greaterThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:authdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=validate",
        "app.security.jwt.secret=integration-test-secret-with-at-least-32-bytes",
        "app.security.jwt.issuer=mascotas-al-dia-test"
})
@AutoConfigureMockMvc
class AuthSecurityIntegrationTest {

    @Autowired
    MockMvc mvc;

    @Test
    void protectedEndpointRejectsAnonymousRequest() throws Exception {
        mvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerReturnsTokenThatCanAccessProtectedEndpoint() throws Exception {
        String registerBody = """
                {"fullName":"Lucía Gómez","email":"lucia.integration@example.com","password":"password123"}
                """;

        String body = mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.expiresInSeconds", greaterThan(0)))
                .andReturn().getResponse().getContentAsString();

        Matcher tokenMatcher = Pattern.compile("\"accessToken\":\"([^\"]+)\"").matcher(body);
        if (!tokenMatcher.find()) {
            throw new AssertionError("La respuesta no contiene accessToken");
        }
        String token = tokenMatcher.group(1);

        mvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("lucia.integration@example.com"))
                .andExpect(jsonPath("$.fullName").value("Lucía Gómez"));
    }
    @Test
    void passwordResetFlowUpdatesCredentials() throws Exception {
        String email = "recovery.integration@example.com";
        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"Recovery Test","email":"recovery.integration@example.com","password":"password123"}
                                """))
                .andExpect(status().isCreated());

        String resetBody = mvc.perform(post("/api/auth/password-reset/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"recovery.integration@example.com"}
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Matcher resetTokenMatcher = Pattern.compile("\"resetToken\":\"([^\"]+)\"").matcher(resetBody);
        if (!resetTokenMatcher.find()) {
            throw new AssertionError("La respuesta no contiene resetToken");
        }
        String resetToken = resetTokenMatcher.group(1);

        mvc.perform(post("/api/auth/password-reset/confirm")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + resetToken + "\",\"newPassword\":\"password456\"}"))
                .andExpect(status().isNoContent());

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"password456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.email").value(email));
    }

    @Test
    void passwordResetRequestIsPublic() throws Exception {
        mvc.perform(post("/api/auth/password-reset/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"missing@example.com"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void passwordChangeRequiresAuthentication() throws Exception {
        mvc.perform(post("/api/auth/password/change")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"currentPassword":"password123","newPassword":"password456"}
                                """))
                .andExpect(status().isUnauthorized());
    }

}
