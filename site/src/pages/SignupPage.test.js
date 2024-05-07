import React from 'react';
import { render, screen, waitFor, act } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import SignupPage from './SignupPage';
import { setCookie } from "../utils/cookieHelper";
import { AuthProvider } from '../AuthContext';

jest.mock("../utils/cookieHelper", () => ({
    setCookie: jest.fn(),
    getCookie: jest.fn(),
}));

describe('SignupPage Component Tests', () => {
    test('renders the signup form with required fields', async () => {
        render(
            <MemoryRouter>
                <AuthProvider>
                    <SignupPage />
                </AuthProvider>
            </MemoryRouter>
        );

        expect(screen.getByLabelText(/Name/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Email/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/^Password$/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Confirm Password/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /Sign Up/i })).toBeInTheDocument();
    });

    test('displays error when passwords do not match', async () => {
        const user = userEvent.setup();
        render(
            <MemoryRouter>
                <AuthProvider>
                    <SignupPage />
                </AuthProvider>
            </MemoryRouter>
        );

        await act(async () => {
            await user.type(screen.getByLabelText(/^Password$/i), 'password123');
            await user.type(screen.getByLabelText(/Confirm Password/i), 'password');
            await user.click(screen.getByRole('button', {name: /Sign Up/i}));
        });

        await waitFor(() => {
            expect(screen.getByText(/Passwords don't match/i)).toBeInTheDocument();
        });
    });

    test('displays error from the server on failed signup', async () => {
        const user = userEvent.setup();
        render(
            <MemoryRouter>
                <AuthProvider>
                    <SignupPage />
                </AuthProvider>
            </MemoryRouter>
        );

        global.fetch = jest.fn().mockResolvedValue({
            ok: false,
            json: async () => ({ message: "Email already in use" }),
        });

        await act(async () => {
            await user.type(screen.getByLabelText(/Name/i), 'Test User');
            await user.type(screen.getByLabelText(/Email/i), 'test@example.com');
            await user.type(screen.getByLabelText(/^Password$/i), 'password123');
            await user.type(screen.getByLabelText(/Confirm Password/i), 'password123');
            await user.click(screen.getByRole('button', {name: /Sign Up/i}));
        });

        await waitFor(() => {
            expect(screen.getByText(/Email already in use/i)).toBeInTheDocument();
        });
    });

    test('navigates to home page on successful signup', async () => {
        const user = userEvent.setup();
        render(
            <MemoryRouter>
                <AuthProvider>
                    <SignupPage />
                </AuthProvider>
            </MemoryRouter>
        );

        global.fetch = jest.fn().mockResolvedValue({
            ok: true,
            json: async () => ({ token: "fake_token" }),
        });

        await act(async () => {
            await user.type(screen.getByLabelText(/Name/i), 'New User');
            await user.type(screen.getByLabelText(/Email/i), 'newuser@example.com');
            await user.type(screen.getByLabelText(/^Password$/i), 'password123');
            await user.type(screen.getByLabelText(/Confirm Password/i), 'password123');
            await user.click(screen.getByRole('button', {name: /Sign Up/i}));
        });

        await waitFor(() => {
            expect(setCookie).toHaveBeenCalledWith("token", "fake_token");
        });
    });

    test('navigates to login page from signup page', async () => {
        const user = userEvent.setup();
        render(
            <MemoryRouter>
                <AuthProvider>
                    <SignupPage />
                </AuthProvider>
            </MemoryRouter>
        );

        expect(screen.getByText(/Sign Up/i)).toBeInTheDocument();

        const loginLink = screen.getByRole('link', { name: /log\s*in!/i });
        await act(async () => {
            await user.click(loginLink);
        });

        waitFor(() => {
            expect(screen.findByLabelText(/Email/i)).toBeInTheDocument();
            expect(screen.findByLabelText(/Password/i)).toBeInTheDocument();
            expect(screen.findByRole('button', {name: /Login/i})).toBeInTheDocument();
        });
    });

    test('cancel signup', async () => {
        const user = userEvent.setup();
        render(
            <MemoryRouter>
                <AuthProvider>
                    <SignupPage/>
                </AuthProvider>
            </MemoryRouter>
        );

        await act(async () => {
            await user.click(screen.getByRole('button', {name: /Cancel/i}));
        });
    });
});
