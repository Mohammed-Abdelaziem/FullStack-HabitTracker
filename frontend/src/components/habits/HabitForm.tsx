import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { LoadingSpinner } from '@/components/ui/loading-spinner';
import { HabitCreateRequest, HabitUpdateRequest, Habit } from '@/types/api';

const habitSchema = z.object({
  name: z.string().min(1, 'Habit name is required'),
  frequency: z.enum(['DAILY', 'WEEKLY', 'MONTHLY']),
  target: z.number().min(1, 'Target must be at least 1'),
});

type HabitFormData = z.infer<typeof habitSchema>;

interface HabitFormProps {
  initialData?: Habit;
  onSubmit: (data: HabitCreateRequest | HabitUpdateRequest) => void;
  isLoading?: boolean;
}

export const HabitForm: React.FC<HabitFormProps> = ({
  initialData,
  onSubmit,
  isLoading = false,
}) => {
  const {
    register,
    handleSubmit,
    setValue,
    watch,
    formState: { errors },
  } = useForm<HabitFormData>({
    resolver: zodResolver(habitSchema),
    defaultValues: initialData ? {
      name: initialData.name,
      frequency: initialData.frequency,
      target: initialData.target,
    } : {
      frequency: 'DAILY',
      target: 1,
    },
  });

  const frequency = watch('frequency');

  const handleFormSubmit = (data: HabitFormData) => {
    onSubmit(data);
  };

  return (
    <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
      <div className="space-y-2">
        <Label htmlFor="name">Habit Name</Label>
        <Input
          id="name"
          placeholder="e.g., Drink 8 glasses of water"
          {...register('name')}
          className={errors.name ? 'border-red-500' : ''}
        />
        {errors.name && (
          <p className="text-sm text-red-500">{errors.name.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="frequency">Frequency</Label>
        <Select
          value={frequency}
          onValueChange={(value) => setValue('frequency', value as 'DAILY' | 'WEEKLY' | 'MONTHLY')}
        >
          <SelectTrigger>
            <SelectValue placeholder="Select frequency" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="DAILY">Daily</SelectItem>
            <SelectItem value="WEEKLY">Weekly</SelectItem>
            <SelectItem value="MONTHLY">Monthly</SelectItem>
          </SelectContent>
        </Select>
        {errors.frequency && (
          <p className="text-sm text-red-500">{errors.frequency.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <Label htmlFor="target">Target</Label>
        <Input
          id="target"
          type="number"
          min="1"
          placeholder="How many times?"
          {...register('target', { valueAsNumber: true })}
          className={errors.target ? 'border-red-500' : ''}
        />
        {errors.target && (
          <p className="text-sm text-red-500">{errors.target.message}</p>
        )}
      </div>

      <div className="flex justify-end space-x-2 pt-4">
        <Button 
          type="submit" 
          disabled={isLoading}
          className="min-w-[100px]"
        >
          {isLoading ? (
            <>
              <LoadingSpinner size="sm" className="mr-2" />
              {initialData ? 'Updating...' : 'Creating...'}
            </>
          ) : (
            initialData ? 'Update Habit' : 'Create Habit'
          )}
        </Button>
      </div>
    </form>
  );
};