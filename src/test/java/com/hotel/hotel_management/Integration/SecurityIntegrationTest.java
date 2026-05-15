package com.hotel.hotel_management.Integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void unauthenticatedAccess_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/rezervari"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void adminOnlyEndpoint_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(get("/inventar-camere"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void loginWithWrongCredentials_redirectsToLoginError() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "utilizator_inexistent")
                        .param("password", "parola_gresita"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void loginWithCorrectCredentials_redirectsToHome() throws Exception {
        mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "admin")
                        .param("password", "admin123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void h2Console_isAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/h2-console"))
                .andExpect(status().isNotFound());
    }
}
