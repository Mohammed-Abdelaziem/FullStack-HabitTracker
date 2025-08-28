// User types
export interface User {
  userId: string;
  name: string;
  email: string;
  phoneNumber: string;
  role: 'USER' | 'ADMIN';
}

// Auth types
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

// Habit types
export interface Habit {
  id: string;
  name: string;
  frequency: 'DAILY' | 'WEEKLY' | 'MONTHLY';
  target: number;
  progress: number;
}

export interface HabitDetails extends Habit {
  userId: string;
  createdAt: string;
  updatedAt: string;
}

export interface HabitCreateRequest {
  name: string;
  frequency: 'DAILY' | 'WEEKLY' | 'MONTHLY';
  target: number;
}

export interface HabitUpdateRequest {
  name: string;
  frequency: 'DAILY' | 'WEEKLY' | 'MONTHLY';
  target: number;
}

// Ongoing Habit types
export interface OngoingHabit {
  ongoingHabitId: string;
  habitId: string;
  habitName: string;
  target: number;
  counter: number;
  status: 'NOT_STARTED' | 'STARTED' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';
  startDate: string;
  endDate?: string;
}

export interface OngoingHabitCreateRequest {
  habitId: string;
  startDate: string;
}

// Progress types
export interface Progress {
  progressPercentage: string;
  habitId?: string;
  status?: string;
}