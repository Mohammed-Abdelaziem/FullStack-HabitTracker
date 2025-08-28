import Cookies from 'js-cookie';
import { User } from '@/types/api';

export const AUTH_TOKEN_KEY = 'auth-token';

export const authStorage = {
  setToken: (token: string) => {
    Cookies.set(AUTH_TOKEN_KEY, token, { 
      expires: 7, // 7 days
      secure: process.env.NODE_ENV === 'production',
      sameSite: 'strict'
    });
  },

  getToken: (): string | undefined => {
    return Cookies.get(AUTH_TOKEN_KEY);
  },

  removeToken: () => {
    Cookies.remove(AUTH_TOKEN_KEY);
  },

  isAuthenticated: (): boolean => {
    return !!Cookies.get(AUTH_TOKEN_KEY);
  },
};

export const isAdmin = (user: User | null): boolean => {
  return user?.role === 'ADMIN';
};