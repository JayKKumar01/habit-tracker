// components/state/habitLogState.js
import { getLocalDateStr } from "../../utils/dateUtils";

/**
 * Update the habit log in the UI state locally
 * @param {Array} habits - Original habit list from Dashboard
 * @param {string} habitId - ID of the habit being updated
 * @param {boolean} completed - New completed status
 * @returns {Array} - Updated habits array (shallow copy)
 */
export const updateLocalLog = (habits, habitId, completed) => {
    const today = getLocalDateStr();

    return habits.map(habit => {
        if (habit.id !== habitId) return habit;

        const logs = habit.logs || [];
        const index = logs.findIndex(log => log.date === today);
        const updatedLogs = [...logs];

        if (index !== -1) {
            updatedLogs[index] = { ...updatedLogs[index], completed };
        } else {
            updatedLogs.push({ habitId, date: today, completed });
        }

        return { ...habit, logs: updatedLogs };
    });
};

/**
 * Revert the habit log for today in case of failure
 * @param {Array} habits - Original habit list
 * @param {string} habitId - ID of the habit to revert
 * @returns {Array} - Updated habits array with today's log removed
 */
export const revertLocalLog = (habits, habitId) => {
    const today = getLocalDateStr();

    return habits.map(habit => {
        if (habit.id !== habitId) return habit;

        const updatedLogs = (habit.logs || []).filter(log => log.date !== today);
        return { ...habit, logs: updatedLogs };
    });
};
