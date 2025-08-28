import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { BarChart3, TrendingUp, Calendar, Target } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Badge } from '@/components/ui/badge';
import { LoadingSpinner } from '@/components/ui/loading-spinner';
import { Label } from '@/components/ui/label';
import { Button } from '@/components/ui/button';
import { habitsApi } from '@/lib/api';

export const ProgressPage: React.FC = () => {
  const [selectedHabitId, setSelectedHabitId] = useState<string>('');
  const [selectedStatus, setSelectedStatus] = useState<string>('');

  const { data: habits, isLoading: habitsLoading } = useQuery({
    queryKey: ['habits'],
    queryFn: habitsApi.getHabits,
  });

  const { data: progress, isLoading: progressLoading } = useQuery({
    queryKey: ['progress', selectedHabitId, selectedStatus],
    queryFn: () => habitsApi.getProgress(
      selectedHabitId || undefined, 
      selectedStatus || undefined
    ),
  });

  const isLoading = habitsLoading || progressLoading;

  const clearFilters = () => {
    setSelectedHabitId('');
    setSelectedStatus('');
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center h-64">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
      >
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-3xl font-bold text-gray-900">Progress Analytics</h1>
            <p className="text-gray-600 mt-1">
              Track your habit completion rates and overall progress
            </p>
          </div>
          <BarChart3 className="w-8 h-8 text-primary" />
        </div>
      </motion.div>

      {/* Filters */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.1 }}
      >
        <Card>
          <CardHeader>
            <CardTitle className="text-lg">Filters</CardTitle>
            <CardDescription>
              Filter progress data by habit or status
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="flex flex-col sm:flex-row gap-4">
              <div className="flex-1">
                <Label htmlFor="habit-filter">Filter by Habit</Label>
                <Select value={selectedHabitId} onValueChange={setSelectedHabitId}>
                  <SelectTrigger>
                    <SelectValue placeholder="All habits" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="">All habits</SelectItem>
                    {habits?.map((habit) => (
                      <SelectItem key={habit.id} value={habit.id}>
                        {habit.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="flex-1">
                <Label htmlFor="status-filter">Filter by Status</Label>
                <Select value={selectedStatus} onValueChange={setSelectedStatus}>
                  <SelectTrigger>
                    <SelectValue placeholder="All statuses" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="">All statuses</SelectItem>
                    <SelectItem value="INCOMPLETE">Incomplete</SelectItem>
                    <SelectItem value="IN_PROGRESS">In Progress</SelectItem>
                    <SelectItem value="COMPLETED">Completed</SelectItem>
                  </SelectContent>
                </Select>
              </div>

              <div className="flex items-end">
                <Button variant="outline" onClick={clearFilters}>
                  Clear Filters
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>
      </motion.div>

      {/* Progress Display */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, delay: 0.2 }}
      >
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center space-x-2">
              <TrendingUp className="w-5 h-5" />
              <span>Progress Overview</span>
            </CardTitle>
            <CardDescription>
              {selectedHabitId || selectedStatus ? 'Filtered results' : 'Overall progress across all habits'}
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="text-center py-8">
              <div className="text-6xl font-bold text-primary mb-4">
                {progress?.progressPercentage || '0%'}
              </div>
              <p className="text-lg text-gray-600 mb-4">
                {selectedHabitId && selectedStatus 
                  ? `Progress for selected habit with ${selectedStatus.toLowerCase()} status`
                  : selectedHabitId 
                    ? 'Progress for selected habit'
                    : selectedStatus
                      ? `Overall ${selectedStatus.toLowerCase()} rate`
                      : 'Overall completion rate'
                }
              </p>
              
              {progress && (
                <div className="flex justify-center space-x-4 text-sm text-gray-500">
                  {progress.habitId && (
                    <div className="flex items-center space-x-1">
                      <Target className="w-4 h-4" />
                      <span>Specific Habit</span>
                    </div>
                  )}
                  {progress.status && (
                    <div className="flex items-center space-x-1">
                      <Badge variant="outline" className="text-xs">
                        {progress.status}
                      </Badge>
                    </div>
                  )}
                </div>
              )}
            </div>
          </CardContent>
        </Card>
      </motion.div>

      {/* Quick Stats */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.3 }}
        >
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Total Habits</CardTitle>
              <Target className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{habits?.length || 0}</div>
              <p className="text-xs text-muted-foreground">
                Created habits
              </p>
            </CardContent>
          </Card>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.4 }}
        >
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Active Sessions</CardTitle>
              <Calendar className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-blue-600">
                {ongoingHabits?.filter(h => h.status === 'IN_PROGRESS' || h.status === 'STARTED').length || 0}
              </div>
              <p className="text-xs text-muted-foreground">
                Currently tracking
              </p>
            </CardContent>
          </Card>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.5 }}
        >
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Completed</CardTitle>
              <CheckCircle className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-green-600">
                {ongoingHabits?.filter(h => h.status === 'COMPLETED').length || 0}
              </div>
              <p className="text-xs text-muted-foreground">
                Successfully finished
              </p>
            </CardContent>
          </Card>
        </motion.div>
      </div>
    </div>
  );
};