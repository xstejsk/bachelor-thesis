package com.rstejskalprojects.reservationsystem.securityconfig;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.Event;
import com.rstejskalprojects.reservationsystem.model.Location;
import com.rstejskalprojects.reservationsystem.model.Reservation;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.repository.ReservationRepository;
import com.rstejskalprojects.reservationsystem.security.filter.JwtFilter;
import com.rstejskalprojects.reservationsystem.service.ReservationService;
import com.rstejskalprojects.reservationsystem.service.impl.ReservationServiceImpl;
import com.rstejskalprojects.reservationsystem.service.impl.UserDetailsServiceImpl;
import com.rstejskalprojects.reservationsystem.util.JwtUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

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

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private JwtFilter jwtFilter;

    @MockBean
    private ReservationServiceImpl reservationService;


    UserDetails superAdmin;
    UserDetails admin;
    UserDetails user;
    UserDetails otherUser;
    Event event;

    private final String SUPER_ADMIN_USERNAME = "superadmin";
    private final String ADMIN_USERNAME = "admin";
    private final String USER_USERNAME = "randomuser";
    private final String PASSWORD = "password";

    @Before
    public void setup() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .addFilters(jwtFilter) // newly added
                .build();

        superAdmin = new AppUser(10L, "fstname","lastname", SUPER_ADMIN_USERNAME, PASSWORD, UserRoleEnum.SUPER_ADMIN);
        admin = new AppUser(11L, "fstname","lastname", ADMIN_USERNAME, PASSWORD, UserRoleEnum.ADMIN);
        user = new AppUser(12L, "fstname","lastname", USER_USERNAME, PASSWORD, UserRoleEnum.USER);
        otherUser = new AppUser(14L, "fstname","lastname", USER_USERNAME, PASSWORD, UserRoleEnum.USER);
        event = new Event(new Location(), LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusMinutes(60), 6, 6d, "");

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
        mockMvc.perform(get("/api/v1/reservations/12").servletPath("/api/v1/reservations/12").header("Authorization", "Bearer " + jwtUtil.generateToken(user, false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // DELETE /api/v1/reservations/{reservationId}

    @Test
    @WithMockUser(username = SUPER_ADMIN_USERNAME, roles = {"SUPER_ADMIN"})
    public void testDeleteReservationByUserIdRoleSuperAdmin() throws Exception {
        Mockito.when(reservationService.findById(1000L)).thenReturn(new Reservation((AppUser)user, event));
        mockMvc.perform(delete("/api/v1/reservations/1000").servletPath("/api/v1/reservations/1000").header("Authorization", "Bearer " + jwtUtil.generateToken(superAdmin, false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testDeleteReservationByUserIdRoleAdmin() throws Exception {
        Mockito.when(reservationService.findById(1000L)).thenReturn(new Reservation((AppUser)user, event));
        mockMvc.perform(delete("/api/v1/reservations/1000").servletPath("/api/v1/reservations/1000").header("Authorization", "Bearer " + jwtUtil.generateToken(admin, false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testDeleteReservationByUserIdRoleUser() throws Exception {
        Mockito.when(reservationService.findById(1000L)).thenReturn(new Reservation((AppUser)user, event));
        mockMvc.perform(delete("/api/v1/reservations/1000").servletPath("/api/v1/reservations/1000").header("Authorization", "Bearer " + jwtUtil.generateToken(user, false))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
