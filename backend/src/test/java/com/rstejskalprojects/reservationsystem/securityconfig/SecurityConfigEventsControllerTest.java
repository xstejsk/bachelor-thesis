package com.rstejskalprojects.reservationsystem.securityconfig;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.model.dto.EventDTO;
import com.rstejskalprojects.reservationsystem.util.JwtUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigEventsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Mock
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    UserDetails superAdmin;
    UserDetails admin;
    UserDetails user;

    private final String SUPER_ADMIN_USERNAME = "superadmin";
    private final String ADMIN_USERNAME = "admin";
    private final String USER_USERNAME = "user";
    private final String PASSWORD = "password";

    private final String eventDTO = "{ \"start\": \"2023-06-06T12:00\", \"end\": \"2023-06-06T12:00\"," +
            " \"maximumCapacity\": 10, \"price\": 10, \"title\": \"title\", \"description\": \"description\"," +
            " \"locationId\": 2, \"recurrenceGroupId\": null}";

    private final String updateEventRequest = "{ \"title\": \"title\"}";

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        superAdmin = new AppUser("fstname","lastname", SUPER_ADMIN_USERNAME, PASSWORD, UserRoleEnum.SUPER_ADMIN);
        admin = new AppUser("fstname","lastname", ADMIN_USERNAME, PASSWORD, UserRoleEnum.ADMIN);
        user = new AppUser("fstname","lastname", USER_USERNAME, PASSWORD, UserRoleEnum.USER);

        Mockito.when(userDetailsService.loadUserByUsername(SUPER_ADMIN_USERNAME)).thenReturn(superAdmin);
        Mockito.when(userDetailsService.loadUserByUsername(ADMIN_USERNAME)).thenReturn(admin);
        Mockito.when(userDetailsService.loadUserByUsername(USER_USERNAME)).thenReturn(user);
    }

    // GET /api/v1/events

    @Test
    public void testGetEventsAny() throws Exception {
        mockMvc.perform(get("/api/v1/events").servletPath("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // POST /api/v1/events
    @Test
    @WithMockUser(username = SUPER_ADMIN_USERNAME, roles = {"SUPER_ADMIN"})
    public void testCreateEventSuperAdmin() throws Exception {
        String token = jwtUtil.generateToken(superAdmin, false);
        mockMvc.perform(post("/api/v1/events").servletPath("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON).content(eventDTO).header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testCreateEventRoleAdmin() throws Exception {
        String token = jwtUtil.generateToken(admin, false);
        mockMvc.perform(post("/api/v1/events").servletPath("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON).content(eventDTO).header("Authorization", "Bearer " + token))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testCreateEventRoleUser() throws Exception {
        String token = jwtUtil.generateToken(user, false);
        mockMvc.perform(post("/api/v1/events").servletPath("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON).content(eventDTO).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    // PUT /api/v1/events/recurrent
    @Test
    @WithMockUser(username = SUPER_ADMIN_USERNAME, roles = {"SUPER_ADMIN"})
    public void testUpdateRecurrentEventSuperAdmin() throws Exception {
        String token = jwtUtil.generateToken(superAdmin, false);
        mockMvc.perform(put("/api/v1/events/recurrent/1000").servletPath("/api/v1/events/recurrent/1000")
                        .contentType(MediaType.APPLICATION_JSON).content(updateEventRequest).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testUpdateRecurrentEventRoleAdmin() throws Exception {
        String token = jwtUtil.generateToken(admin, false);
        mockMvc.perform(put("/api/v1/events/recurrent/1000").servletPath("/api/v1/events/recurrent/1000")
                        .contentType(MediaType.APPLICATION_JSON).content(updateEventRequest).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testUpdateRecurrentEventRoleUser() throws Exception {
        String token = jwtUtil.generateToken(user, false);
        mockMvc.perform(put("/api/v1/events/recurrent/1000").servletPath("/api/v1/events/recurrent/1000")
                        .contentType(MediaType.APPLICATION_JSON).content(updateEventRequest).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    // PUT /api/v1/events/
    @Test
    @WithMockUser(username = SUPER_ADMIN_USERNAME, roles = {"SUPER_ADMIN"})
    public void testUpdateEventSuperAdmin() throws Exception {
        String token = jwtUtil.generateToken(superAdmin, false);
        mockMvc.perform(put("/api/v1/events/1000").servletPath("/api/v1/events/1000")
                        .contentType(MediaType.APPLICATION_JSON).content(updateEventRequest).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testUpdateEventRoleAdmin() throws Exception {
        String token = jwtUtil.generateToken(admin, false);
        mockMvc.perform(put("/api/v1/events/1000").servletPath("/api/v1/events/1000")
                        .contentType(MediaType.APPLICATION_JSON).content(updateEventRequest).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testUpdateEventRoleUser() throws Exception {
        String token = jwtUtil.generateToken(user, false);
        mockMvc.perform(put("/api/v1/events/1000").servletPath("/api/v1/events/1000")
                        .contentType(MediaType.APPLICATION_JSON).content(updateEventRequest).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    // DELETE /api/v1/events
    @Test
    @WithMockUser(username = SUPER_ADMIN_USERNAME, roles = {"SUPER_ADMIN"})
    public void testDeleteEventSuperAdmin() throws Exception {
        String token = jwtUtil.generateToken(superAdmin, false);
        mockMvc.perform(delete("/api/v1/events/1000").servletPath("/api/v1/events/1000")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testDeleteEventRoleAdmin() throws Exception {
        String token = jwtUtil.generateToken(admin, false);
        mockMvc.perform(delete("/api/v1/events/1000").servletPath("/api/v1/events/1000")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testDeleteEventRoleUser() throws Exception {
        String token = jwtUtil.generateToken(user, false);
        mockMvc.perform(delete("/api/v1/events/1000").servletPath("/api/v1/events/1000")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    // DELETE /api/v1/recurrent/events
    @Test
    @WithMockUser(username = SUPER_ADMIN_USERNAME, roles = {"SUPER_ADMIN"})
    public void testDeleteRecurrentEventSuperAdmin() throws Exception {
        String token = jwtUtil.generateToken(superAdmin, false);
        mockMvc.perform(delete("/api/v1/events/recurrent/1000").servletPath("/api/v1/events/recurrent/1000")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testDeleteRecurrentEventRoleAdmin() throws Exception {
        String token = jwtUtil.generateToken(admin, false);
        mockMvc.perform(delete("/api/v1/events/recurrent/1000").servletPath("/api/v1/events/recurrent/1000")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testDeleteRecurrentEventRoleUser() throws Exception {
        String token = jwtUtil.generateToken(user, false);
        mockMvc.perform(delete("/api/v1/events/recurrent/1000").servletPath("/api/v1/events/recurrent/1000")
                        .contentType(MediaType.APPLICATION_JSON).header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
