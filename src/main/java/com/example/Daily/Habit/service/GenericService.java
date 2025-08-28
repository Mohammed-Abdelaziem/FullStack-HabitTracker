    package com.example.Daily.Habit.service;
    import org.springframework.data.domain.Page;
    import java.util.Collection;
    import java.util.UUID;

    public interface GenericService<T> {
        T findById(UUID id);
        T save(T entity);
        void delete(UUID id);
        Collection<T> findAll();
        Page<T> findAll(int page, int size);

    }
