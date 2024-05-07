import React from 'react';
import { render, screen, waitFor, act } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import '@testing-library/jest-dom';
import { MemoryRouter } from 'react-router-dom';
import App from './App';
import { AuthProvider } from './AuthContext';

describe('App Component Navigation Tests', () => {
    test('renders home page and navigates to login page', async () => {
        const user = userEvent.setup();
        render(
            <MemoryRouter initialEntries={['/']}>
                <AuthProvider>
                    <App />
                </AuthProvider>
            </MemoryRouter>
        );

        expect(screen.getByText("Let's Go Camping")).toBeInTheDocument();

        await act(async () => {
            const loginLink = await screen.getByRole('link', { name: /^login$/i });
            await user.click(loginLink);
        });

        waitFor(() => {
            expect(screen.findByLabelText(/Email/i)).toBeInTheDocument();
            expect(screen.findByLabelText(/Password/i)).toBeInTheDocument();
            expect(screen.findByRole('button', {name: /Login/i})).toBeInTheDocument();
        });
    });

    test('logs out and redirects to home page', async () => {
        const user = userEvent.setup();

        Object.defineProperty(window.document, 'cookie', {
            writable: true,
            value: 'token=fake_token',
        });

        render(
            <MemoryRouter>
                <AuthProvider>
                    <App />
                </AuthProvider>
            </MemoryRouter>
        );

        await waitFor(() => {
            expect(screen.getByRole('link', { name: /Logout/i })).toBeInTheDocument();
        });

        const logoutLink = await screen.getByRole('link', { name: /^logout$/i });
        await user.click(logoutLink);

        await waitFor(() => {
            expect(screen.getByText("Let's Go Camping")).toBeInTheDocument();
            expect(screen.getByRole('link', {name: /Login/i})).toBeInTheDocument();
        });

        Object.defineProperty(window.document, 'cookie', {
            writable: true,
            value: '',
        });
    });

    test('navigates to search page from home page', async () => {
        const user = userEvent.setup();

        Object.defineProperty(window.document, 'cookie', {
            writable: true,
            value: 'token=fake_token',
        });

        render(
            <MemoryRouter>
                <AuthProvider>
                    <App />
                </AuthProvider>
            </MemoryRouter>
        );

        const searchLink = screen.getByRole('link', {name: /Search/i});
        await user.click(searchLink);

        waitFor(() => {
            expect(screen.findByTestId('search-button')).toBeInTheDocument();
        });

        Object.defineProperty(window.document, 'cookie', {
            writable: true,
            value: '',
        });
    });
});
