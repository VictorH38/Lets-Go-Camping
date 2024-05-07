package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.domain.Activity;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.usc.csci310.project.domain.Amenity;
import edu.usc.csci310.project.domain.Park;
import edu.usc.csci310.project.dto.FavoriteParkDto;
import edu.usc.csci310.project.dto.SearchParkDto;
import edu.usc.csci310.project.service.ParkService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ParkController.class)
public class ParkControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ParkService parkService;

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void searchParks_ReturnsParks() throws Exception {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("username");

        String searchName = "Yosemite";
        List<SearchParkDto> mockParks = Arrays.asList(new SearchParkDto());
        given(parkService.searchParks(List.of(searchName), "a@a.com")).willReturn(mockParks);

        mockMvc.perform(get("/api/parks/search")
                        .param("searchName", searchName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        SecurityContextHolder.clearContext();
    }

    @Test
    public void getPark_ReturnsPark() throws Exception {
        Long parkId = 1L;
        Park mockPark = new Park();

        given(parkService.getParkById(parkId)).willReturn(mockPark);

        mockMvc.perform(get("/api/parks/{parkId}", parkId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    public void getPark_WhenParkIsNull_ReturnsInternalServerError() throws Exception {
        Long parkId = 1L;

        given(parkService.getParkById(parkId)).willReturn(null);

        mockMvc.perform(get("/api/parks/{parkId}", parkId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class, result.getResolvedException()))
                .andExpect(result -> assertTrue(Objects.requireNonNull(result.getResolvedException()).getMessage().contains("Park data not found")));
    }

    @Test
    public void createFavoritePark_ShouldInvokeService() throws Exception {
        CreateFavoriteParkRequest request = new CreateFavoriteParkRequest(1L, 1L);
        String jsonRequest = "{\"user_id\":1,\"park_id\":1}";

        mockMvc.perform(post("/api/parks/favorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        verify(parkService, times(1)).createFavoritePark(1L, 1L);
    }

    @Test
    public void testRemoveFavoritePark() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        CreateFavoriteParkRequest req = new CreateFavoriteParkRequest();
        req.setUserId(1L);
        req.setParkId(2L);

        String jsonRequest = objectMapper.writeValueAsString(req);

        mockMvc.perform(post("/api/parks/unfavorite")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        verify(parkService).removeFavoritePark(req.getUserId(), req.getParkId());
    }

    @Test
    public void testGetFavoriteParks() throws Exception {
        FavoriteParkDto park1 = new FavoriteParkDto();
        FavoriteParkDto park2 = new FavoriteParkDto();
        List<FavoriteParkDto> favoriteParks = Arrays.asList(park1, park2);

        when(parkService.getFavoriteParks(anyLong())).thenReturn(favoriteParks);
        Long userId = 1L;

        mockMvc.perform(get("/api/parks/favorite/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.parks", hasSize(2)));

        verify(parkService).getFavoriteParks(userId);
    }

    @Test
    void testGetActivities() throws Exception {
        Activity activity1 = new Activity("Hiking");
        Activity activity2 = new Activity("Cycling");
        when(parkService.getAllActivities()).thenReturn(Arrays.asList(activity1, activity2));

        mockMvc.perform(get("/api/parks/activities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Hiking"))
                .andExpect(jsonPath("$.data[1].name").value("Cycling"));

        verify(parkService).getAllActivities();
    }

    @Test
    void testGetStates() throws Exception {
        Park park1 = new Park();
        park1.setStates("CA");
        Park park2 = new Park();
        park2.setStates("NV");
        Map<String, List<Park>> groupedParks = new HashMap<>();
        groupedParks.put("CA", Arrays.asList(park1));
        groupedParks.put("NV", Arrays.asList(park2));

        when(parkService.getParksGroupedByState()).thenReturn(groupedParks);

        mockMvc.perform(get("/api/parks/states"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..state").value(containsInAnyOrder("CA", "NV")));

        verify(parkService).getParksGroupedByState();
    }

    @Test
    void updateParkRanks_Success() throws Exception {
        doNothing().when(parkService).updateFavoriteParkRanks(anyLong(), anyList());

        mockMvc.perform(post("/api/parks/favorite/ranks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"user_id\":1,\"park_ids\":[1,2,3]}"))
                .andExpect(status().isOk());

        verify(parkService).updateFavoriteParkRanks(1L, Arrays.asList(1L, 2L, 3L));
    }

    @Test
    void testGetAmenities() throws Exception {
        Amenity amenity1 = new Amenity("Picnic Area");
        Amenity amenity2 = new Amenity("Hiking Trail");
        List<Amenity> mockAmenities = Arrays.asList(amenity1, amenity2);

        when(parkService.getAmenities()).thenReturn(mockAmenities);

        mockMvc.perform(get("/api/parks/amenities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Picnic Area"))
                .andExpect(jsonPath("$.data[1].name").value("Hiking Trail"));

        verify(parkService).getAmenities();
    }

}
