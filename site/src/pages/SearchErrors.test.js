import { render, screen, waitFor, act } from "@testing-library/react";
import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';
import Search from "./Search";
import React from "react";
import {getCookie} from "../utils/cookieHelper";

jest.mock('../utils/cookieHelper', () => ({
    getCookie: jest.fn(() => 'dummyToken'),
}));

beforeEach(() => {
    global.fetch = jest.fn();

    getCookie.mockImplementation((name) => {
        if (name === 'token') return 'dummyToken';
        if (name === 'user_id') return 'dummyUserId';
    });
});

const mockParks = {
    data: {
        parks: [
            {
                park: {
                    id: 193,
                    fullName: 'Grand Canyon National Park',
                    description: "Grand Canyon National Park, in Northern Arizona, encompasses 278 miles (447 km) of the Colorado River and adjacent uplands. Located on ancestral homeland of 11 Associated Tribes, Grand Canyon is one of the most spectacular examples of erosion anywhere in the world—unmatched in the incomparable vistas it offers visitors from the rims. The South Rim is open. The North Rim is CLOSED for the winter.",
                    states: "AZ",
                    latitude: "36.0001165336",
                    longitude: "-112.121516363",
                    address: "20 South Entrance Road, AZ 86023",
                    directionsInfo: "South Rim: Open all year, is located 60 miles north of Williams, Arizona (via route 64 from Interstate 40) and 80 miles northwest of Flagstaff (via route 180). Grand Canyon lies entirely within the state of Arizona. North Rim: Open for the season between May 15 and October 15, 2024. The North Rim is located 30 miles south of Jacob Lake on Highway 67; the actual rim of the canyon is an additional 14 miles south. Jacob Lake, AZ is located in northern Arizona on Highway 89A, not far from the Utah border.",
                    directionsURL: "http://www.nps.gov/grca/planyourvisit/directions.htm",
                    designation: "National Park",
                    weather: "This weather varies with cold winters and mild pleasant summers, moderate humidity, and considerable diurnal temperature changes at the higher elevations, with hot and drier summers at the bottom of the Grand Canyon along with cool damp winters. Summer thunderstorms and winter snowfall adds to the weather variety in this region.",
                    shortName: "Grand Canyon"
                },
                amenities: [
                    { id: 13, name: 'Baby Changing Station' },
                    { id: 14, name: 'Food/Drink - Snacks' },
                    { id: 15, name: 'ATM/Cash Machine' }
                ],
                activities: [
                    { id: 101, name: 'Hiking' },
                    { id: 102, name: 'Camping' },
                    { id: 103, name: 'Bird Watching' },
                    // assuming there are 32 activities, adding a few as example
                ],
                parkImages: [
                    { id: 1132, title: 'Grand Canyon Mather Point Sunset on the South Rim', caption: "People come from all over the world to view Grand Canyon's sunset", url: 'https://www.nps.gov/common/uploads/structured_data/3C7B12D1-1DD8-B71B-0BCE0712F9CEA155.jpg', altText: 'The canyon glows orange as people visit Mather Point, a rock outcropping that juts into Grand Canyon' },
                    { id: 1133, title: 'Grand Canyon National Park: View from Cape Royal on the North Rim', caption: 'A popular outdoor site for weddings and receptions… miles (37 km) from the North Rim developed area.', url: 'https://www.nps.gov/common/uploads/structured_data/3C7B143E-1DD8-B71B-0BD4A1EF96847292.jpg', altText: 'The Cape Royal viewpoint curves into the distance and closer rock formations jut into the canyon.' },
                    { id: 1134, title: 'Grand Canyon National Park: Desert View Watchtower (South Rim)', caption: 'The Watchtower is located at Desert View, the east…a on the South Rim of Grand Canyon National Park.', url: 'https://www.nps.gov/common/uploads/structured_data/3C7B15A4-1DD8-B71B-0BFADECB506765CC.jpg', altText: 'The Desert View Watchtower looms 70 feet into the air over a vast and dramatic view of the canyon.' },
                    // More images can be added here
                ],
                isFavorite: false
            }
        ]
    }
};

const mockFavorites = {
    data: {
        parks: [
            {
                park: {
                    id: 193,
                    fullName: 'Grand Canyon National Park',
                    description: "Grand Canyon National Park, in Northern Arizona, encompasses 278 miles (447 km) of the Colorado River and adjacent uplands. Located on ancestral homeland of 11 Associated Tribes, Grand Canyon is one of the most spectacular examples of erosion anywhere in the world—unmatched in the incomparable vistas it offers visitors from the rims. The South Rim is open. The North Rim is CLOSED for the winter.",
                    states: "AZ",
                    latitude: "36.0001165336",
                    longitude: "-112.121516363",
                    address: "20 South Entrance Road, AZ 86023",
                    directionsInfo: "South Rim: Open all year, is located 60 miles north of Williams, Arizona (via route 64 from Interstate 40) and 80 miles northwest of Flagstaff (via route 180). Grand Canyon lies entirely within the state of Arizona. North Rim: Open for the season between May 15 and October 15, 2024. The North Rim is located 30 miles south of Jacob Lake on Highway 67; the actual rim of the canyon is an additional 14 miles south. Jacob Lake, AZ is located in northern Arizona on Highway 89A, not far from the Utah border.",
                    directionsURL: "http://www.nps.gov/grca/planyourvisit/directions.htm",
                    designation: "National Park",
                    weather: "This weather varies with cold winters and mild pleasant summers, moderate humidity, and considerable diurnal temperature changes at the higher elevations, with hot and drier summers at the bottom of the Grand Canyon along with cool damp winters. Summer thunderstorms and winter snowfall adds to the weather variety in this region.",
                    shortName: "Grand Canyon"
                },
                rank: 1
            }
        ]
    }
};

const mockActivities = {
    data: [
        {
            name: "Fishing",
            id: 1
        },
        {
            id: 2,
            name: "Not Fishing"
        }
    ]
};

const mockAmenities = {
    data: [
        {
            name: "Test Amenity",
            id: 1
        },
        {
            id: 7,
            name: "Restrooms"
        }
    ]
};

describe('Search component error handling', () => {
    let consoleSpy;

    beforeEach(() => {
        consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});

        consoleSpy.mockClear();

        fetch.mockImplementation((url, options) => {
            if (url.includes('api/parks/search?searchName=Test Park')) {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve(mockParks)
                });
            } else if (url.endsWith('/api/parks/favorite')) {
                if (options.method === 'POST') {
                    return Promise.resolve({
                        ok: true,
                        json: () => Promise.resolve({ data: {} }),
                    });
                }
            } else if (url.includes('/api/parks/favorite/')) {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve({ data: { parks: [1] } }),
                });
            } else if (url.includes('api/parks/amenities')) {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve(mockAmenities),
                });
            } else if (url.includes('api/parks/activities')) {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve(mockActivities),
                });
            }
            return Promise.reject(new Error('Unexpected URL in fetch mock'));
        });
    });

    afterAll(() => {
        consoleSpy.mockRestore();
    });

    test('logs an error when fetching favorites fails', async () => {
        render(<Search />);
        const user = userEvent.setup();

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockActivities,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockAmenities,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockParks,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({ data: {} }),
        });

        fetch.mockRejectedValueOnce(new Error('Failed to fetch favorites'));

        await act(async () => {
            await user.type(screen.getByPlaceholderText("Search for parks"), "Test Park");
            await user.click(screen.getByRole('button', { name: 'Search' }));
        });

        await act(async () => {
            const addFavoriteButton = await screen.findByRole('button', { name: /add to favorites/i });
            await user.click(addFavoriteButton);
        });

        await waitFor(() => {
            expect(console.error).toHaveBeenCalledWith('Failed to fetch favorites', expect.any(Error));
        });
    });


    test('logs an error when adding to favorites fails', async () => {
        render(<Search />);
        const user = userEvent.setup();

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockActivities,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockAmenities,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockParks,
        });

        fetch.mockRejectedValueOnce(new Error('Failed to add park to favorites'));

        await act(async () => {
            await user.type(screen.getByPlaceholderText('Search for parks'), 'Test Park');
            await user.click(screen.getByRole('button', { name: 'Search' }));
        });

        const addFavoriteButton = await screen.findByRole('button', { name: /add to favorites/i });
        await user.click(addFavoriteButton);

        await waitFor(() => {
            expect(console.error).toHaveBeenCalledWith('An error occurred while adding park to favorites:', expect.any(Error));
        });
    });

    test('logs an error when removing from favorites fails', async () => {
        render(<Search />);
        const user = userEvent.setup();

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockActivities,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockAmenities,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockParks,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({ data: {} }),
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => (mockFavorites),
        });

        await act(async () => {
            await user.type(screen.getByPlaceholderText("Search for parks"), "Test Park");
            await user.click(screen.getByRole('button', { name: 'Search' }));
        });

        await act(async () => {
            const addFavoriteButton = await screen.findByRole('button', { name: /add to favorites/i });
            await user.click(addFavoriteButton);
        });

        await waitFor(() => {
            expect(fetch).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({
                method: 'POST',
                headers: expect.objectContaining({
                    "Content-Type": "application/json",
                    "Authorization": expect.stringContaining("Bearer ")
                }),
                body: JSON.stringify({
                    user_id: 'dummyUserId',
                    park_id: 193,
                }),
            }));
        });

        fetch.mockRejectedValueOnce(new Error('Failed to remove park from favorites'));

        await act(async () => {
            const removeFavoriteButton = await screen.findByRole('button', {name: /remove from favorites/i});
            await user.click(removeFavoriteButton);
        });

        const confirmYesButton = screen.getByText('Yes');
        await act(async () => {
            await user.click(confirmYesButton);
        });

        await waitFor(() => {
            expect(console.error).toHaveBeenCalledWith('An error occurred while removing park from favorites:', expect.any(Error));
        });
    });

    test('logs an error when the response for adding to favorites is not OK', async () => {
        render(<Search />);
        const user = userEvent.setup();

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockActivities,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockAmenities,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockParks,
        });

        fetch.mockResolvedValueOnce({
            ok: false,
            json: async () => ({ error: 'Custom error message' }),
        });

        await act(async () => {
            await user.type(screen.getByPlaceholderText('Search for parks'), 'Test Park');
            await user.click(screen.getByRole('button', { name: 'Search' }));
        });

        await act(async () => {
            const favoriteButton = await screen.findByRole('button', {name: /add to favorites/i});
            await user.click(favoriteButton);
        });

        await waitFor(() => {
            expect(console.error).toHaveBeenCalledWith('Failed to add park to favorites:', 'Custom error message');
        });

        fetch.mockClear();
    });

    test('logs an error when the response for removing from favorites is not OK', async () => {
        render(<Search />);
        const user = userEvent.setup();

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockActivities,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockAmenities,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockParks,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({ data: {} }),
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => (mockFavorites),
        });

        await act(async () => {
            await user.type(screen.getByPlaceholderText("Search for parks"), "Test Park");
            await user.click(screen.getByRole('button', { name: 'Search' }));
        });

        await act(async () => {
            const addFavoriteButton = await screen.findByRole('button', { name: /add to favorites/i });
            await user.click(addFavoriteButton);
        });

        await waitFor(() => {
            expect(fetch).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({
                method: 'POST',
                headers: expect.objectContaining({
                    "Content-Type": "application/json",
                    "Authorization": expect.stringContaining("Bearer ")
                }),
                body: JSON.stringify({
                    user_id: 'dummyUserId',
                    park_id: 193,
                }),
            }));
        });

        fetch.mockResolvedValueOnce({
            ok: false,
            json: async () => ({ error: 'Custom error message' }),
        });

        await act(async () => {
            const removeFavoriteButton = await screen.findByRole('button', {name: /remove from favorites/i});
            await user.click(removeFavoriteButton);
        });

        const confirmYesButton = screen.getByText('Yes');
        await act(async () => {
            await user.click(confirmYesButton);
        });

        await waitFor(() => {
            expect(console.error).toHaveBeenCalledWith('Failed to remove park from favorites:', 'Custom error message');
        });

        fetch.mockClear();
    });
});
