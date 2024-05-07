import { render, screen, waitFor, act, fireEvent } from "@testing-library/react";
import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';
import Favorites from "./Favorites";
import React from "react";
import {getCookie} from "../utils/cookieHelper";

jest.mock('../utils/cookieHelper', () => ({
    getCookie: jest.fn(() => 'dummyToken'),
}));

beforeEach(() => {
    global.fetch = jest.fn();

    getCookie.mockImplementation((name) => {
        if (name === 'token') return 'dummyToken';
        if (name === 'user_id') return '1';
    });
});

const mockUsers = {
    data: {
        users: [
            {
                id: 1,
                name: "John Doe",
                isPublic: true,
                favoriteParks: []
            }
        ]
    }
};

const mockUsersPrivate = {
    data: {
        users: [
            {
                id: 1,
                name: "John Doe",
                isPublic: false,
                favoriteParks: []
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
            },
            {
                park: {
                    id: 2,
                    fullName: 'Test Park 2',
                    description: "A description of the test park 2",
                    states: "CA",
                    latitude: "123",
                    longitude: "456",
                    address: "123 Park Ave",
                    directionsInfo: "Use the main gate",
                    weather: "Sunny",
                },
                rank: 2
            }
        ]
    }
};

describe("Favorites components tests", () => {
    beforeEach(() => {
        fetch.mockImplementation((url) => {
            if (url.includes('/api/parks/favorite/')) {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve(mockFavorites),
                });
            } else if (url.endsWith('/api/parks/unfavorite')) {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve({}),
                });
            } else if (url.endsWith('/api/parks/favorite/ranks')) {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve({}),
                });
            } else if (url.includes('/api/users')) {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve(mockUsers),
                });
            } else if (url.endsWith('/api/users/public')) {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve({}),
                });
            }
            return Promise.reject(new Error('Unexpected URL in fetch mock'));
        });
    });

    afterEach(() => {
        jest.restoreAllMocks();
    });

    test("displays favorite parks correctly", async () => {
        render(<Favorites />);

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => (mockFavorites),
        });

        await waitFor(() => {
            expect(screen.getByText("My Favorite Parks")).toBeInTheDocument();
        });

        fetch.mockClear();
    });

    test("displays message when there are no favorite parks", async () => {
        render(<Favorites />);

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({data: {parks: []}}),
        });

        await waitFor(async () => {
            expect(screen.getByText("You have no favorite parks")).toBeInTheDocument();
        });

        fetch.mockClear();
    });

    test("expand and collapse favorite park details", async () => {
        render(<Favorites />);
        const user = userEvent.setup();

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockFavorites,
        });

        await waitFor(() => {
            expect(screen.getByText("My Favorite Parks")).toBeInTheDocument();
        });

        const firstParkCard = screen.getByTestId('favorite-park-card-1');
        await act(async () => {
            await user.click(firstParkCard);
        });

        waitFor(() => {
            expect(screen.queryByText("123 Park St")).toBeInTheDocument();
        });

        await act(async () => {
            await user.click(firstParkCard);
        });

        waitFor(() => {
            expect(screen.queryByText("123 Park St")).not.toBeInTheDocument();
        });

        fetch.mockClear();
    });

    test("handles undefined detailEl when toggling park details", async () => {
        render(<Favorites testRefMissing={true} />);
        const user = userEvent.setup();

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockFavorites,
        });

        await waitFor(() => expect(screen.getByText("My Favorite Parks")).toBeInTheDocument());

        const firstParkCard = screen.getByTestId('favorite-park-card-1');

        await act(async () => {
            await user.click(firstParkCard);
        });

        expect(screen.getByText("My Favorite Parks")).toBeInTheDocument();
    });

    test("removes a park from favorites successfully after saying yes to confirmation", async () => {
        render(<Favorites />);
        const user = userEvent.setup();

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockFavorites,
        });

        await waitFor(() => {
            expect(screen.getByText("My Favorite Parks")).toBeInTheDocument();
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({}),
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({data: {parks: []}}),
        });

        await act(async () => {
            const removeFavoriteButton = await screen.findByTestId('remove-favorite-button-1');
            expect(removeFavoriteButton).toBeInTheDocument();
            await user.click(removeFavoriteButton);
        });

        const confirmYesButton = screen.getByText('Yes');
        await act(async () => {
            await user.click(confirmYesButton);
        });

        await waitFor(() => {
            expect(fetch).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({
                headers: expect.objectContaining({
                    "Authorization": expect.stringContaining("Bearer ")
                }),
            }));

            expect(fetch).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({
                method: 'POST',
                headers: expect.objectContaining({
                    "Content-Type": "application/json",
                    "Authorization": expect.stringContaining("Bearer ")
                }),
                body: JSON.stringify({
                    user_id: '1',
                    park_id: mockFavorites.data.parks[0].park.id,
                }),
            }));

            expect(fetch).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({
                headers: expect.objectContaining({
                    "Authorization": expect.stringContaining("Bearer ")
                }),
            }));
        });

        fetch.mockClear();
    });

    test("doesn't remove a park from favorites successfully after saying no to confirmation", async () => {
        render(<Favorites />);
        const user = userEvent.setup();

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockFavorites,
        });

        await waitFor(() => {
            expect(screen.getByText("My Favorite Parks")).toBeInTheDocument();
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({}),
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({data: {parks: []}}),
        });

        await act(async () => {
            const removeFavoriteButton = await screen.findByTestId('remove-favorite-button-1');
            expect(removeFavoriteButton).toBeInTheDocument();
            await user.click(removeFavoriteButton);
        });

        const confirmNoButton = screen.getByText('No');
        await act(async () => {
            await user.click(confirmNoButton);
        });

        await waitFor(() => {
            expect(fetch).toHaveBeenCalledWith(expect.anything(), expect.objectContaining({
                headers: expect.objectContaining({
                    "Authorization": expect.stringContaining("Bearer ")
                }),
            }));
        });

        fetch.mockClear();
    });

    test('should reorder favorites after manual drag and drop', async () => {
        render(<Favorites />);

        fetch.mockResolvedValueOnce({
            ok: true,
            json: () => Promise.resolve(mockFavorites),
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: () => Promise.resolve({}),
        });

        await waitFor(() => expect(screen.getByText("Test Park")).toBeInTheDocument());

        const draggableElements = screen.getAllByTestId(/^droppable-/);
        const dragStart = draggableElements[0];
        const dragEnd = draggableElements[1];

        const sufficientYMovement = 500;

        fireEvent.mouseDown(dragStart, { clientX: 0, clientY: 0 });
        fireEvent.dragStart(dragStart);

        fireEvent.dragEnter(dragEnd, { clientX: 0, clientY: sufficientYMovement });
        fireEvent.mouseMove(dragEnd, { clientX: 0, clientY: sufficientYMovement });
        fireEvent.dragOver(dragEnd, { clientX: 0, clientY: sufficientYMovement });

        fireEvent.drop(dragEnd);
        fireEvent.mouseUp(dragEnd);

        await waitFor(() => {
            const reorderedElements = screen.getAllByTestId(/^droppable-/);
            expect(reorderedElements[0]).toHaveTextContent('Test Park');
        });

        fetch.mockClear();
    });

    test("publicity button toggles from Public to Private", async () => {
        fetch.mockResolvedValueOnce({
            ok: true,
            json: () => Promise.resolve(mockUsers),
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: () => Promise.resolve(mockFavorites),
        });

        render(<Favorites />);
        const user = userEvent.setup();

        await waitFor(() => {
            expect(screen.getByText("My Favorite Parks")).toBeInTheDocument();
            expect(screen.getByText("Public")).toBeInTheDocument();
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: () => Promise.resolve({}),
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: () => Promise.resolve(mockUsersPrivate),
        });

        await act(async () => {
            const publicityButton = await screen.findByTestId('publicity-button');
            await user.click(publicityButton);
        });

        await waitFor(() => {
            expect(screen.getByText("Private")).toBeInTheDocument();
        });

        fetch.mockClear();
    });
});

describe('Favorites component error handling', () => {
    let consoleSpy;

    beforeEach(() => {
        consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
        consoleSpy.mockClear();

        fetch.mockImplementation((url) => {
            if (url.includes('/api/parks/favorite/')) {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve(mockFavorites),
                });
            } else if (url.endsWith('/api/parks/unfavorite')) {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve({}),
                });
            } else if (url.includes('/api/users')) {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve(mockUsers),
                });
            }
            return Promise.reject(new Error('Unexpected URL in fetch mock'));
        });
    });

    afterAll(() => {
        consoleSpy.mockRestore();
        fetch.mockRestore();
    });

    test('logs an error when fetching current user fails', async () => {
        fetch.mockRejectedValueOnce(new Error('Failed to fetch favorites'));

        render(<Favorites />);

        await waitFor(() => {
            expect(console.error).toHaveBeenCalledWith("Failed to fetch current user", expect.any(Error));
        });

        fetch.mockClear();
    });

    test('logs an error when toggling publicity fails', async () => {
        fetch.mockResolvedValueOnce({
            ok: true,
            json: () => Promise.resolve(mockUsers),
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: () => Promise.resolve(mockFavorites),
        });

        render(<Favorites />);
        const user = userEvent.setup();

        await waitFor(() => {
            expect(screen.getByText("My Favorite Parks")).toBeInTheDocument();
            expect(screen.getByText("Public")).toBeInTheDocument();
        });

        fetch.mockRejectedValueOnce(new Error('Error updating user publicity'))

        await act(async () => {
            const publicityButton = await screen.findByTestId('publicity-button');
            await user.click(publicityButton);
        });

        await waitFor(() => {
            expect(console.error).toHaveBeenCalledWith("Error updating user publicity", expect.any(Error));
        });

        fetch.mockClear();
    });

    test('logs an error when fetching favorites fails', async () => {
        render(<Favorites />);
        const user = userEvent.setup();

        await waitFor(() => {
            expect(screen.getByText("My Favorite Parks")).toBeInTheDocument();
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({}),
        });

        fetch.mockRejectedValueOnce(new Error('Failed to fetch favorites'));

        await act(async () => {
            const removeFavoriteButton = await screen.findByTestId('remove-favorite-button-1');
            expect(removeFavoriteButton).toBeInTheDocument();
            await user.click(removeFavoriteButton);
        });

        const confirmYesButton = screen.getByText('Yes');
        await act(async () => {
            await user.click(confirmYesButton);
        });

        await waitFor(() => {
            expect(console.error).toHaveBeenCalledWith("Failed to fetch favorites", expect.any(Error));
        });

        fetch.mockClear();
    });

    test('logs an error when removing from favorites fails', async () => {
        render(<Favorites />);
        const user = userEvent.setup();

        await waitFor(() => {
            expect(screen.getByText("My Favorite Parks")).toBeInTheDocument();
        });

        fetch.mockRejectedValueOnce(new Error('Failed to remove park from favorites'));

        await act(async () => {
            const removeFavoriteButton = await screen.findByTestId('remove-favorite-button-1');
            expect(removeFavoriteButton).toBeInTheDocument();
            await user.click(removeFavoriteButton);
        });

        const confirmYesButton = screen.getByText('Yes');
        await act(async () => {
            await user.click(confirmYesButton);
        });

        await waitFor(() => {
            expect(console.error).toHaveBeenCalledWith('An error occurred while removing park from favorites:', expect.any(Error));
        });

        fetch.mockClear();
    });

    test('logs an error when updating ranks fails', async () => {
        render(<Favorites />);

        await waitFor(() => expect(screen.getByText("Test Park")).toBeInTheDocument());

        fetch.mockRejectedValueOnce(new Error('An error occurred while updating favorite park ranks:'));

        const draggableElements = screen.getAllByTestId(/^droppable-/);
        const dragStart = draggableElements[0];
        const dragEnd = draggableElements[1];

        fireEvent.mouseDown(dragStart, { clientX: 0, clientY: 0 });
        fireEvent.dragStart(dragStart);
        fireEvent.dragEnter(dragEnd, { clientX: 0, clientY: 500 });
        fireEvent.mouseMove(dragEnd, { clientX: 0, clientY: 500 });
        fireEvent.dragOver(dragEnd, { clientX: 0, clientY: 500 });
        fireEvent.drop(dragEnd);
        fireEvent.mouseUp(dragEnd);

        await waitFor(() => {
            expect(console.error).toHaveBeenCalledWith('An error occurred while updating favorite park ranks:', expect.any(Error));
        });

        fetch.mockClear();
    });

    test('logs an error when the response for toggling publicity is not OK', async () => {
        fetch.mockResolvedValueOnce({
            ok: true,
            json: () => Promise.resolve(mockUsers),
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: () => Promise.resolve(mockFavorites),
        });

        render(<Favorites />);
        const user = userEvent.setup();

        await waitFor(() => {
            expect(screen.getByText("My Favorite Parks")).toBeInTheDocument();
            expect(screen.getByText("Public")).toBeInTheDocument();
        });

        fetch.mockResolvedValueOnce({
            ok: false,
            json: async () => ({ error: 'Custom error message' }),
        });

        await act(async () => {
            const publicityButton = await screen.findByTestId('publicity-button');
            await user.click(publicityButton);
        });

        await waitFor(() => {
            expect(console.error).toHaveBeenCalledWith('Failed to update user publicity');
        });

        fetch.mockClear();
    });

    test('logs an error when the response for removing from favorites is not OK', async () => {
        render(<Favorites />);
        const user = userEvent.setup();

        await waitFor(() => {
            expect(screen.getByText("My Favorite Parks")).toBeInTheDocument();
        });

        fetch.mockResolvedValueOnce({
            ok: false,
            json: async () => ({ error: 'Custom error message' }),
        });

        await act(async () => {
            const removeFavoriteButton = await screen.findByTestId('remove-favorite-button-1');
            expect(removeFavoriteButton).toBeInTheDocument();
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

    test('logs an error when the response for updating ranks is not OK', async () => {
        render(<Favorites />);

        await waitFor(() => expect(screen.getByText("Test Park")).toBeInTheDocument());

        fetch.mockResolvedValueOnce({
            ok: false,
            json: async () => ({ error: 'Custom error message' }),
        });

        const draggableElements = screen.getAllByTestId(/^droppable-/);
        const dragStart = draggableElements[0];
        const dragEnd = draggableElements[1];

        fireEvent.mouseDown(dragStart, { clientX: 0, clientY: 0 });
        fireEvent.dragStart(dragStart);
        fireEvent.dragEnter(dragEnd, { clientX: 0, clientY: 500 });
        fireEvent.mouseMove(dragEnd, { clientX: 0, clientY: 500 });
        fireEvent.dragOver(dragEnd, { clientX: 0, clientY: 500 });
        fireEvent.drop(dragEnd);
        fireEvent.mouseUp(dragEnd);

        await waitFor(() => {
            expect(console.error).toHaveBeenCalledWith('Failed to update favorite park ranks');
        });

        fetch.mockClear();
    });
});

describe("Favorites with missing credentials or references", () => {
    beforeEach(() => {
        fetch.mockReset();
        fetch.mockImplementation(() => Promise.reject(new Error('Fetch should not be called')));
    });

    afterEach(() => {
        jest.restoreAllMocks();
    });

    test("does not fetch current user if userId is missing", async () => {
        getCookie.mockImplementation((name) => {
            if (name === 'token') return 'dummyToken';
            return undefined;
        });

        const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

        render(<Favorites />);

        expect(fetch).not.toHaveBeenCalled();
        expect(consoleSpy).not.toHaveBeenCalledWith("Failed to fetch current user", expect.any(Error));

        consoleSpy.mockRestore();
    });

    test("does not fetch current user if token is missing", async () => {
        getCookie.mockImplementation((name) => {
            if (name === 'user_id') return 'dummyUserId';
            return undefined;
        });

        const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

        render(<Favorites />);

        expect(fetch).not.toHaveBeenCalled();
        expect(consoleSpy).not.toHaveBeenCalledWith("Failed to fetch current user", expect.any(Error));

        consoleSpy.mockRestore();
    });

    test("does not fetch favorites if userId is missing", async () => {
        getCookie.mockImplementation((name) => {
            if (name === 'token') return 'dummyToken';
            return undefined;
        });

        const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

        render(<Favorites />);

        expect(fetch).not.toHaveBeenCalled();
        expect(consoleSpy).not.toHaveBeenCalledWith("Failed to fetch favorites", expect.any(Error));

        consoleSpy.mockRestore();
    });

    test("does not fetch favorites if token is missing", async () => {
        getCookie.mockImplementation((name) => {
            if (name === 'user_id') return 'dummyUserId';
            return undefined;
        });

        const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

        render(<Favorites />);

        expect(fetch).not.toHaveBeenCalled();
        expect(consoleSpy).not.toHaveBeenCalledWith("Failed to fetch favorites", expect.any(Error));

        consoleSpy.mockRestore();
    });

    test("does not toggle publicity if token is missing", async () => {
        getCookie.mockImplementation((name) => {
            if (name === 'user_id') return 'dummyUserId';
            return undefined;
        });

        const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

        render(<Favorites />);
        const user = userEvent.setup();

        await act(async () => {
            const publicityButton = await screen.findByTestId('publicity-button');
            await user.click(publicityButton);
        });

        expect(fetch).not.toHaveBeenCalled();
        expect(consoleSpy).not.toHaveBeenCalledWith("Error updating user publicity", expect.any(Error));

        consoleSpy.mockRestore();
    });
});

describe("Favorites component data handling tests", () => {
    let consoleSpy;

    beforeEach(() => {
        consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
        consoleSpy.mockClear();
    });

    afterAll(() => {
        consoleSpy.mockRestore();
        jest.restoreAllMocks();
    });

    test("handles empty responseData.data in fetchCurrentUser", async () => {
        fetch.mockResolvedValueOnce({
            ok: true,
            json: () => Promise.resolve({})
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: () => Promise.resolve(mockFavorites),
        });

        render(<Favorites />);

        await waitFor(() => {
            expect(screen.getByText("Private")).toBeInTheDocument();
        });

        expect(consoleSpy).not.toHaveBeenCalled();
    });
});
