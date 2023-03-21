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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigReservationsControllerTest {

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

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        superAdmin = new AppUser(10L, "fstname","lastname", SUPER_ADMIN_USERNAME, PASSWORD, UserRoleEnum.SUPER_ADMIN);
        admin = new AppUser(11L, "fstname","lastname", ADMIN_USERNAME, PASSWORD, UserRoleEnum.ADMIN);
        user = new AppUser(12L, "fstname","lastname", USER_USERNAME, PASSWORD, UserRoleEnum.USER);

        Mockito.when(userDetailsService.loadUserByUsername(SUPER_ADMIN_USERNAME)).thenReturn(superAdmin);
        Mockito.when(userDetailsService.loadUserByUsername(ADMIN_USERNAME)).thenReturn(admin);
        Mockito.when(userDetailsService.loadUserByUsername(USER_USERNAME)).thenReturn(user);
    }

    // GET /api/v1/reservations

    @Test
    @WithMockUser(username = SUPER_ADMIN_USERNAME, roles = {"SUPER_ADMIN"})
    public void testGetReservationsRoleSuperAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/reservations").servletPath("/api/v1/reservations").header("Authorization", "Bearer " + jwtUtil.generateToken(superAdmin, false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testGetReservationsRoleAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/reservations").servletPath("/api/v1/reservations").header("Authorization", "Bearer " + jwtUtil.generateToken(admin, false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testGetReservationsRoleUser() throws Exception {
        mockMvc.perform(get("/api/v1/reservations").servletPath("/api/v1/reservations").header("Authorization", "Bearer " + jwtUtil.generateToken(user, false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // POST /api/v1/reservations
    @Test
    @WithMockUser(username = SUPER_ADMIN_USERNAME, roles = {"SUPER_ADMIN"})
    public void testCreateReservationRoleSuperAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/reservations").servletPath("/api/v1/reservations").header("Authorization", "Bearer " + jwtUtil.generateToken(superAdmin, false))
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testCreateReservationRoleAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/reservations").servletPath("/api/v1/reservations").header("Authorization", "Bearer " + jwtUtil.generateToken(admin, false))
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testCreateReservationRoleUser() throws Exception {
        mockMvc.perform(post("/api/v1/reservations").servletPath("/api/v1/reservations").header("Authorization", "Bearer " + jwtUtil.generateToken(user, false))
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    // GET /api/v1/reservations/{userId}

    @Test
    @WithMockUser(username = SUPER_ADMIN_USERNAME, roles = {"SUPER_ADMIN"})
    public void testGetReservationsByUserIdRoleSuperAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/3").servletPath("/api/v1/reservations/3").header("Authorization", "Bearer " + jwtUtil.generateToken(superAdmin, false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testGetReservationsByUserIdRoleAdmin() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/3").servletPath("/api/v1/reservations/3").header("Authorization", "Bearer " + jwtUtil.generateToken(admin, false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testGetReservationsByUserIdRoleUser() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/2").servletPath("/api/v1/reservations/2").header("Authorization", "Bearer " + jwtUtil.generateToken(user, false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testGetReservationsByOtherUserIdRoleUser() throws Exception {
        mockMvc.perform(get("/api/v1/reservations/6").servletPath("/api/v1/reservations/6").header("Authorization", "Bearer " + jwtUtil.generateToken(user, false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
