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
                    id: 1,
                    fullName: 'Test Park',
                    description: "A description of the test park",
                    states: "CA",
                    latitude: "123",
                    longitude: "456",
                    address: "123 Park St",
                    directionsInfo: "Use the main gate",
                    weather: "Sunny",
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
                    { id: 1132, title: 'Grand', caption: "People come from all over the world to view Grand Canyon's sunset", url: 'https://www.nps.gov/common/uploads/structured_data/3C7B12D1-1DD8-B71B-0BCE0712F9CEA155.jpg', altText: 'The canyon glows orange as people visit Mather Point, a rock outcropping that juts into Grand Canyon' },
                    { id: 1133, title: 'Grand', caption: 'A popular outdoor site for weddings and receptions… miles (37 km) from the North Rim developed area.', url: 'https://www.nps.gov/common/uploads/structured_data/3C7B143E-1DD8-B71B-0BD4A1EF96847292.jpg', altText: 'The Cape Royal viewpoint curves into the distance and closer rock formations jut into the canyon.' },
                    { id: 1134, title: 'Grand', caption: 'The Watchtower is located at Desert View, the east…a on the South Rim of Grand Canyon National Park.', url: 'https://www.nps.gov/common/uploads/structured_data/3C7B15A4-1DD8-B71B-0BFADECB506765CC.jpg', altText: 'The Desert View Watchtower looms 70 feet into the air over a vast and dramatic view of the canyon.' },
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
                    id: 1,
                    fullName: 'Test Park',
                    description: "A description of the test park",
                    states: "CA",
                    latitude: "123",
                    longitude: "456",
                    address: "123 Park St",
                    directionsInfo: "Use the main gate",
                    weather: "Sunny",
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

describe("Favorite button tests", () => {
    beforeEach(() => {
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
                    json: () => Promise.resolve({ data: { parks: [] } }),
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

    test("renders favorite button for each park", async () => {
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
            json: async () => mockFavorites,
        });

        const searchInput = screen.getByPlaceholderText("Search for parks");
        const searchButton = screen.getByRole('button', { name: 'Search' });

        await act(async () => {
            await user.type(searchInput, "Test Park");
            await user.click(searchButton);
        });

        await waitFor(() => expect(screen.getByTestId("park-title-1")).toBeInTheDocument());

        const favoriteButtons = screen.getAllByText("Add to Favorites");
        await waitFor(() => {
            expect(favoriteButtons.length).toBeGreaterThan(0);
        });
    });

    test("successfully adds park to favorites", async () => {
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

        const searchInput = screen.getByPlaceholderText("Search for parks");
        const searchButton = screen.getByRole('button', { name: 'Search' });

        await act(async () => {
            await user.type(searchInput, "Test Park");
            await user.click(searchButton);
        });

        await waitFor(() => expect(screen.getByTestId("park-title-1")).toBeInTheDocument());

        const favoriteButton = screen.getByRole('button', { name: 'Add to Favorites' });
        await act(async () => {
            await user.click(favoriteButton);
        });

        await waitFor(() => {
            expect(favoriteButton.textContent).toBe('Remove from Favorites');
        });
    });

    test("successfully removes park from favorites after saying yes to confirmation", async () => {
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

        const searchInput = screen.getByPlaceholderText("Search for parks");
        const searchButton = screen.getByRole('button', { name: 'Search' });

        await act(async () => {
            await user.type(searchInput, "Test Park");
            await user.click(searchButton);
        });

        await waitFor(() => expect(screen.getByTestId("park-title-1")).toBeInTheDocument());

        const favoriteButton = screen.getByRole('button', { name: 'Add to Favorites' });
        await act(async () => {
            await user.click(favoriteButton);
        });

        await waitFor(() => {
            expect(favoriteButton.textContent).toBe('Remove from Favorites');
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({ data: {} }),
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({ data: { parks: [] } }),
        });

        await act(async () => {
            await user.click(favoriteButton);
        });

        const confirmYesButton = screen.getByText('Yes');
        await act(async () => {
            await user.click(confirmYesButton);
        });

        await waitFor(() => {
            expect(favoriteButton.textContent).toBe('Add to Favorites');
        });
    });

    test("successfully doesn't remove park from favorites after saying no to confirmation", async () => {
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

        const searchInput = screen.getByPlaceholderText("Search for parks");
        const searchButton = screen.getByRole('button', { name: 'Search' });

        await act(async () => {
            await user.type(searchInput, "Test Park");
            await user.click(searchButton);
        });

        await waitFor(() => expect(screen.getByTestId("park-title-1")).toBeInTheDocument());

        const favoriteButton = screen.getByRole('button', { name: 'Add to Favorites' });
        await act(async () => {
            await user.click(favoriteButton);
        });

        await waitFor(() => {
            expect(favoriteButton.textContent).toBe('Remove from Favorites');
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({ data: {} }),
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({ data: { parks: [] } }),
        });

        await act(async () => {
            await user.click(favoriteButton);
        });

        const confirmYesButton = screen.getByText('No');
        await act(async () => {
            await user.click(confirmYesButton);
        });

        await waitFor(() => {
            expect(favoriteButton.textContent).toBe('Remove from Favorites');
        });
    });
});

describe("Search component data handling tests", () => {
    let consoleSpy;

    beforeEach(() => {
        consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
        consoleSpy.mockClear();

        fetch.mockImplementation((url, options) => {
            if (url.includes('api/parks/search?searchName=Test Park')) {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve({})
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

    test("handles missing data in fetchFavorites", async () => {
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

        fetch.mockClear();
    });

    test("handles empty responseData.data in fetchFavorites", async () => {
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

        const searchInput = screen.getByPlaceholderText("Search for parks");
        const searchButton = screen.getByRole('button', { name: 'Search' });

        await act(async () => {
            await user.type(searchInput, "Test Park");
            await user.click(searchButton);
        });

        await waitFor(() => expect(screen.getByTestId("park-title-1")).toBeInTheDocument());

        const favoriteButton = screen.getByRole('button', { name: 'Add to Favorites' });
        await act(async () => {
            await user.click(favoriteButton);
        });

        await waitFor(() => {
            expect(console.error).toHaveBeenCalled();
        });

        fetch.mockClear();
    });

    test("handles empty response.data in handleSearch", async () => {
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
            json: async () => ({ data: {} }),
        });

        const searchInput = screen.getByPlaceholderText("Search for parks");
        const searchButton = screen.getByRole('button', { name: 'Search' });

        await act(async () => {
            await user.type(searchInput, "Test Park");
            await user.click(searchButton);
        });

        const noResultsMessage = screen.getByText(`No results for "Test Park"`);
        await waitFor(() => {
            expect(noResultsMessage).toBeInTheDocument();
            expect(screen.queryByText("Test Park")).not.toBeInTheDocument();
        });

        expect(console.error).not.toHaveBeenCalled();

        fetch.mockClear();
    });

    test("handles empty response.data in getActivities", async () => {
        render(<Search />);
        const user = userEvent.setup();

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({}),
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockAmenities,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockParks,
        });

        const searchInput = screen.getByPlaceholderText("Search for parks");
        const searchButton = screen.getByRole('button', { name: 'Search' });

        await act(async () => {
            await user.type(searchInput, "Test Park");
            await user.click(searchButton);
        });

        expect(console.error).not.toHaveBeenCalled();

        fetch.mockClear();
    });

    test("handles empty response.data in getAmenities", async () => {
        render(<Search />);
        const user = userEvent.setup();

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockActivities,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({}),
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockParks,
        });

        const searchInput = screen.getByPlaceholderText("Search for parks");
        const searchButton = screen.getByRole('button', { name: 'Search' });

        await act(async () => {
            await user.type(searchInput, "Test Park");
            await user.click(searchButton);
        });

        expect(console.error).not.toHaveBeenCalled();

        fetch.mockClear();
    });
});
