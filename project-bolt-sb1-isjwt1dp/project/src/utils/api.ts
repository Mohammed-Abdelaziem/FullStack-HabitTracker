import toast from "react-hot-toast";
import type {
  User,
  Habit,
  OngoingHabit,
  ProgressData,
  AuthResponse,
  CreateHabitRequest,
  UpdateHabitRequest,
  CreateOngoingHabitRequest,
  UpdateOngoingHabitRequest,
} from "../types";

const BASE_URL = "http://localhost:8081";

class ApiClient {
  private getHeaders() {
    const token = localStorage.getItem("token");
    return {
      "Content-Type": "application/json",
      ...(token && { Authorization: `Bearer ${token}` }),
    };
  }

  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<T> {
    try {
      const response = await fetch(`${BASE_URL}${endpoint}`, {
        ...options,
        headers: {
          ...this.getHeaders(),
          ...options.headers,
        },
      });

      if (!response.ok) {
        const errorData = await response
          .json()
          .catch(() => ({ message: "An error occurred" }));
        throw new Error(errorData.message || `HTTP ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      const message =
        error instanceof Error ? error.message : "Network error occurred";
      toast.error(message);
      throw error;
    }
  }

  // Auth endpoints
  async login(email: string, password: string): Promise<AuthResponse> {
    return this.request<AuthResponse>("/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email, password }),
    });
  }
  async signup(
    name: string,
    email: string,
    password: string,
    phoneNumber: string
  ): Promise<AuthResponse> {
    return this.request<AuthResponse>("/auth/register", {
      method: "POST",
      body: JSON.stringify({ name, email, password, phoneNumber }),
    });
  }

  // User endpoints
  async getUser(): Promise<User> {
    return this.request<User>("/user");
  }

  async updateUser(updates: Partial<User>): Promise<User> {
    return this.request<User>("/user", {
      method: "PATCH",
      body: JSON.stringify(updates),
    });
  }

  async deleteUser(): Promise<{ message: string }> {
    return this.request<{ message: string }>("/user", {
      method: "DELETE",
    });
  }

  // Habits endpoints
  async getHabits(): Promise<Habit[]> {
    return this.request<Habit[]>("/habits");
  }

  async getHabit(id: string): Promise<Habit> {
    return this.request<Habit>(`/habits/${id}`);
  }

  async createHabit(habit: CreateHabitRequest): Promise<Habit> {
    return this.request<Habit>("/habits", {
      method: "POST",
      body: JSON.stringify(habit),
    });
  }

  async updateHabit(id: string, updates: UpdateHabitRequest): Promise<Habit> {
    return this.request<Habit>(`/habits/${id}`, {
      method: "PATCH",
      body: JSON.stringify(updates),
    });
  }

  async deleteHabit(id: string): Promise<void> {
    return this.request<void>(`/habits/${id}`, {
      method: "DELETE",
    });
  }

  // Ongoing habits endpoints
  async getOngoingHabits(): Promise<OngoingHabit[]> {
    return this.request<OngoingHabit[]>("/ongoing-habit");
  }

  async createOngoingHabit(
    ongoingHabit: CreateOngoingHabitRequest
  ): Promise<OngoingHabit> {
    return this.request<OngoingHabit>("/ongoing-habit", {
      method: "POST",
      body: JSON.stringify(ongoingHabit),
    });
  }

  async updateOngoingHabit(
    id: string,
    updates: UpdateOngoingHabitRequest
  ): Promise<OngoingHabit> {
    return this.request<OngoingHabit>(`/ongoing-habit/${id}`, {
      method: "PUT",
      body: JSON.stringify(updates),
    });
  }

  async deleteOngoingHabit(id: string): Promise<void> {
    return this.request<void>(`/ongoing-habit/${id}`, {
      method: "DELETE",
    });
  }
  async checkOffHabit(id: string, increment = 1): Promise<OngoingHabit> {
    return this.request<OngoingHabit>(`/ongoing-habit/${id}/check-off`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ increment }),
    });
  }

  // Progress endpoints
  async getProgress(filters?: Record<string, string>): Promise<ProgressData> {
    const params = new URLSearchParams(filters);
    return this.request<ProgressData>(`/progress?${params}`);
  }

  // Admin endpoints
  async getUsers(
    page: number = 0,
    size: number = 20
  ): Promise<{ users: User[]; total: number }> {
    const response = await this.request<{ users: User[]; total: number }>(
      `/admin/users?page=${page}&size=${size}`
    );
    return response;
  }
  async searchUserByPhone(phone: string): Promise<User[]> {
    return this.request<User[]>(`/admin/search/phone?phone=${phone}`);
  }
}

export const apiClient = new ApiClient();
