import React, { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { motion } from 'framer-motion';
import { Play, Plus, CheckCircle, Calendar } from 'lucide-react';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../components/ui/card';
import { Badge } from '../components/ui/badge';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '../components/ui/dialog';
import { Input } from '../components/ui/input';
import { Label } from '../components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../components/ui/select';
import { LoadingSpinner } from '../components/ui/loading-spinner';
import { ongoingHabitsApi, habitsApi } from '../lib/api';
import { OngoingHabit } from '../types/api';
import toast from 'react-hot-toast';

export const OngoingPage: React.FC = () => {
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [selectedHabitId, setSelectedHabitId] = useState('');
  const [startDate, setStartDate] = useState(new Date().toISOString().split('T')[0]);
  const queryClient = useQueryClient();

  const { data: ongoingHabits, isLoading: ongoingLoading } = useQuery({
    queryKey: ['ongoing-habits'],
    queryFn: ongoingHabitsApi.getOngoingHabits,
  });

  const { data: habits, isLoading: habitsLoading } = useQuery({
    queryKey: ['habits'],
    queryFn: habitsApi.getHabits,
  });

  const createMutation = useMutation({
    mutationFn: ongoingHabitsApi.createOngoingHabit,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ongoing-habits'] });
      setIsCreateDialogOpen(false);
      setSelectedHabitId('');
      toast.success('Habit tracking started!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to start habit tracking');
    },
  });

  const checkOffMutation = useMutation({
    mutationFn: ({ id, increment }: { id: string; increment: number }) =>
      ongoingHabitsApi.checkOffHabit(id, increment),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ongoing-habits'] });
      toast.success('Progress updated!');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to update progress');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: ongoingHabitsApi.deleteOngoingHabit,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ongoing-habits'] });
      toast.success('Habit tracking stopped');
    },
    onError: (error: any) => {
      toast.error(error.response?.data?.message || 'Failed to stop tracking');
    },
  });

  const handleStartTracking = () => {
    if (selectedHabitId && startDate) {
      createMutation.mutate({
        habitId: selectedHabitId,
        startDate,
      });
    }
  };

  const handleCheckOff = (id: string) => {
    checkOffMutation.mutate({ id, increment: 1 });
  };

  const handleStopTracking = (id: string) => {
    if (confirm('Are you sure you want to stop tracking this habit?')) {
      deleteMutation.mutate(id);
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'COMPLETED': return 'bg-green-100 text-green-800';
      case 'IN_PROGRESS': return 'bg-blue-100 text-blue-800';
      case 'STARTED': return 'bg-yellow-100 text-yellow-800';
      case 'NOT_STARTED': return 'bg-gray-100 text-gray-800';
      case 'CANCELLED': return 'bg-red-100 text-red-800';
      default: return 'bg-gray-100 text-gray-800';
    }
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
    <div className="space-y-6">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="flex items-center justify-between"
      >
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Ongoing Habits</h1>
          <p className="text-gray-600 mt-1">
            Track your active habit sessions and progress
          </p>
        </div>
        
        <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
          <DialogTrigger asChild>
            <Button className="flex items-center space-x-2">
              <Play className="w-4 h-4" />
              <span>Start Tracking</span>
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Start Habit Tracking</DialogTitle>
              <DialogDescription>
                Choose a habit to start tracking
              </DialogDescription>
            </DialogHeader>
            <div className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="habit">Select Habit</Label>
                <Select value={selectedHabitId} onValueChange={setSelectedHabitId}>
                  <SelectTrigger>
                    <SelectValue placeholder="Choose a habit" />
                  </SelectTrigger>
                  <SelectContent>
                    {habits?.map((habit) => (
                      <SelectItem key={habit.id} value={habit.id}>
                        {habit.name} ({habit.frequency})
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="space-y-2">
                <Label htmlFor="startDate">Start Date</Label>
                <Input
                  id="startDate"
                  type="date"
                  value={startDate}
                  onChange={(e) => setStartDate(e.target.value)}
                />
              </div>

              <div className="flex justify-end space-x-2 pt-4">
                <Button
                  onClick={handleStartTracking}
                  disabled={!selectedHabitId || createMutation.isPending}
                >
                  {createMutation.isPending ? (
                    <>
                      <LoadingSpinner size="sm" className="mr-2" />
                      Starting...
                    </>
                  ) : (
                    'Start Tracking'
                  )}
                </Button>
              </div>
            </div>
          </DialogContent>
        </Dialog>
      </motion.div>

      {ongoingHabits && ongoingHabits.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {ongoingHabits.map((habit, index) => (
            <motion.div
              key={habit.ongoingHabitId}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.5, delay: index * 0.1 }}
            >
              <Card className="hover:shadow-lg transition-all duration-200">
                <CardHeader>
                  <div className="flex items-start justify-between">
                    <div>
                      <CardTitle className="text-lg">{habit.habitName}</CardTitle>
                      <CardDescription>
                        Progress: {habit.counter}/{habit.target}
                      </CardDescription>
                    </div>
                    <Badge className={getStatusColor(habit.status)}>
                      {habit.status.replace('_', ' ')}
                    </Badge>
                  </div>
                </CardHeader>
                <CardContent>
                  <div className="space-y-4">
                    <div className="w-full bg-gray-200 rounded-full h-3">
                      <div 
                        className="bg-primary h-3 rounded-full transition-all duration-300"
                        style={{ width: `${Math.min((habit.counter / habit.target) * 100, 100)}%` }}
                      />
                    </div>
                    
                    <div className="flex space-x-2">
                      {habit.status === 'STARTED' || habit.status === 'IN_PROGRESS' ? (
                        <Button
                          onClick={() => handleCheckOff(habit.ongoingHabitId)}
                          disabled={checkOffMutation.isPending}
                          className="flex-1"
                        >
                          {checkOffMutation.isPending ? (
                            <LoadingSpinner size="sm" className="mr-2" />
                          ) : (
                            <CheckCircle className="w-4 h-4 mr-2" />
                          )}
                          Check Off
                        </Button>
                      ) : null}
                      
                      <Button
                        variant="outline"
                        onClick={() => handleStopTracking(habit.ongoingHabitId)}
                        disabled={deleteMutation.isPending}
                        className="flex-1"
                      >
                        Stop Tracking
                      </Button>
                    </div>
                  </div>
                </CardContent>
              </Card>
            </motion.div>
          ))}
        </div>
      ) : (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          transition={{ duration: 0.5 }}
          className="text-center py-12"
        >
          <Play className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-900 mb-2">No ongoing habits</h3>
          <p className="text-gray-600 mb-6">
            Start tracking a habit to see your progress here
          </p>
          <Button onClick={() => setIsCreateDialogOpen(true)}>
            <Play className="w-4 h-4 mr-2" />
            Start Tracking
          </Button>
        </motion.div>
      )}
    </div>
  );
};