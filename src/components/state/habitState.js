// habitState.js

/**
 * Add the newly created habit optimistically to the habit list.
 * @param {Array} currentHabits - Existing habits.
 * @param {Object} newHabit - Habit returned from backend.
 * @returns {Array} - New habit list with added habit.
 */
export const addHabitLocally = (currentHabits, newHabit) => {
    return [...currentHabits, { ...newHabit, logs: [] }];
};

export const updateHabitInList = (habits, habitId, updates) =>
    habits.map(habit =>
        habit.id === habitId ? { ...habit, ...updates } : habit
    );
export const deleteHabitInList = (habits, habitId) => {
    return habits.filter(habit => habit.id !== habitId);
};
