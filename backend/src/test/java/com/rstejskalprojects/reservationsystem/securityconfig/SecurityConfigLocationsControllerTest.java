package com.rstejskalprojects.reservationsystem.securityconfig;

import com.rstejskalprojects.reservationsystem.model.AppUser;
import com.rstejskalprojects.reservationsystem.model.Location;
import com.rstejskalprojects.reservationsystem.model.UserRoleEnum;
import com.rstejskalprojects.reservationsystem.security.filter.JwtFilter;
import com.rstejskalprojects.reservationsystem.service.LocationService;
import com.rstejskalprojects.reservationsystem.service.impl.UserDetailsServiceImpl;
import com.rstejskalprojects.reservationsystem.util.JwtUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigLocationsControllerTest {
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
    private LocationService locationService;

    UserDetails superAdmin;
    UserDetails admin;
    UserDetails user;

    private final String SUPER_ADMIN_USERNAME = "superadmin";
    private final String ADMIN_USERNAME = "admin";
    private final String USER_USERNAME = "user";
    private final String PASSWORD = "password";

    private final String location = "{\"name\": \"new location\", \"opensAt\": \"12:00\", \"closesAt\": \"16:00\"}";

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .addFilter(jwtFilter)
                .build();
        superAdmin = new AppUser(1L,"fstname","lastname", SUPER_ADMIN_USERNAME, PASSWORD, UserRoleEnum.SUPER_ADMIN, false, true);
        admin = new AppUser(2L, "fstname","lastname", ADMIN_USERNAME, PASSWORD, UserRoleEnum.ADMIN, false, true);
        user = new AppUser(3L,"fstname","lastname", USER_USERNAME, PASSWORD, UserRoleEnum.USER, false, true);
        Mockito.doNothing().when(locationService).deleteLocationById(1L);
        Mockito.when(locationService.saveLocation(Mockito.any(Location.class))).thenReturn(new Location());
        Mockito.when(userDetailsService.loadUserByUsername(SUPER_ADMIN_USERNAME)).thenReturn(superAdmin);
        Mockito.when(userDetailsService.loadUserByUsername(ADMIN_USERNAME)).thenReturn(admin);
        Mockito.when(userDetailsService.loadUserByUsername(USER_USERNAME)).thenReturn(user);
    }

    // GET /api/v1/locations

    @Test
    public void testGetEventsAny() throws Exception {
        mockMvc.perform(get("/api/v1/locations").servletPath("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // POST /api/v1/locations

    @Test
    @WithMockUser(username = SUPER_ADMIN_USERNAME, roles = {"SUPER_ADMIN"})
    public void testCreateLocationRoleSuperAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/locations").servletPath("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(location)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(superAdmin, false)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testCreateLocationRoleAdmin() throws Exception {
        mockMvc.perform(post("/api/v1/locations").servletPath("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(location)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(admin, false)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testCreateLocationRoleUser() throws Exception {
        mockMvc.perform(post("/api/v1/locations").servletPath("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(location)
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(user, false)))
                .andExpect(status().isForbidden());
    }

    // DELETE /api/v1/locations

    @Test
    @WithMockUser(username = SUPER_ADMIN_USERNAME, roles = {"SUPER_ADMIN"})
    public void testDeleteLocationRoleSuperAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/locations/1").servletPath("/api/v1/locations/1")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(superAdmin, false)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = ADMIN_USERNAME, roles = {"ADMIN"})
    public void testDeleteLocationRoleAdmin() throws Exception {
        mockMvc.perform(delete("/api/v1/locations/1").servletPath("/api/v1/locations/1")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(admin, false)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = USER_USERNAME, roles = {"USER"})
    public void testDeleteLocationRoleUser() throws Exception {
        mockMvc.perform(delete("/api/v1/locations/1").servletPath("/api/v1/locations/1")
                        .header("Authorization", "Bearer " + jwtUtil.generateToken(user, false)))
                .andExpect(status().isForbidden());
    }
}
