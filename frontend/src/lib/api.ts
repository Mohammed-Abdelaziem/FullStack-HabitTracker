import axios, { AxiosResponse } from 'axios';
import Cookies from 'js-cookie';
import { 
  User, 
  Habit, 
  HabitDetails, 
  OngoingHabit, 
  Progress,
  LoginRequest, 
  RegisterRequest, 
  HabitCreateRequest, 
  HabitUpdateRequest,
  OngoingHabitCreateRequest 
} from '../types/api';

const API_BASE_URL = 'http://localhost:8081';

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token
api.interceptors.request.use((config) => {
  const token = Cookies.get('auth-token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      Cookies.remove('auth-token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Auth API
export const authApi = {
  login: async (credentials: LoginRequest): Promise<string> => {
    const response = await api.post('/auth/login', credentials);
    return response.data;
  },

  register: async (userData: RegisterRequest): Promise<User> => {
    const response = await api.post('/auth/register', userData);
    return response.data;
  },

  logout: () => {
    Cookies.remove('auth-token');
  },
};

// User API
export const userApi = {
  getCurrentUser: async (): Promise<User> => {
    const response = await api.get('/user');
    return response.data;
  },

  updateCurrentUser: async (userData: Partial<User>): Promise<{ message: string }> => {
    const response = await api.patch('/user', userData);
    return response.data;
  },

  deleteCurrentUser: async (): Promise<{ message: string }> => {
    const response = await api.delete('/user');
    return response.data;
  },

  getAllUsers: async (): Promise<User[]> => {
    const response = await api.get('/admin/users');
    return response.data;
  },

  searchUserByPhone: async (phone: string): Promise<User> => {
    const response = await api.get(`/admin/search/phone?phone=${phone}`);
    return response.data;
  },
};

// Habits API
export const habitsApi = {
  getHabits: async (): Promise<Habit[]> => {
    const response = await api.get('/habits');
    return response.data;
  },

  getHabitById: async (id: string): Promise<HabitDetails> => {
    const response = await api.get(`/habits/${id}`);
    return response.data;
  },

  createHabit: async (habit: HabitCreateRequest): Promise<Habit> => {
    const response = await api.post('/habits', habit);
    return response.data;
  },

  updateHabit: async (id: string, habit: HabitUpdateRequest): Promise<{ message: string; name: string; target: number; frequency: string }> => {
    const response = await api.patch(`/habits/${id}`, habit);
    return response.data;
  },

  deleteHabit: async (id: string): Promise<{ message: string }> => {
    const response = await api.delete(`/habits/${id}`);
    return response.data;
  },

  getProgress: async (habitId?: string, status?: string): Promise<Progress> => {
    const params = new URLSearchParams();
    if (habitId) params.append('habitId', habitId);
    if (status) params.append('status', status);
    
    const response = await api.get(`/progress?${params.toString()}`);
    return response.data;
  },
};

// Ongoing Habits API
export const ongoingHabitsApi = {
  getOngoingHabits: async (): Promise<OngoingHabit[]> => {
    const response = await api.get('/ongoing-habit');
    return response.data;
  },

  getOngoingHabitById: async (id: string): Promise<OngoingHabit> => {
    const response = await api.get(`/ongoing-habit/${id}`);
    return response.data;
  },

  createOngoingHabit: async (data: OngoingHabitCreateRequest): Promise<OngoingHabit> => {
    const response = await api.post('/ongoing-habit', data);
    return response.data;
  },

  updateOngoingHabit: async (id: string, data: { startDate: string }): Promise<OngoingHabit> => {
    const response = await api.put(`/ongoing-habit/${id}`, data);
    return response.data;
  },

  deleteOngoingHabit: async (id: string): Promise<void> => {
    await api.delete(`/ongoing-habit/${id}`);
  },

  checkOffHabit: async (id: string, increment: number = 1): Promise<OngoingHabit> => {
    const response = await api.post(`/ongoing-habit/${id}/check-off`, { increment });
    return response.data;
  },
};

export default api;