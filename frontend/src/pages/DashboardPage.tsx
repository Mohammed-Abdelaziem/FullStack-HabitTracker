import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { Target, TrendingUp, Calendar, CheckCircle } from 'lucide-react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Badge } from '../components/ui/badge';
import { LoadingSpinner } from '../components/ui/loading-spinner';
import { useAuth } from '../contexts/AuthContext';
import { habitsApi, ongoingHabitsApi } from '../lib/api';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();

  const { data: habits, isLoading: habitsLoading } = useQuery({
    queryKey: ['habits'],
    queryFn: habitsApi.getHabits,
  });

  const { data: ongoingHabits, isLoading: ongoingLoading } = useQuery({
    queryKey: ['ongoing-habits'],
    queryFn: ongoingHabitsApi.getOngoingHabits,
  });

  const { data: progress, isLoading: progressLoading } = useQuery({
    queryKey: ['progress'],
    queryFn: () => habitsApi.getProgress(),
  });

  const isLoading = habitsLoading || ongoingLoading || progressLoading;

  const stats = {
    totalHabits: habits?.length || 0,
    activeHabits: ongoingHabits?.filter(h => h.status === 'IN_PROGRESS' || h.status === 'STARTED').length || 0,
    completedHabits: ongoingHabits?.filter(h => h.status === 'COMPLETED').length || 0,
    overallProgress: progress?.progressPercentage || '0%',
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
            <h1 className="text-3xl font-bold text-gray-900">
              Welcome back, {user?.name}!
            </h1>
            <p className="text-gray-600 mt-1">
              Here's your habit tracking overview
            </p>
          </div>
          <Badge variant="secondary" className="text-sm">
            {user?.role}
          </Badge>
        </div>
      </motion.div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.1 }}
        >
          <Card className="hover:shadow-lg transition-shadow">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Total Habits</CardTitle>
              <Target className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.totalHabits}</div>
              <p className="text-xs text-muted-foreground">
                Habits created
              </p>
            </CardContent>
          </Card>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.2 }}
        >
          <Card className="hover:shadow-lg transition-shadow">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Active Habits</CardTitle>
              <Calendar className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-blue-600">{stats.activeHabits}</div>
              <p className="text-xs text-muted-foreground">
                Currently tracking
              </p>
            </CardContent>
          </Card>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.3 }}
        >
          <Card className="hover:shadow-lg transition-shadow">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Completed</CardTitle>
              <CheckCircle className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-green-600">{stats.completedHabits}</div>
              <p className="text-xs text-muted-foreground">
                Habits completed
              </p>
            </CardContent>
          </Card>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5, delay: 0.4 }}
        >
          <Card className="hover:shadow-lg transition-shadow">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium">Overall Progress</CardTitle>
              <TrendingUp className="h-4 w-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold text-purple-600">{stats.overallProgress}</div>
              <p className="text-xs text-muted-foreground">
                Success rate
              </p>
            </CardContent>
          </Card>
        </motion.div>
      </div>

      {/* Recent Activity */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <motion.div
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.5, delay: 0.5 }}
        >
          <Card>
            <CardHeader>
              <CardTitle>Recent Habits</CardTitle>
              <CardDescription>Your latest created habits</CardDescription>
            </CardHeader>
            <CardContent>
              {habits && habits.length > 0 ? (
                <div className="space-y-3">
                  {habits.slice(0, 5).map((habit) => (
                    <div key={habit.id} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                      <div>
                        <p className="font-medium">{habit.name}</p>
                        <p className="text-sm text-gray-600">
                          {habit.frequency} • Target: {habit.target}
                        </p>
                      </div>
                      <Badge variant="outline">
                        {habit.progress.toFixed(0)}%
                      </Badge>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-gray-500 text-center py-4">No habits created yet</p>
              )}
            </CardContent>
          </Card>
        </motion.div>

        <motion.div
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.5, delay: 0.6 }}
        >
          <Card>
            <CardHeader>
              <CardTitle>Active Tracking</CardTitle>
              <CardDescription>Currently ongoing habit sessions</CardDescription>
            </CardHeader>
            <CardContent>
              {ongoingHabits && ongoingHabits.length > 0 ? (
                <div className="space-y-3">
                  {ongoingHabits
                    .filter(h => h.status === 'IN_PROGRESS' || h.status === 'STARTED')
                    .slice(0, 5)
                    .map((habit) => (
                      <div key={habit.ongoingHabitId} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg">
                        <div>
                          <p className="font-medium">{habit.habitName}</p>
                          <p className="text-sm text-gray-600">
                            Progress: {habit.counter}/{habit.target}
                          </p>
                        </div>
                        <Badge 
                          variant={habit.status === 'IN_PROGRESS' ? 'default' : 'secondary'}
                        >
                          {habit.status.replace('_', ' ')}
                        </Badge>
                      </div>
                    ))}
                </div>
              ) : (
                <p className="text-gray-500 text-center py-4">No active tracking sessions</p>
              )}
            </CardContent>
          </Card>
        </motion.div>
      </div>
    </div>
  );
};