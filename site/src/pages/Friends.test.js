import { render, screen, waitFor, act } from "@testing-library/react";
import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';
import Friends from "./Friends";
import React from "react";
import { getCookie } from "../utils/cookieHelper";

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
                favoriteParks: [
                    { park: { id: 101, fullName: "Yellowstone National Park" }, rank: 1 },
                    { park: { id: 102, fullName: "Zion National Park" }, rank: 2 }
                ]
            },
            {
                id: 2,
                name: "Jane Smith",
                isPublic: true,
                favoriteParks: [
                    { park: { id: 101, fullName: "Yellowstone National Park" }, rank: 2 },
                    { park: { id: 103, fullName: "Grand Canyon National Park" }, rank: 1 }
                ]
            }
        ]
    }
};

describe("Friends component tests", () => {
    beforeEach(() => {
        fetch.mockImplementation((url) => {
            if (url.includes('/api/users')) {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve(mockUsers),
                });
            }
            return Promise.reject(new Error('Unexpected URL in fetch mock'));
        });
    });

    afterEach(() => {
        jest.restoreAllMocks();
    });

    test("displays friends list correctly", async () => {
        render(<Friends />);

        await waitFor(() => {
            expect(screen.getByText("My Friends")).toBeInTheDocument();
            expect(screen.getByText("Jane Smith")).toBeInTheDocument();
        });
    });

    test("displays no friends message when there are no friends", async () => {
        render(<Friends />);

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({data: {users: []}}),
        });

        await waitFor(async () => {
            expect(screen.getByText("You have no friends")).toBeInTheDocument();
        });
    });

    test("expands and collapses friend details", async () => {
        render(<Friends />);
        const user = userEvent.setup();

        await waitFor(() => {
            expect(screen.getByText("My Friends")).toBeInTheDocument();
        });

        const friendCard = screen.getByTestId("user-card-2");
        await act(async () => {
            await user.click(friendCard);
        });

        waitFor(() => {
            expect(screen.queryByText("Grand Canyon National Park")).toBeInTheDocument();
        });

        await act(async () => {
            await user.click(friendCard);
        });

        waitFor(() => {
            expect(screen.queryByText("Grand Canyon National Park")).not.toBeInTheDocument();
        });
    });

    test("displays no parks for friends with private parks", async () => {
        const mockUsersPrivate = {
            data: {
                users: [
                    { id: 1, name: "User One", isPublic: true, favoriteParks: [{ park: { id: 101 }, rank: 1 }] },
                    { id: 2, name: "User Two", isPublic: false, favoriteParks: [{ park: { id: 102 }, rank: 2 }] }
                ]
            }
        };

        fetch.mockResolvedValueOnce({
            ok: true,
            json: () => Promise.resolve(mockUsersPrivate)
        });

        render(<Friends />);
        const user = userEvent.setup();

        await waitFor(() => {
            expect(screen.getByText("My Friends")).toBeInTheDocument();
        });

        const friendCard = screen.getByTestId("user-card-2");
        await act(async () => {
            await user.click(friendCard);
        });

        await waitFor(() => {
            expect(screen.queryByText("Favorite parks are private")).toBeInTheDocument();
        });

        fetch.mockClear();
    });

    test("calculates suggested parks correctly", async () => {
        render(<Friends />);
        const user = userEvent.setup();

        await waitFor(() => {
            expect(screen.getByText("My Friends")).toBeInTheDocument();
        });

        const friendCard = screen.getByTestId("user-card-2");
        await act(async () => {
            await user.click(friendCard);
        });

        await waitFor(() => {
            const suggestedParkText = screen.getByText("Suggested Park:");
            expect(suggestedParkText.nextSibling.textContent).toBe("Yellowstone National Park");
        });
    });
});

describe('Friends component error handling', () => {
    let consoleSpy;

    beforeEach(() => {
        consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
        consoleSpy.mockClear();
    });

    afterAll(() => {
        consoleSpy.mockRestore();
        fetch.mockRestore();
    });

    test('logs an error when fetching users fails', async () => {
        const errorMessage = 'Failed to fetch users';
        fetch.mockRejectedValueOnce(new Error(errorMessage));

        render(<Friends />);

        await waitFor(() => {
            expect(consoleSpy).toHaveBeenCalledWith(errorMessage, expect.any(Error));
        });
    });
});

describe("Friends with missing credentials or references", () => {
    let consoleSpy;

    beforeEach(() => {
        consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
        consoleSpy.mockClear();

        fetch.mockReset();
        fetch.mockImplementation(() => Promise.reject(new Error('Fetch should not be called')));
    });

    afterEach(() => {
        consoleSpy.mockRestore();
        jest.restoreAllMocks();
    });

    test("does not fetch users if userId is missing", async () => {
        getCookie.mockImplementation((name) => {
            if (name === 'token') return 'dummyToken';
            return undefined;
        });

        const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

        render(<Friends />);

        expect(fetch).not.toHaveBeenCalled();
        expect(consoleSpy).not.toHaveBeenCalledWith("Failed to fetch users", expect.any(Error));
    });

    test("does not fetch users if token is missing", async () => {
        getCookie.mockImplementation((name) => {
            if (name === 'user_id') return 'dummyUserId';
            return undefined;
        });

        const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

        render(<Friends />);

        expect(fetch).not.toHaveBeenCalled();
        expect(consoleSpy).not.toHaveBeenCalledWith("Failed to fetch users", expect.any(Error));
    });
});

describe("Friends component data handling tests", () => {
    let consoleSpy;

    beforeEach(() => {
        consoleSpy = jest.spyOn(console, 'error').mockImplementation(() => {});
        consoleSpy.mockClear();
    });

    afterAll(() => {
        consoleSpy.mockRestore();
        jest.restoreAllMocks();
    });

    test("handles empty responseData.data in fetchUsers", async () => {
        fetch.mockResolvedValueOnce({
            ok: true,
            json: () => Promise.resolve({})
        });

        render(<Friends />);

        await waitFor(() => {
            expect(screen.getByText("You have no friends")).toBeInTheDocument();
        });

        expect(consoleSpy).not.toHaveBeenCalled();
    });

    test("handles when there are no common parks between friends", async () => {
        const mockUsersWithoutCommonParks = {
            data: {
                users: [
                    { id: 1, name: "User One", isPublic: true, favoriteParks: [{ park: { id: 101 }, rank: 1 }] },
                    { id: 2, name: "User Two", isPublic: true, favoriteParks: [{ park: { id: 102 }, rank: 2 }] }
                ]
            }
        };

        fetch.mockResolvedValueOnce({
            ok: true,
            json: () => Promise.resolve(mockUsersWithoutCommonParks)
        });

        render(<Friends />);

        await waitFor(() => {
            expect(screen.getByText("My Friends")).toBeInTheDocument();
        });

        const suggestedText = screen.queryAllByText(/You have no common favorite parks to suggest/i);
        expect(suggestedText.length).toBeGreaterThan(0);
    });

    test("does not suggest parks when the average rank is higher than the maximum average rank", async () => {
        const mockUsersWithHigherAverageRank = {
            data: {
                users: [
                    { id: 1, name: "User One", isPublic: true, favoriteParks: [{ park: { id: 101, fullName: "Common Park" }, rank: 500 }] },
                    { id: 2, name: "User Two", isPublic: true, favoriteParks: [{ park: { id: 101, fullName: "Common Park" }, rank: 500 }] }
                ]
            }
        };

        fetch.mockResolvedValueOnce({
            ok: true,
            json: () => Promise.resolve(mockUsersWithHigherAverageRank)
        });

        render(<Friends />);

        await waitFor(() => {
            expect(screen.getByText("My Friends")).toBeInTheDocument();
        });

        const suggestedText = screen.queryAllByText(/You have no common favorite parks to suggest/i);
        expect(suggestedText.length).toBeGreaterThan(0);
    });
});
