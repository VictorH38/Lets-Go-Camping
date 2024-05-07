import Cookies from 'universal-cookie';
import { setCookie, getCookie, removeCookie } from './cookieHelper';

jest.mock('universal-cookie');

describe('Cookie Helper Tests', () => {
    afterEach(() => {
        jest.clearAllMocks();
    });

    test('sets a cookie with provided value', () => {
        setCookie('testCookie', { key: 'value' });
        expect(Cookies.prototype.set).toHaveBeenCalledWith('testCookie', { key: 'value' });
    });

    test('sets a cookie with default value', () => {
        setCookie('testCookie');
        expect(Cookies.prototype.set).toHaveBeenCalledWith('testCookie', {});
    });

    test('gets a cookie', () => {
        Cookies.prototype.get.mockReturnValue({ key: 'value' });
        expect(getCookie('testCookie')).toEqual({ key: 'value' });
    });

    test('removes a cookie', () => {
        removeCookie('testCookie');
        expect(Cookies.prototype.remove).toHaveBeenCalledWith('testCookie');
    });
});
