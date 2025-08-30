import React, { useEffect, useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Plus, Target } from "lucide-react";
import { apiClient } from "../utils/api";
import { HabitCard } from "../components/habits/HabitCard";
import { HabitForm } from "../components/habits/HabitForm";
import { Modal } from "../components/ui/Modal";
import { LoadingSpinner } from "../components/ui/LoadingSpinner";
import type { Habit, CreateHabitRequest } from "../types";
import toast from "react-hot-toast";

export const MyHabitsPage: React.FC = () => {
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [isViewModalOpen, setIsViewModalOpen] = useState(false);
  const [selectedHabit, setSelectedHabit] = useState<Habit | null>(null);

  const queryClient = useQueryClient();

  const { data: habits = [], isLoading } = useQuery<Habit[]>({
    queryKey: ["habits"],
    queryFn: () => apiClient.getHabits(),
    placeholderData: [], // avoids undefined during first render
    select: (d) => {
      // log what comes back
      console.log("GET /habits ->", d);
      return d ?? [];
    },
  });

  useEffect(() => {
    console.log("habits after query:", habits);
  }, [habits]);

  const createMutation = useMutation({
    mutationFn: (data: CreateHabitRequest) => apiClient.createHabit(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["habits"] });
      setIsCreateModalOpen(false);
      toast.success("Habit created successfully!");
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: CreateHabitRequest }) =>
      apiClient.updateHabit(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["habits"] });
      setIsEditModalOpen(false);
      setSelectedHabit(null);
      toast.success("Habit updated successfully!");
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: string) => apiClient.deleteHabit(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["habits"] });
      toast.success("Habit deleted successfully!");
    },
  });

  const handleDelete = (id: string) => {
    deleteMutation.mutate(id);
  };
  const handleCreate = (data: CreateHabitRequest) => {
    createMutation.mutate(data);
  };

  const handleEdit = (habit: Habit) => {
    setSelectedHabit(habit);
    setIsEditModalOpen(true);
  };

  const handleUpdate = (data: CreateHabitRequest) => {
    if (selectedHabit) {
      updateMutation.mutate({ id: selectedHabit.id, data });
    }
  };

  const handleView = (habit: Habit) => {
    setSelectedHabit(habit);
    setIsViewModalOpen(true);
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  return (
    <div className="space-y-8">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
            My Habits
          </h1>
          <p className="mt-2 text-gray-600 dark:text-gray-400">
            Manage and track your personal habits
          </p>
        </div>
        <button
          onClick={() => setIsCreateModalOpen(true)}
          className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-lg shadow-sm text-white bg-blue-600 hover:bg-blue-700 transition-colors"
        >
          <Plus className="h-4 w-4 mr-2" />
          Create Habit
        </button>
      </div>

      {habits.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {habits.map((habit) => (
            <HabitCard
              key={habit.id}
              habit={habit}
              onEdit={handleEdit}
              onDelete={handleDelete}
              onView={handleView}
            />
          ))}
        </div>
      ) : (
        <div className="text-center py-12">
          <Target className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-2 text-sm font-medium text-gray-900 dark:text-white">
            No habits
          </h3>
          <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
            Get started by creating a new habit.
          </p>
          <div className="mt-6">
            <button
              onClick={() => setIsCreateModalOpen(true)}
              className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-lg text-white bg-blue-600 hover:bg-blue-700 transition-colors"
            >
              <Plus className="h-4 w-4 mr-2" />
              Create Habit
            </button>
          </div>
        </div>
      )}

      {/* Create Habit Modal */}
      <Modal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        title="Create New Habit"
      >
        <HabitForm
          onSubmit={handleCreate}
          onCancel={() => setIsCreateModalOpen(false)}
          isLoading={createMutation.isPending}
        />
      </Modal>

      {/* Edit Habit Modal */}
      <Modal
        isOpen={isEditModalOpen}
        onClose={() => {
          setIsEditModalOpen(false);
          setSelectedHabit(null);
        }}
        title="Edit Habit"
      >
        {selectedHabit && (
          <HabitForm
            habit={selectedHabit}
            onSubmit={handleUpdate}
            onCancel={() => {
              setIsEditModalOpen(false);
              setSelectedHabit(null);
            }}
            isLoading={updateMutation.isPending}
          />
        )}
      </Modal>

      {/* View Habit Modal */}
      <Modal
        isOpen={isViewModalOpen}
        onClose={() => {
          setIsViewModalOpen(false);
          setSelectedHabit(null);
        }}
        title="Habit Details"
      >
        {selectedHabit && (
          <div className="space-y-6">
            <div>
              <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
                {selectedHabit.name}
              </h3>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Frequency
                </label>
                <p className="text-gray-900 dark:text-white">
                  {selectedHabit.frequency}
                </p>
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Target
                </label>
                <p className="text-gray-900 dark:text-white">
                  {selectedHabit.target}
                </p>
              </div>
            </div>
            <div className="flex justify-end">
              <button
                onClick={() => {
                  setIsViewModalOpen(false);
                  setSelectedHabit(null);
                }}
                className="px-4 py-2 text-sm font-medium text-gray-700 dark:text-gray-300 bg-gray-100 dark:bg-gray-700 hover:bg-gray-200 dark:hover:bg-gray-600 rounded-lg transition-colors"
              >
                Close
              </button>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};
