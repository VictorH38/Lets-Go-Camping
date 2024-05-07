import React from 'react';
import {render, screen, waitFor, act } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import LoginPage from './LoginPage';
import { AuthProvider } from '../AuthContext';

jest.mock("../utils/cookieHelper", () => ({
    setCookie: jest.fn(),
}));

jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useNavigate: () => jest.fn(),
}));

describe('LoginPage Component Tests', () => {
    beforeEach(() => {
        global.fetch = jest.fn();
    });

    afterEach(() => {
        jest.clearAllMocks();
    });

    test('renders the login form with email and password fields', () => {
        render(
            <MemoryRouter>
                <AuthProvider>
                    <LoginPage />
                </AuthProvider>
            </MemoryRouter>
        );

        expect(screen.getByLabelText(/Email/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Password/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /Login/i })).toBeInTheDocument();
    });

    test('submits the form with email and password', async () => {
        const user = userEvent.setup();
        render(
            <MemoryRouter>
                <AuthProvider>
                    <LoginPage />
                </AuthProvider>
            </MemoryRouter>
        );

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => ({ token: "fake_token" }),
        });

        await act(async () => {
            await user.type(screen.getByLabelText(/Email/i), "test@example.com");
            await user.type(screen.getByLabelText(/Password/i), "password123");
            await user.click(screen.getByRole('button', {name: /Login/i}));
        });

        await waitFor(() => {
            expect(fetch).toHaveBeenCalledWith("api/users/signin", {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({email: "test@example.com", password: "password123"}),
            });
        });
    });

    test('shows an error message on failed login', async () => {
        const user = userEvent.setup();
        render(
            <MemoryRouter>
                <AuthProvider>
                    <LoginPage />
                </AuthProvider>
            </MemoryRouter>
        );

        fetch.mockResolvedValueOnce({
            ok: false,
            json: async () => ({ message: "Invalid credentials" }),
        });

        await act(async () => {
            await user.type(screen.getByLabelText(/Email/i), "wrong@example.com");
            await user.type(screen.getByLabelText(/Password/i), "wrongpassword");
            await user.click(screen.getByRole('button', {name: /Login/i}));
        });

        await waitFor(() => {
            expect(screen.getByText("Invalid credentials")).toBeInTheDocument();
        });
    });

    test('cancel login', async () => {
        const user = userEvent.setup();
        render(
            <MemoryRouter>
                <AuthProvider>
                    <LoginPage/>
                </AuthProvider>
            </MemoryRouter>
        );

        await act(async () => {
            await user.click(screen.getByRole('button', {name: /Cancel/i}));
        });
    });

    test('test lockout', async () => {
        const user = userEvent.setup();
        render(
            <MemoryRouter>
                <AuthProvider>
                    <LoginPage />
                </AuthProvider>
            </MemoryRouter>
        );

        await act(async () => {
            await user.type(screen.getByLabelText(/Email/i), "wrong@example.com");
            await user.type(screen.getByLabelText(/Password/i), "wrongpassword");

                // mock response + click button
                fetch.mockResolvedValueOnce({
                    ok: false,
                    json: async () => ({message: "Invalid password"}),
                });
                await user.click(screen.getByRole('button', {name: /Login/i,}));
        });

        await waitFor(() => {
            expect(screen.getByText("Invalid password. 3 attempts remaining in the next minute.")).toBeInTheDocument();
        });
    }, 10000);

    test('test lockout reset', async () => {
        const user = userEvent.setup();
        render(
            <MemoryRouter>
                <AuthProvider>
                    <LoginPage/>
                </AuthProvider>
            </MemoryRouter>
        );

        await act(async () => {
            await user.type(screen.getByLabelText(/Email/i), "wrong@example.com");
            await user.type(screen.getByLabelText(/Password/i), "wrongpassword");

            // mock response + click button for initial lockout
            fetch.mockResolvedValueOnce({
                ok: false,
                json: async () => ({message: "[Test] Account no longer locked"}),
            });
            await user.click(screen.getByRole('button', {name: /Login/i,}));
        });

        await waitFor(() => {
            expect(screen.getByText("Invalid password. 3 attempts remaining in the next minute.")).toBeInTheDocument();
        });
    });
});
