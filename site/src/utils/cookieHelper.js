import Cookies from 'universal-cookie';

const cookies = new Cookies(null, { path: '/' });

export const setCookie = (name, value= {}) => {
  cookies.set(name, value);
};

export const getCookie = (name) => {
  return cookies.get(name);
};

export const removeCookie = (name) => {
  cookies.remove(name);
};