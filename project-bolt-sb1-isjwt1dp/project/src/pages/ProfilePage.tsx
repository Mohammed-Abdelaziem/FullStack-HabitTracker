import React, { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { User, Mail, Phone, Calendar, Save, Trash2 } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { apiClient } from "../utils/api";
import { LoadingSpinner } from "../components/ui/LoadingSpinner";
import toast from "react-hot-toast";

export const ProfilePage: React.FC = () => {
  const { user, logout } = useAuth();
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    name: user?.name || "",
    email: user?.email || "",
    phoneNumber: user?.phoneNumber || "",
  });

  const queryClient = useQueryClient();

  const updateMutation = useMutation({
    mutationFn: (data: Partial<typeof formData>) => apiClient.updateUser(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["user"] });
      setIsEditing(false);
      toast.success("Profile updated successfully!");
    },
  });

  const deleteMutation = useMutation({
    mutationFn: () => apiClient.deleteUser(), // make sure to call the function
    onSuccess: () => {
      logout();
      toast.success("Account deleted successfully");
    },
    onError: (error) => {
      toast.error("Failed to delete account");
      console.error(error);
    },
  });

  const handleDeleteAccount = () => {
    deleteMutation.mutate(); // <-- trigger the request
  };

  const handleSave = () => {
    updateMutation.mutate(formData);
  };

  const handleCancel = () => {
    setFormData({
      name: user?.name || "",
      email: user?.email || "",
      phoneNumber: user?.phoneNumber || "",
    });
    setIsEditing(false);
  };

  if (!user) {
    return null;
  }

  return (
    <div className="max-w-4xl mx-auto space-y-8">
      <div>
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
          Profile
        </h1>
        <p className="mt-2 text-gray-600 dark:text-gray-400">
          Manage your account settings and personal information
        </p>
      </div>

      <div className="bg-white dark:bg-gray-800 shadow-sm rounded-xl border border-gray-200 dark:border-gray-700">
        <div className="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
          <div className="flex items-center justify-between">
            <h3 className="text-lg font-medium text-gray-900 dark:text-white">
              Personal Information
            </h3>
            {!isEditing ? (
              <button
                onClick={() => setIsEditing(true)}
                className="inline-flex items-center px-3 py-2 border border-gray-300 dark:border-gray-600 text-sm font-medium rounded-lg text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
              >
                Edit
              </button>
            ) : (
              <div className="flex space-x-3">
                <button
                  onClick={handleCancel}
                  className="inline-flex items-center px-3 py-2 border border-gray-300 dark:border-gray-600 text-sm font-medium rounded-lg text-gray-700 dark:text-gray-300 bg-white dark:bg-gray-800 hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                >
                  Cancel
                </button>
                <button
                  onClick={handleSave}
                  disabled={updateMutation.isPending}
                  className="inline-flex items-center px-3 py-2 border border-transparent text-sm font-medium rounded-lg text-white bg-blue-600 hover:bg-blue-700 transition-colors disabled:opacity-50"
                >
                  {updateMutation.isPending ? (
                    <LoadingSpinner size="sm" className="mr-2" />
                  ) : (
                    <Save className="h-4 w-4 mr-2" />
                  )}
                  Save
                </button>
              </div>
            )}
          </div>
        </div>

        <div className="px-6 py-6">
          <div className="grid grid-cols-1 gap-6">
            <div className="flex items-center space-x-4">
              <div className="flex-shrink-0">
                <div className="h-16 w-16 bg-blue-100 dark:bg-blue-900/20 rounded-full flex items-center justify-center">
                  <User className="h-8 w-8 text-blue-600" />
                </div>
              </div>
              <div className="flex-1">
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Full Name
                </label>
                {isEditing ? (
                  <input
                    type="text"
                    value={formData.name}
                    onChange={(e) =>
                      setFormData({ ...formData, name: e.target.value })
                    }
                    className="block w-full rounded-lg border border-gray-300 dark:border-gray-600 px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
                  />
                ) : (
                  <p className="text-gray-900 dark:text-white">{user.name}</p>
                )}
              </div>
            </div>

            <div className="flex items-center space-x-4">
              <div className="flex-shrink-0">
                <div className="h-16 w-16 bg-green-100 dark:bg-green-900/20 rounded-full flex items-center justify-center">
                  <Mail className="h-8 w-8 text-green-600" />
                </div>
              </div>
              <div className="flex-1">
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Email Address
                </label>
                {isEditing ? (
                  <input
                    type="email"
                    value={formData.email}
                    onChange={(e) =>
                      setFormData({ ...formData, email: e.target.value })
                    }
                    className="block w-full rounded-lg border border-gray-300 dark:border-gray-600 px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
                  />
                ) : (
                  <p className="text-gray-900 dark:text-white">{user.email}</p>
                )}
              </div>
            </div>

            <div className="flex items-center space-x-4">
              <div className="flex-shrink-0">
                <div className="h-16 w-16 bg-purple-100 dark:bg-purple-900/20 rounded-full flex items-center justify-center">
                  <Phone className="h-8 w-8 text-purple-600" />
                </div>
              </div>
              <div className="flex-1">
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Phone Number
                </label>
                {isEditing ? (
                  <input
                    type="tel"
                    value={formData.phoneNumber}
                    onChange={(e) =>
                      setFormData({ ...formData, phoneNumber: e.target.value })
                    }
                    className="block w-full rounded-lg border border-gray-300 dark:border-gray-600 px-3 py-2 focus:ring-2 focus:ring-blue-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
                  />
                ) : (
                  <p className="text-gray-900 dark:text-white">
                    {user.phoneNumber}
                  </p>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Danger Zone */}
      <div className="bg-white dark:bg-gray-800 shadow-sm rounded-xl border border-red-200 dark:border-red-800">
        <div className="px-6 py-4 border-b border-red-200 dark:border-red-800">
          <h3 className="text-lg font-medium text-red-900 dark:text-red-400">
            Danger Zone
          </h3>
        </div>
        <div className="px-6 py-6">
          <div className="flex items-center justify-between">
            <div>
              <h4 className="text-sm font-medium text-gray-900 dark:text-white">
                Delete Account
              </h4>
              <p className="text-sm text-gray-600 dark:text-gray-400">
                Permanently delete your account and all associated data. This
                action cannot be undone.
              </p>
            </div>
            <button
              onClick={handleDeleteAccount}
              disabled={deleteMutation.isPending}
              className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-lg text-white bg-red-600 hover:bg-red-700 transition-colors disabled:opacity-50"
            >
              {deleteMutation.isPending ? (
                <LoadingSpinner size="sm" className="mr-2" />
              ) : (
                <Trash2 className="h-4 w-4 mr-2" />
              )}
              Delete Account
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
