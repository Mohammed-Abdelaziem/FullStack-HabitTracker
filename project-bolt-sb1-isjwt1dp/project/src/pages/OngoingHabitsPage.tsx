import React, { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { Plus } from "lucide-react";
import { apiClient } from "../utils/api";
import { OngoingHabitCard } from "../components/habits/OngoingHabitCard";
import { OngoingHabitForm } from "../components/habits/OngoingHabitForm";
import { Modal } from "../components/ui/Modal";
import { LoadingSpinner } from "../components/ui/LoadingSpinner";
import type { OngoingHabit, CreateOngoingHabitRequest } from "../types";
import toast from "react-hot-toast";

export const OngoingHabitsPage: React.FC = () => {
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [selectedOngoingHabit, setSelectedOngoingHabit] =
    useState<OngoingHabit | null>(null);

  const queryClient = useQueryClient();

  const { data: ongoingHabits = [], isLoading: ongoingLoading } = useQuery({
    queryKey: ["ongoing-habits"],
    queryFn: () => apiClient.getOngoingHabits(),
  });

  const { data: habits = [], isLoading: habitsLoading } = useQuery({
    queryKey: ["habits"],
    queryFn: apiClient.getHabits,
  });

  const createMutation = useMutation({
    mutationFn: (data: CreateOngoingHabitRequest) =>
      apiClient.createOngoingHabit(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["ongoing-habits"] });
      setIsCreateModalOpen(false);
      toast.success("Ongoing habit created successfully!");
    },
  });
  const handleCreate = (data: CreateOngoingHabitRequest) => {
    createMutation.mutate(data);
  };

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: any }) =>
      apiClient.updateOngoingHabit(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["ongoing-habits"] });
      setIsEditModalOpen(false);
      setSelectedOngoingHabit(null);
      toast.success("Ongoing habit updated successfully!");
    },
  });

  const checkOffMutation = useMutation({
    mutationFn: ({ id, increment }: { id: string; increment: number }) =>
      apiClient.checkOffHabit(id, increment),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["ongoing-habits"] });
      toast.success("Habit checked off!");
    },
  });

  const handleEdit = (ongoingHabit: OngoingHabit) => {
    setSelectedOngoingHabit(ongoingHabit);
    setIsEditModalOpen(true);
  };

  const handleUpdate = (data: any) => {
    if (selectedOngoingHabit) {
      updateMutation.mutate({ id: selectedOngoingHabit.ongoingHabitId, data });
    }
  };
  const deleteMutation = useMutation({
    mutationFn: (id: string) => apiClient.deleteOngoingHabit(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["ongoing-habits"] });
      toast.success("Ongoing habit deleted successfully!");
    },
  });

  const handleDelete = (id: string) => {
    console.log("Deleting habit ID:", id); // optional check
    deleteMutation.mutate(id);
  };

  const handleCheckOff = (id: string) => {
    checkOffMutation.mutate({ id, increment: 1 });
  };

  const isLoading = ongoingLoading || habitsLoading;

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
            Ongoing Habits
          </h1>
          <p className="mt-2 text-gray-600 dark:text-gray-400">
            Track your active habit sessions
          </p>
        </div>
        <button
          onClick={() => setIsCreateModalOpen(true)}
          className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-lg shadow-sm text-white bg-blue-600 hover:bg-blue-700 transition-colors"
        >
          <Plus className="h-4 w-4 mr-2" />
          Start Tracking
        </button>
      </div>

      {ongoingHabits.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {ongoingHabits.map((ongoingHabit, index) => (
            <OngoingHabitCard
              key={ongoingHabit.ongoingHabitId}
              ongoingHabit={ongoingHabit}
              onCheckOff={handleCheckOff}
              onEdit={handleEdit}
              onDelete={handleDelete}
              index={index}
            />
          ))}
        </div>
      ) : (
        <div className="text-center py-12">
          <Plus className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-2 text-sm font-medium text-gray-900 dark:text-white">
            No ongoing habits
          </h3>
          <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
            Start tracking a habit to see your progress.
          </p>
          <div className="mt-6">
            <button
              onClick={() => setIsCreateModalOpen(true)}
              className="inline-flex items-center px-4 py-2 border border-transparent shadow-sm text-sm font-medium rounded-lg text-white bg-blue-600 hover:bg-blue-700 transition-colors"
            >
              <Plus className="h-4 w-4 mr-2" />
              Start Tracking
            </button>
          </div>
        </div>
      )}

      {/* Create Modal */}
      <Modal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
        title="Start Tracking Habit"
      >
        <OngoingHabitForm
          habits={habits}
          onSubmit={handleCreate}
          onCancel={() => setIsCreateModalOpen(false)}
          isLoading={createMutation.isPending}
        />
      </Modal>

      {/* Edit Modal */}
      <Modal
        isOpen={isEditModalOpen}
        onClose={() => {
          setIsEditModalOpen(false);
          setSelectedOngoingHabit(null);
        }}
        title="Edit Ongoing Habit"
      >
        {selectedOngoingHabit && (
          <OngoingHabitForm
            ongoingHabit={selectedOngoingHabit}
            habits={habits}
            onSubmit={handleUpdate}
            onCancel={() => {
              setIsEditModalOpen(false);
              setSelectedOngoingHabit(null);
            }}
            isLoading={updateMutation.isPending}
          />
        )}
      </Modal>
    </div>
  );
};
