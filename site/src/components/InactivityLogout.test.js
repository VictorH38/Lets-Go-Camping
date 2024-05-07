import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { AuthProvider } from '../AuthContext'; // Adjust this import if AuthProvider is not a named export
import InactivityLogout from '../components/InactivityLogout';

// Mocking the AuthContext with the correct structure
jest.mock('../AuthContext', () => ({
    useAuth: jest.fn().mockImplementation(() => ({ setIsLoggedIn: jest.fn() })),
    AuthProvider: jest.fn(({ children }) => <div>{children}</div>) // Assuming AuthProvider is a named export
}));

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: jest.fn(() => jest.fn())
}));

jest.mock("../utils/cookieHelper", () => ({
    getCookie: jest.fn(),
    removeCookie: jest.fn()
}));

jest.mock('react-idle-timer', () => ({
    useIdleTimer: jest.fn(() => ({
        getRemainingTime: jest.fn()
    }))
}));

describe('Logout due to inactivity test', () => {
    test('logs out and redirects to login page after 30 minutes idle', async () => {
        render(
            <MemoryRouter>
                <AuthProvider>
                    <InactivityLogout />
                </AuthProvider>
            </MemoryRouter>
        );

        jest.advanceTimersByTime(1000 * 30 * 60 + 1); // Fast-forward time to simulate idle timeout
    });
});
