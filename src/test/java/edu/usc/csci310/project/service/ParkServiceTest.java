package edu.usc.csci310.project.service;

import edu.usc.csci310.project.domain.*;
import edu.usc.csci310.project.dto.ActivityDto;
import edu.usc.csci310.project.dto.AmenityDto;
import edu.usc.csci310.project.dto.FavoriteParkDto;
import edu.usc.csci310.project.dto.SearchParkDto;
import edu.usc.csci310.project.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParkServiceTest {

    @Mock
    private ParkRepository parkRepository;

    @Mock
    private FavoriteParkRepository favoriteParkRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private AmenityRepository amenityRepository;

    @Mock
    private ParkImageRepository parkImageRepository;

    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private ResourceLoader resourceLoader;

    @InjectMocks
    private ParkService parkService;

    private Park park1;
    private Park park2;

    private final String dummyJsonData = """
            {
              "data": [
                {
                  "name": "Test Park",
                  "fullName": "Test Park Full Name",
                  "description": "A beautiful park for testing",
                  "weatherInfo": "Sunny with occasional rain",
                  "designation": "National Park",
                  "directionsInfo": "Take the highway and exit at Test Exit",
                  "directionsUrl": "http://example.com/directions",
                  "states": "TestState",
                  "latitude": "40.7128",
                  "longitude": "-74.0060",
                  "addresses": [
                    {
                      "type": "Physical",
                      "line1": "123 Test Lane",
                      "city": "Test City",
                      "stateCode": "TS",
                      "postalCode": "12345"
                    }
                  ],
                  "activities": [
                    {
                      "name": "Hiking"
                    },
                    {
                      "name": "Bird Watching"
                    }
                  ],
                  "entranceFees": [
                    {
                      "cost": "10.00",
                      "description": "Per vehicle fee",
                      "title": "Standard Entry"
                    }
                  ],
                  "operatingHours": [
                    {
                      "description": "Open daily from sunrise to sunset",
                      "standardHours": {
                        "monday": "All Day",
                        "tuesday": "All Day",
                        "wednesday": "All Day",
                        "thursday": "All Day",
                        "friday": "All Day",
                        "saturday": "All Day",
                        "sunday": "All Day"
                      },
                      "name": "Regular Hours"
                    }
                  ],
                  "images": [
                    {
                      "url": "http://example.com/image1.jpg",
                      "title": "Test Image 1",
                      "caption": "An example image for testing",
                      "altText": "A descriptive alternative text"
                    }
                  ]
                }
              ]
            }""";

    private final String dummyAmenityJsonData = """
            {
              "data": [
                {
                  "name": "ATM/Cash Machine"
                },
                {
                  "name": "Accessible Rooms"
                },
                {
                  "name": "Benches/Seating"
                },
                {
                  "name": "Bicycle - Rack"
                },
                {
                  "name": "Amphitheater"
                },
                {
                  "name": "Animal-Safe Food Storage"
                },
                {
                  "name": "Braille"
                },
                {
                  "name": "Assistive Listening Systems"
                },
                {
                  "name": "Electric Car Charging Station"
                },
                {
                  "name": "Audio Description"
                },
                {
                  "name": "Electrical Outlet/Cell Phone Charging"
                },
                {
                  "name": "Automated Entrance"
                },
                {
                  "name": "First Aid Kit Available"
                },
                {
                  "name": "Baby Changing Station"
                },
                {
                  "name": "Food/Drink - Snacks"
                }
              ]
            }""";

    @BeforeEach
    void setUp() {
        park1 = new Park();
        park1.setId(1L);
        park1.setFullName("Yosemite National Park");

        park2 = new Park();
        park2.setId(2L);
        park2.setFullName("Yellowstone National Park");
    }

    @Test
    public void testImportParks() throws IOException {
        Resource mockResource = mock(Resource.class);
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream(dummyJsonData.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:parks.json")).thenReturn(mockResource);
        when(mockResource.getInputStream()).thenReturn(mockInputStream);

        Resource mockAmenityResource = mock(Resource.class);
        ByteArrayInputStream mockAmenityInputStream = new ByteArrayInputStream(dummyAmenityJsonData.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:amenities.json")).thenReturn(mockAmenityResource);
        when(mockAmenityResource.getInputStream()).thenReturn(mockAmenityInputStream);

        when(activityRepository.findAll()).thenReturn(List.of(new Activity("Hiking"), new Activity("Bird Watching")));

        parkService.importParks();

        verify(parkRepository, atLeastOnce()).save(any(Park.class));
    }

    @Test
    public void testRun_WithEmptyRepository() throws Exception {
        when(parkRepository.count()).thenReturn(0L);

        Resource mockResource = mock(Resource.class);
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream(dummyJsonData.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:parks.json")).thenReturn(mockResource);
        when(mockResource.getInputStream()).thenReturn(mockInputStream);

        Resource mockAmenityResource = mock(Resource.class);
        ByteArrayInputStream mockAmenityInputStream = new ByteArrayInputStream(dummyAmenityJsonData.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:amenities.json")).thenReturn(mockAmenityResource);
        when(mockAmenityResource.getInputStream()).thenReturn(mockAmenityInputStream);

        when(activityRepository.findAll()).thenReturn(List.of(new Activity("Hiking"), new Activity("Bird Watching")));

        doAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            callback.doInTransaction(null);
            return null;
        }).when(transactionTemplate).execute(any(TransactionCallback.class));

        parkService.run();

        verify(parkRepository, atLeastOnce()).save(any(Park.class));
    }

    @Test
    public void testRun_WithNonEmptyRepository() throws Exception {
        when(parkRepository.count()).thenReturn(1L);

        parkService.run();

        verify(parkRepository, never()).save(any(Park.class));
    }

    @Test
    void testImportParksWhenAmenityArrayIsNotAnArray() throws IOException {
        String emptyAmenityJsonData = "{\"data\": \"\"}";

        Resource mockResource = mock(Resource.class);
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream(dummyJsonData.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:parks.json")).thenReturn(mockResource);
        when(mockResource.getInputStream()).thenReturn(mockInputStream);

        Resource mockEmptyAmenityResource = mock(Resource.class);
        ByteArrayInputStream mockEmptyAmenityInputStream = new ByteArrayInputStream(emptyAmenityJsonData.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:amenities.json")).thenReturn(mockEmptyAmenityResource);
        when(mockEmptyAmenityResource.getInputStream()).thenReturn(mockEmptyAmenityInputStream);

        when(activityRepository.findAll()).thenReturn(List.of(new Activity("Hiking"), new Activity("Bird Watching")));

        parkService.importParks();

        verify(parkRepository, atLeastOnce()).save(any(Park.class));
    }

    @Test
    public void testImportParks_NumberFormatException() throws IOException {
        Resource mockResource = mock(Resource.class);
        String dummyJsonDataWithInvalidCost = """
                {
                  "data": [
                    {
                      "name": "Test Park",
                      "fullName": "Test Park Full Name",
                      "description": "A beautiful park for testing",
                      "weatherInfo": "Sunny with occasional rain",
                      "designation": "National Park",
                      "directionsInfo": "Take the highway and exit at Test Exit",
                      "directionsUrl": "http://example.com/directions",
                      "states": "TestState",
                      "latitude": "40.7128",
                      "longitude": "-74.0060",
                      "addresses": [
                        {
                          "type": "Physical",
                          "line1": "123 Test Lane",
                          "city": "Test City",
                          "stateCode": "TS",
                          "postalCode": "12345"
                        }
                      ],
                      "activities": [
                        {
                          "name": "Hiking"
                        },
                        {
                          "name": "Bird Watching"
                        }
                      ],
                      "entranceFees": [
                        {
                          "cost": "INVALID COST",
                          "description": "Per vehicle fee",
                          "title": "Standard Entry"
                        }
                      ],
                      "operatingHours": [
                        {
                          "description": "Open daily from sunrise to sunset",
                          "standardHours": {
                            "monday": "All Day",
                            "tuesday": "All Day",
                            "wednesday": "All Day",
                            "thursday": "All Day",
                            "friday": "All Day",
                            "saturday": "All Day",
                            "sunday": "All Day"
                          },
                          "name": "Regular Hours"
                        }
                      ],
                      "images": [
                        {
                          "url": "http://example.com/image1.jpg",
                          "title": "Test Image 1",
                          "caption": "An example image for testing",
                          "altText": "A descriptive alternative text"
                        }
                      ]
                    }
                  ]
                }""";
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream(dummyJsonDataWithInvalidCost.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:parks.json")).thenReturn(mockResource);
        when(mockResource.getInputStream()).thenReturn(mockInputStream);

        Resource mockAmenityResource = mock(Resource.class);
        ByteArrayInputStream mockAmenityInputStream = new ByteArrayInputStream(dummyAmenityJsonData.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:amenities.json")).thenReturn(mockAmenityResource);
        when(mockAmenityResource.getInputStream()).thenReturn(mockAmenityInputStream);

        parkService.importParks();

        verify(parkRepository, atLeastOnce()).save(any(Park.class));
    }

    @Test
    public void testExecuteImportWithTransaction_IOException() throws Exception {
        when(parkRepository.count()).thenReturn(0L);

        Resource mockResource = mock(Resource.class);
        when(resourceLoader.getResource("classpath:parks.json")).thenReturn(mockResource);
        try {
            when(mockResource.getInputStream()).thenThrow(new IOException("Simulated IOException"));
        } catch (IOException e) {
            //
        }

        doAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            assertThrows(RuntimeException.class, () -> callback.doInTransaction(null), "Expected RuntimeException due to IOException");
            return null;
        }).when(transactionTemplate).execute(any(TransactionCallback.class));

        parkService.run();

        verify(transactionTemplate).execute(any(TransactionCallback.class));
    }

    @Test
    public void testImportParks_EmptyData() throws IOException {
        String emptyDataJson = """
        {
          "data": []
        }""";

        Resource mockResource = mock(Resource.class);
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream(emptyDataJson.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:parks.json")).thenReturn(mockResource);
        when(mockResource.getInputStream()).thenReturn(mockInputStream);

        Resource mockAmenityResource = mock(Resource.class);
        ByteArrayInputStream mockAmenityInputStream = new ByteArrayInputStream(dummyAmenityJsonData.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:amenities.json")).thenReturn(mockAmenityResource);
        when(mockAmenityResource.getInputStream()).thenReturn(mockAmenityInputStream);

        parkService.importParks();

        verify(parkRepository, never()).save(any(Park.class));
    }

    @Test
    public void testImportParks_MissingFields() throws IOException {
        String emptyFieldsJson = """
        {
          "data": [
                {
                  "name": "Test Park",
                  "fullName": "Test Park Full Name",
                  "description": "A beautiful park for testing",
                  "weatherInfo": "Sunny with occasional rain",
                  "designation": "National Park",
                  "directionsInfo": "Take the highway and exit at Test Exit",
                  "directionsUrl": "http://example.com/directions",
                  "states": "TestState",
                  "latitude": "40.7128",
                  "longitude": "-74.0060"
                }
              ]
        }""";

        Resource mockResource = mock(Resource.class);
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream(emptyFieldsJson.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:parks.json")).thenReturn(mockResource);
        when(mockResource.getInputStream()).thenReturn(mockInputStream);

        Resource mockAmenityResource = mock(Resource.class);
        ByteArrayInputStream mockAmenityInputStream = new ByteArrayInputStream(dummyAmenityJsonData.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:amenities.json")).thenReturn(mockAmenityResource);
        when(mockAmenityResource.getInputStream()).thenReturn(mockAmenityInputStream);

        parkService.importParks();

        verify(parkRepository, atLeastOnce()).save(any(Park.class));
    }

    @Test
    public void testImportParks_DataNotArray() throws IOException {
        String dataNotArrayJson = """
        {
          "data": "This is not an array"
        }""";

        Resource mockResource = mock(Resource.class);
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream(dataNotArrayJson.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:parks.json")).thenReturn(mockResource);
        when(mockResource.getInputStream()).thenReturn(mockInputStream);

        Resource mockAmenityResource = mock(Resource.class);
        ByteArrayInputStream mockAmenityInputStream = new ByteArrayInputStream(dummyAmenityJsonData.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:amenities.json")).thenReturn(mockAmenityResource);
        when(mockAmenityResource.getInputStream()).thenReturn(mockAmenityInputStream);

        parkService.importParks();

        verify(parkRepository, never()).save(any(Park.class));
    }

    @Test
    public void testImportParks_NotArray() throws IOException {
        String dummyJsonDataWithNoArray = """
                {
                  "data": [
                    {
                      "name": "Test Park",
                      "fullName": "Test Park Full Name",
                      "description": "A beautiful park for testing",
                      "weatherInfo": "Sunny with occasional rain",
                      "designation": "National Park",
                      "directionsInfo": "Take the highway and exit at Test Exit",
                      "directionsUrl": "http://example.com/directions",
                      "states": "TestState",
                      "latitude": "40.7128",
                      "longitude": "-74.0060",
                      "addresses": "Not an array",
                      "activities": "Not an array",
                      "entranceFees": "Not an array",
                      "operatingHours": "Not an array",
                      "images": "Not an array"
                    }
                  ]
                }""";

        Resource mockResource = mock(Resource.class);
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream(dummyJsonDataWithNoArray.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:parks.json")).thenReturn(mockResource);
        when(mockResource.getInputStream()).thenReturn(mockInputStream);

        Resource mockAmenityResource = mock(Resource.class);
        ByteArrayInputStream mockAmenityInputStream = new ByteArrayInputStream(dummyAmenityJsonData.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:amenities.json")).thenReturn(mockAmenityResource);
        when(mockAmenityResource.getInputStream()).thenReturn(mockAmenityInputStream);

        parkService.importParks();

        verify(parkRepository, atLeastOnce()).save(any(Park.class));
    }

    @Test
    public void testImportParks_NoPhysicalAddress() throws IOException {
        String dummyJsonDataWithNoPhysicalAddress = """
                {
                  "data": [
                    {
                      "name": "Test Park",
                      "fullName": "Test Park Full Name",
                      "description": "A beautiful park for testing",
                      "weatherInfo": "Sunny with occasional rain",
                      "designation": "National Park",
                      "directionsInfo": "Take the highway and exit at Test Exit",
                      "directionsUrl": "http://example.com/directions",
                      "states": "TestState",
                      "latitude": "40.7128",
                      "longitude": "-74.0060",
                      "addresses": [
                        {
                          "type": "Mailing",
                          "line1": "123 Test Lane",
                          "city": "Test City",
                          "stateCode": "TS",
                          "postalCode": "12345"
                        }
                      ],
                      "activities": [
                        {
                          "name": "Hiking"
                        },
                        {
                          "name": "Bird Watching"
                        }
                      ],
                      "entranceFees": [
                        {
                          "cost": "INVALID COST",
                          "description": "Per vehicle fee",
                          "title": "Standard Entry"
                        }
                      ],
                      "operatingHours": [
                        {
                          "description": "Open daily from sunrise to sunset",
                          "standardHours": {
                            "monday": "All Day",
                            "tuesday": "All Day",
                            "wednesday": "All Day",
                            "thursday": "All Day",
                            "friday": "All Day",
                            "saturday": "All Day",
                            "sunday": "All Day"
                          },
                          "name": "Regular Hours"
                        }
                      ],
                      "images": [
                        {
                          "url": "http://example.com/image1.jpg",
                          "title": "Test Image 1",
                          "caption": "An example image for testing",
                          "altText": "A descriptive alternative text"
                        }
                      ]
                    }
                  ]
                }""";

        Resource mockResource = mock(Resource.class);
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream(dummyJsonDataWithNoPhysicalAddress.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:parks.json")).thenReturn(mockResource);
        when(mockResource.getInputStream()).thenReturn(mockInputStream);

        Resource mockAmenityResource = mock(Resource.class);
        ByteArrayInputStream mockAmenityInputStream = new ByteArrayInputStream(dummyAmenityJsonData.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:amenities.json")).thenReturn(mockAmenityResource);
        when(mockAmenityResource.getInputStream()).thenReturn(mockAmenityInputStream);

        parkService.importParks();

        verify(parkRepository, atLeastOnce()).save(any(Park.class));
    }

    @Test
    public void testImportParks_EmptyCodes() throws IOException {
        String dummyJsonDataWithEmptyCodes = """
                {
                  "data": [
                    {
                      "name": "Test Park",
                      "fullName": "Test Park Full Name",
                      "description": "A beautiful park for testing",
                      "weatherInfo": "Sunny with occasional rain",
                      "designation": "National Park",
                      "directionsInfo": "Take the highway and exit at Test Exit",
                      "directionsUrl": "http://example.com/directions",
                      "states": "TestState",
                      "latitude": "40.7128",
                      "longitude": "-74.0060",
                      "addresses": [
                        {
                          "type": "Physical",
                          "line1": "123 Test Lane",
                          "city": "Test City",
                          "stateCode": "",
                          "postalCode": ""
                        }
                      ],
                      "activities": [
                        {
                          "name": "Hiking"
                        },
                        {
                          "name": "Bird Watching"
                        }
                      ],
                      "entranceFees": [
                        {
                          "cost": "INVALID COST",
                          "description": "Per vehicle fee",
                          "title": "Standard Entry"
                        }
                      ],
                      "operatingHours": [
                        {
                          "description": "Open daily from sunrise to sunset",
                          "standardHours": {
                            "monday": "All Day",
                            "tuesday": "All Day",
                            "wednesday": "All Day",
                            "thursday": "All Day",
                            "friday": "All Day",
                            "saturday": "All Day",
                            "sunday": "All Day"
                          },
                          "name": "Regular Hours"
                        }
                      ],
                      "images": [
                        {
                          "url": "http://example.com/image1.jpg",
                          "title": "Test Image 1",
                          "caption": "An example image for testing",
                          "altText": "A descriptive alternative text"
                        }
                      ]
                    }
                  ]
                }""";

        Resource mockResource = mock(Resource.class);
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream(dummyJsonDataWithEmptyCodes.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:parks.json")).thenReturn(mockResource);
        when(mockResource.getInputStream()).thenReturn(mockInputStream);

        Resource mockAmenityResource = mock(Resource.class);
        ByteArrayInputStream mockAmenityInputStream = new ByteArrayInputStream(dummyAmenityJsonData.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:amenities.json")).thenReturn(mockAmenityResource);
        when(mockAmenityResource.getInputStream()).thenReturn(mockAmenityInputStream);

        parkService.importParks();

        verify(parkRepository, atLeastOnce()).save(any(Park.class));
    }

    @Test
    public void testImportParks_NoStandardHours() throws IOException {
        String dummyJsonDataWithNoStandardHours = """
                {
                  "data": [
                    {
                      "name": "Test Park",
                      "fullName": "Test Park Full Name",
                      "description": "A beautiful park for testing",
                      "weatherInfo": "Sunny with occasional rain",
                      "designation": "National Park",
                      "directionsInfo": "Take the highway and exit at Test Exit",
                      "directionsUrl": "http://example.com/directions",
                      "states": "TestState",
                      "latitude": "40.7128",
                      "longitude": "-74.0060",
                      "addresses": [
                        {
                          "type": "Physical",
                          "line1": "123 Test Lane",
                          "city": "Test City",
                          "stateCode": "TS",
                          "postalCode": "12345"
                        }
                      ],
                      "activities": [
                        {
                          "name": "Hiking"
                        },
                        {
                          "name": "Bird Watching"
                        }
                      ],
                      "entranceFees": [
                        {
                          "cost": "INVALID COST",
                          "description": "Per vehicle fee",
                          "title": "Standard Entry"
                        }
                      ],
                      "operatingHours": [
                        {
                          "description": "Open daily from sunrise to sunset",
                          "name": "Regular Hours"
                        }
                      ],
                      "images": [
                        {
                          "url": "http://example.com/image1.jpg",
                          "title": "Test Image 1",
                          "caption": "An example image for testing",
                          "altText": "A descriptive alternative text"
                        }
                      ]
                    }
                  ]
                }""";

        Resource mockResource = mock(Resource.class);
        ByteArrayInputStream mockInputStream = new ByteArrayInputStream(dummyJsonDataWithNoStandardHours.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:parks.json")).thenReturn(mockResource);
        when(mockResource.getInputStream()).thenReturn(mockInputStream);

        Resource mockAmenityResource = mock(Resource.class);
        ByteArrayInputStream mockAmenityInputStream = new ByteArrayInputStream(dummyAmenityJsonData.getBytes(StandardCharsets.UTF_8));
        when(resourceLoader.getResource("classpath:amenities.json")).thenReturn(mockAmenityResource);
        when(mockAmenityResource.getInputStream()).thenReturn(mockAmenityInputStream);

        parkService.importParks();

        verify(parkRepository, atLeastOnce()).save(any(Park.class));
    }

    @Test
    void searchParks_ReturnsMatchingParks() {
        User user = new User(1L, "name", "a@a.com", "123");

        Park park = new Park();
        park.setId(100L);

        ParkImage parkImage = new ParkImage();
        parkImage.setPark(park);

        ActivityDto activity = new ActivityDto(1L, "a1");

        AmenityDto amenity = new AmenityDto(300L, "a1");

        FavoritePark fp = new FavoritePark();
        fp.setUser(user);
        fp.setPark(park);

        when(userRepository.findByEmail("user@example.com")).thenReturn(user);
        when(parkRepository.findByNameContaining("Sunny")).thenReturn(List.of(park));
        when(favoriteParkRepository.findAllByUserIdOrderByRankAsc(1L)).thenReturn(Collections.singletonList(fp));
        when(parkImageRepository.findByParkId(100L)).thenReturn(List.of(parkImage));
        when(activityRepository.findAllActivityDto(100L)).thenReturn(List.of(activity));
        when(amenityRepository.findAllAmenities(100L)).thenReturn(List.of(amenity));

        List<String> keywords = List.of("Sunny");
        String email = "user@example.com";

        List<SearchParkDto> results = parkService.searchParks(keywords, email);

        assertNotNull(results);
        assertEquals(1, results.size());
        SearchParkDto result = results.get(0);
        assertTrue(result.getIsFavorite());
        assertEquals(List.of(parkImage), result.getParkImages());
        assertEquals(List.of(activity), result.getActivities());
        assertEquals(List.of(amenity), result.getAmenities());
    }

    @Test
    void getParkById_ReturnsCorrectPark() {
        Long parkId = 1L;
        Park expectedPark = new Park();
        expectedPark.setId(parkId);
        expectedPark.setFullName("Yosemite National Park");

        given(parkRepository.findById(parkId)).willReturn(Optional.of(expectedPark));

        Park result = parkService.getParkById(parkId);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(expectedPark));
    }

    @Test
    public void createFavoritePark_ShouldSaveFavoritePark_WhenUserAndParkExist() {
        User user = mock(User.class);
        Park park = mock(Park.class);
        Long userId = 1L;
        Long parkId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(parkRepository.findById(parkId)).thenReturn(Optional.of(park));
        when(favoriteParkRepository.findByUserIdAndParkId(userId, parkId)).thenReturn(Optional.empty());

        parkService.createFavoritePark(userId, parkId);

        verify(favoriteParkRepository, times(1)).save(any(FavoritePark.class));
    }

    @Test
    public void createFavoritePark_ShouldDoNothing_WhenUserOrParkDoesNotExist() {
        Long userId = 1L;
        Long parkId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        parkService.createFavoritePark(userId, parkId);
        verify(favoriteParkRepository, never()).save(any(FavoritePark.class));

        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(parkRepository.findById(parkId)).thenReturn(Optional.empty());
        parkService.createFavoritePark(userId, parkId);
        verify(favoriteParkRepository, never()).save(any(FavoritePark.class));
    }

    @Test
    public void createFavoritePark_ShouldDoNothing_WhenFavoriteParkAlreadyExists() {
        User user = mock(User.class);
        Park park = mock(Park.class);
        Long userId = 1L;
        Long parkId = 1L;
        FavoritePark favoritePark = mock(FavoritePark.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(parkRepository.findById(parkId)).thenReturn(Optional.of(park));
        when(favoriteParkRepository.findByUserIdAndParkId(userId, parkId)).thenReturn(Optional.of(favoritePark));

        parkService.createFavoritePark(userId, parkId);

        verify(favoriteParkRepository, never()).save(any(FavoritePark.class));
    }

    @Test
    void testCreateFavoritePark() {
        User user = new User(1L, "name1", "a@a.com", "123");
        user.setId(1L);
        Park park = new Park();
        park.setId(1L);
        Park otherPark = new Park();
        otherPark.setId(2L);

        FavoritePark existingFavoritePark = new FavoritePark();
        existingFavoritePark.setUser(user);
        existingFavoritePark.setPark(otherPark);
        existingFavoritePark.setRank(1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(parkRepository.findById(1L)).thenReturn(Optional.of(park));
        when(favoriteParkRepository.findByUserIdAndParkId(1L, 1L)).thenReturn(Optional.empty());
        when(favoriteParkRepository.findAllByUserIdOrderByRankAsc(1L)).thenReturn(List.of(existingFavoritePark));

        parkService.createFavoritePark(1L, 1L);
        verify(favoriteParkRepository, times(1)).save(any(FavoritePark.class));

        ArgumentCaptor<FavoritePark> captor = ArgumentCaptor.forClass(FavoritePark.class);
        verify(favoriteParkRepository).save(captor.capture());
        FavoritePark savedFavoritePark = captor.getValue();

        assertNotNull(savedFavoritePark);
        assertEquals(1L, savedFavoritePark.getUser().getId());
        assertEquals(1L, savedFavoritePark.getPark().getId());
        assertEquals(2, savedFavoritePark.getRank());
    }

    @Test
    public void removeFavoritePark_ShouldRemoveFavorite_WhenFavoriteExists() {
        Long userId = 1L;
        Long parkId = 1L;
        User user = mock(User.class);
        Park park = mock(Park.class);
        FavoritePark favoritePark = mock(FavoritePark.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(parkRepository.findById(parkId)).thenReturn(Optional.of(park));
        when(favoriteParkRepository.findByUserIdAndParkId(userId, parkId)).thenReturn(Optional.of(favoritePark));

        parkService.removeFavoritePark(userId, parkId);
        verify(favoriteParkRepository, times(1)).delete(favoritePark);
    }

    @Test
    public void removeFavoritePark_ShouldDoNothing_WhenUserOrParkDoesNotExist() {
        Long userId = 1L;
        Long parkId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        parkService.removeFavoritePark(userId, parkId);
        verify(favoriteParkRepository, never()).delete(any(FavoritePark.class));

        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(parkRepository.findById(parkId)).thenReturn(Optional.empty());
        parkService.removeFavoritePark(userId, parkId);
        verify(favoriteParkRepository, never()).delete(any(FavoritePark.class));
    }

    @Test
    public void removeFavoritePark_ShouldDoNothing_WhenFavoriteDoesNotExist() {
        Long userId = 1L;
        Long parkId = 1L;
        User user = mock(User.class);
        Park park = mock(Park.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(parkRepository.findById(parkId)).thenReturn(Optional.of(park));
        when(favoriteParkRepository.findByUserIdAndParkId(userId, parkId)).thenReturn(Optional.empty());

        parkService.removeFavoritePark(userId, parkId);
        verify(favoriteParkRepository, never()).delete(any(FavoritePark.class));
    }

    @Test
    void testRemoveFavoritePark() {
        User user = new User(1L,"name1", "a@a.com", "123");
        Park park = new Park();
        park.setId(1L);
        FavoritePark favoritePark = new FavoritePark();
        favoritePark.setRank(1);
        favoritePark.setPark(park1);
        favoritePark.setUser(user);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(parkRepository.findById(1L)).thenReturn(Optional.of(park));
        when(favoriteParkRepository.findByUserIdAndParkId(1L, 1L)).thenReturn(Optional.of(favoritePark));

        FavoritePark fp2 = new FavoritePark();
        fp2.setRank(2);
        fp2.setPark(park2);
        fp2.setUser(user);
        List<FavoritePark> remainingParks = List.of(fp2);
        when(favoriteParkRepository.findAllByUserIdOrderByRankAsc(1L)).thenReturn(remainingParks);

        parkService.removeFavoritePark(1L, 1L);

        verify(favoriteParkRepository).delete(favoritePark);
        verify(favoriteParkRepository, times(1)).save(any(FavoritePark.class));
    }

    @Test
    void testGetFavoriteParks() {
        User user = new User(1L,"name1", "a@a.com", "123");
        Park park1 = new Park();
        park1.setId(1L);
        Park park2 = new Park();
        park2.setId(2L);
        FavoritePark fp1 = new FavoritePark();
        fp1.setRank(1);
        fp1.setPark(park1);
        fp1.setUser(user);
        FavoritePark fp2 = new FavoritePark();
        fp2.setRank(2);
        fp2.setPark(park2);
        fp2.setUser(user);

        when(favoriteParkRepository.findAllByUserIdOrderByRankAsc(1L)).thenReturn(Arrays.asList(fp1, fp2));
        when(parkRepository.findAllById(Arrays.asList(1L, 2L))).thenReturn(Arrays.asList(park1, park2));

        List<FavoriteParkDto> dtos = parkService.getFavoriteParks(1L);

        assertEquals(2, dtos.size());
        assertEquals(1, dtos.get(0).getRank());
        assertEquals(1L, dtos.get(0).getPark().getId());
        assertEquals(2, dtos.get(1).getRank());
        assertEquals(2L, dtos.get(1).getPark().getId());
    }

    @Test
    void testGetAllActivities() {
        Activity activity1 = new Activity("Hiking");
        Activity activity2 = new Activity("Cycling");
        when(activityRepository.findAll()).thenReturn(Arrays.asList(activity1, activity2));

        List<Activity> activities = parkService.getAllActivities();

        assertEquals(2, activities.size());
        verify(activityRepository).findAll();
    }

    @Test
    void testGetParksGroupedByState() {
        Park park1 = new Park();
        park1.setStates("CA,NV");
        Park park2 = new Park();
        park2.setStates("NV,NY");
        when(parkRepository.findAll()).thenReturn(Arrays.asList(park1, park2));

        Map<String, List<Park>> groupedParks = parkService.getParksGroupedByState();

        assertEquals(3, groupedParks.size());
        assertTrue(groupedParks.containsKey("CA") && groupedParks.get("CA").contains(park1));
        assertTrue(groupedParks.containsKey("NV") && groupedParks.get("NV").containsAll(Arrays.asList(park1, park2)));
        assertTrue(groupedParks.containsKey("NY") && groupedParks.get("NY").contains(park2));
        verify(parkRepository).findAll();
    }

    @Test
    public void testUpdateFavoriteParkRanks() {
        User user = new User(1L, "name", "a@a.com", "123");
        Park park1 = new Park(); park1.setId(1L);
        Park park2 = new Park(); park2.setId(2L);
        Park park3 = new Park(); park3.setId(3L);


        FavoritePark fp1 = new FavoritePark();
        fp1.setPark(park1);
        fp1.setUser(user);
        FavoritePark fp2 = new FavoritePark();
        fp2.setPark(park2);
        fp2.setUser(user);
        FavoritePark fp3 = new FavoritePark();
        fp3.setPark(park3);
        fp3.setUser(user);

        List<FavoritePark> favoriteParks = Arrays.asList(
                fp1, fp2, fp3
        );

        List<Long> newOrder = Arrays.asList(3L, 1L, 2L);

        when(favoriteParkRepository.findAllByUserIdOrderByRankAsc(1L)).thenReturn(favoriteParks);

        parkService.updateFavoriteParkRanks(1L, newOrder);

        assertEquals(2, favoriteParks.get(0).getRank());
        assertEquals(3, favoriteParks.get(1).getRank());
        assertEquals(1, favoriteParks.get(2).getRank());

        verify(favoriteParkRepository).saveAll(favoriteParks);
    }

    @Test
    public void testUpdateFavoriteParkRanksWithIncompleteRanksOrder() {
        User user = new User(1L, "name", "a@a.com", "123");
        Park park1 = new Park(); park1.setId(1L);
        Park park2 = new Park(); park2.setId(2L);
        Park park3 = new Park(); park3.setId(3L);


        FavoritePark fp1 = new FavoritePark();
        fp1.setPark(park1);
        fp1.setUser(user);
        fp1.setRank(1);
        FavoritePark fp2 = new FavoritePark();
        fp2.setPark(park2);
        fp2.setUser(user);
        fp2.setRank(2);
        FavoritePark fp3 = new FavoritePark();
        fp3.setPark(park3);
        fp3.setUser(user);
        fp3.setRank(3);

        List<FavoritePark> favoriteParks = Arrays.asList(
                fp1, fp2, fp3
        );

        List<Long> newOrder = Arrays.asList(1L, 2L);

        when(favoriteParkRepository.findAllByUserIdOrderByRankAsc(1L)).thenReturn(favoriteParks);

        parkService.updateFavoriteParkRanks(1L, newOrder);

        assertEquals(1, favoriteParks.get(0).getRank());
        assertEquals(2, favoriteParks.get(1).getRank());
        assertEquals(3, favoriteParks.get(2).getRank());
    }

    @Test
    public void testUpdateFavoriteParkRanksWithInvalidRanksOrder() {
        User user = new User(1L, "name", "a@a.com", "123");
        Park park1 = new Park(); park1.setId(1L);
        Park park2 = new Park(); park2.setId(2L);
        Park park3 = new Park(); park3.setId(3L);


        FavoritePark fp1 = new FavoritePark();
        fp1.setPark(park1);
        fp1.setUser(user);
        fp1.setRank(1);
        FavoritePark fp2 = new FavoritePark();
        fp2.setPark(park2);
        fp2.setUser(user);
        fp2.setRank(2);
        FavoritePark fp3 = new FavoritePark();
        fp3.setPark(park3);
        fp3.setUser(user);
        fp3.setRank(3);

        List<FavoritePark> favoriteParks = Arrays.asList(
                fp1, fp2, fp3
        );

        List<Long> newOrder = Arrays.asList(2L, 1L, 4L);

        when(favoriteParkRepository.findAllByUserIdOrderByRankAsc(1L)).thenReturn(favoriteParks);

        parkService.updateFavoriteParkRanks(1L, newOrder);

        assertEquals(2, favoriteParks.get(0).getRank());
        assertEquals(1, favoriteParks.get(1).getRank());
        assertEquals(3, favoriteParks.get(2).getRank());
    }

    @Test
    void testGetAmenities() {
        Amenity amenity1 = new Amenity("Picnic Area");
        Amenity amenity2 = new Amenity("Hiking Trail");
        List<Amenity> mockAmenities = Arrays.asList(amenity1, amenity2);

        when(amenityRepository.findAll()).thenReturn(mockAmenities);

        List<Amenity> amenities = parkService.getAmenities();

        assertNotNull(amenities, "The returned list of amenities should not be null");
        assertEquals(2, amenities.size(), "The returned list should have two amenities");
        assertEquals(mockAmenities, amenities, "The returned amenities should match the mock amenities");

        verify(amenityRepository).findAll();
    }
}
