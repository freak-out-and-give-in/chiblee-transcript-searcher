package com.scrape.controller;

import com.scrape.exception.UnauthorizedAccessException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class AdminControllerTest {

    @Container
    static final MySQLContainer<?> databaseContainer = new MySQLContainer<>("mysql:latest");

    @DynamicPropertySource
    static void mySQLProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", databaseContainer::getJdbcUrl);
        registry.add("spring.datasource.username", databaseContainer::getUsername);
        registry.add("spring.datasource.password", databaseContainer::getPassword);
    }

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    void tearDown() {
        mockMvc = null;
    }

    @TestPropertySource(locations = {"classpath:application-test.properties"})
    @Nested
    class Authorized_AdminPage {

        @Test
        void showAdminPage() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                    .andExpect(status().isOk());
        }

        @Test
        void initialiseDatabase() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/admin/initDB"))
                    .andExpect(status().isOk());
        }

        @Test
        void downloadTranscripts() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/admin/downloadTranscripts"))
                    .andExpect(status().isOk());
        }

        @Test
        void initialiseTranscripts() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/admin/initTranscripts"))
                    .andExpect(status().isOk());
        }

        @Test
        void downloadAll() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/admin/downloadAll"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    class Unauthorized_AdminPage {

        @Test
        void showAdminPage() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/admin"))
                    .andExpect(status().isForbidden())
                    .andExpect(result -> Assertions.assertInstanceOf(UnauthorizedAccessException.class, result.getResolvedException()));
        }

        @Test
        void initialiseDatabase() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/admin/initDB"))
                    .andExpect(status().isForbidden())
                    .andExpect(result -> Assertions.assertInstanceOf(UnauthorizedAccessException.class, result.getResolvedException()));
        }

        @Test
        void downloadTranscripts() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/admin/downloadTranscripts"))
                    .andExpect(status().isForbidden())
                    .andExpect(result -> Assertions.assertInstanceOf(UnauthorizedAccessException.class, result.getResolvedException()));
        }

        @Test
        void initialiseTranscripts() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/admin/initTranscripts"))
                    .andExpect(status().isForbidden())
                    .andExpect(result -> Assertions.assertInstanceOf(UnauthorizedAccessException.class, result.getResolvedException()));
        }

        @Test
        void downloadAll() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/admin/downloadAll"))
                    .andExpect(status().isForbidden())
                    .andExpect(result -> Assertions.assertInstanceOf(UnauthorizedAccessException.class, result.getResolvedException()));
        }
    }
}