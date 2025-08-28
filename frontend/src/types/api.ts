export interface User {
  userId: string;
  role: 'USER' | 'ADMIN';
  name: string;
  email: string;
  phoneNumber: string;
}

export interface Habit {
  id: string;
  name: string;
  target: number;
  frequency: 'DAILY' | 'WEEKLY' | 'MONTHLY';
  progress: number;
}

export interface HabitDetails extends Habit {
  status: 'NOT_STARTED' | 'IN_PROGRESS' | 'COMPLETED';
}

export interface OngoingHabit {
  ongoingHabitId: string;
  habitId: string;
  habitName: string;
  counter: number;
  status: 'NOT_STARTED' | 'STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  target: number;
}

export interface Progress {
  habitId?: string;
  status?: 'INCOMPLETE' | 'IN_PROGRESS' | 'COMPLETED';
  progressPercentage: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
  phoneNumber: string;
}

export interface HabitCreateRequest {
  name: string;
  frequency: 'DAILY' | 'WEEKLY' | 'MONTHLY';
  target: number;
}

export interface HabitUpdateRequest {
  name?: string;
  target?: number;
  frequency?: 'DAILY' | 'WEEKLY' | 'MONTHLY';
}

export interface OngoingHabitCreateRequest {
  habitId: string;
  startDate: string;
}

export interface ApiResponse<T> {
  data?: T;
  message?: string;
  error?: string;
}