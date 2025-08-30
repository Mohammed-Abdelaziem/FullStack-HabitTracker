export interface User {
  id: string;
  name: string;
  email: string;
  phoneNumber: string;
  role: "USER" | "ADMIN";
  createdAt: string;
  updatedAt: string;
}

export interface Habit {
  id: string;
  name: string;
  target: number;
  frequency: "DAILY" | "WEEKLY" | "MONTHLY";
  progress: number;
}

export interface OngoingHabit {
  ongoingHabitId: string;
  habitId: string;
  userId: string;
  counter: number;
  target: number;
  status: "ACTIVE" | "COMPLETED" | "PAUSED" | "STARTED" | "IN_PROGRESS";
  startDate: string;
  habit?: Habit;
}

export interface ProgressData {
  progressPercentage: string; // "0%" from API
  habitName?: string; // optional, if API provides
  currentStreak?: number; // optional, if API provides later
  longestStreak?: number; // optional, if API provides later
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface CreateHabitRequest {
  name: string;
  frequency: "DAILY" | "WEEKLY" | "MONTHLY";
  target: number;
}

export interface UpdateHabitRequest extends Partial<CreateHabitRequest> {}

export interface CreateOngoingHabitRequest {
  habitId: string;
  startDate: string; // "YYYY-MM-DD"
}

export interface UpdateOngoingHabitRequest {
  counter?: number;
  target?: number;
  status?: "ACTIVE" | "COMPLETED" | "PAUSED";
}
