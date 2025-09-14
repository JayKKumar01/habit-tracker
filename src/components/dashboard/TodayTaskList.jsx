import React, { useEffect, useState } from "react";
import "../../styles/TodaysTaskList.css";

import AddHabitButton from "./AddHabitButton";
import ConfirmModal from "../modals/ConfirmModal";
import { updateHabitLog } from "../../services/habitLogService";
import { getLocalDateStr, getTodayWeekDay } from "../../utils/dateUtils";
import { updateLocalLog, revertLocalLog } from "../state/habitLogState";

const TodayTaskList = ({ habits = [], loading, user, setHabitsFromChild }) => {
    const [todayHabits, setTodayHabits] = useState([]);
    const [isModalOpen, setModalOpen] = useState(false);
    const [selectedHabit, setSelectedHabit] = useState(null);

    const localDateStr = getLocalDateStr();

    useEffect(() => {
        if (loading || habits.length === 0) {
            setTodayHabits([]);
            return;
        }

        const filteredHabits = habits.filter(habit => {
            const isStartOk = habit.startDate <= localDateStr;
            const isEndOk = !habit.endDate || habit.endDate > localDateStr;
            const isFrequencyOk = habit.frequency === "DAILY" || habit.targetDays?.includes(getTodayWeekDay());
            return isStartOk && isEndOk && isFrequencyOk;
        });

        const enrichedHabits = filteredHabits.map(habit => {
            const todayLog = habit.logs?.find(log => log.date === localDateStr);
            return {
                ...habit,
                completedToday: todayLog?.completed || false,
            };
        });

        setTodayHabits(enrichedHabits);
    }, [habits, loading]);

    const handleHabitCheck = (habit, checked) => {
        setSelectedHabit({ ...habit, checked });
        setModalOpen(true);
    };

    const handleCheckboxChange = async () => {
        if (!selectedHabit) return;

        const { id, checked } = selectedHabit;

        try {
            // ✅ Send to server
            await updateHabitLog(user.id, {
                habitId: id,
                date: localDateStr,
                completed: checked,
            });
            // 👁 Optimistically update the local UI
            const updatedHabits = updateLocalLog(habits, id, checked);
            setHabitsFromChild(updatedHabits);
            console.log(`✅ Synced "${selectedHabit.title}" as ${checked ? "completed" : "incomplete"}`);
        } catch (err) {
            console.error("❌ API call failed. Reverting UI:", err.message);
        } finally {
            setModalOpen(false);
        }
    };

    const isEmpty = !loading && todayHabits.length === 0;

    return (
        <div className="todays-task-card">
            <h2>✅ Today's Tasks</h2>

            {loading ? (
                <p>Loading habits...</p>
            ) : isEmpty ? (
                <div className="empty-task-placeholder">
                    <p className="no-habit-text">You haven’t added any habits yet.</p>
                    <AddHabitButton userId={user.id} setHabitsFromAddHabit={setHabitsFromChild} />
                </div>
            ) : (
                <div className="task-list-wrapper">
                    <ul className="task-list">
                        {todayHabits.map(habit => (
                            <li
                                key={habit.id}
                                className={`task modern-task-item ${habit.completedToday ? "done" : "pending"}`}
                            >
                                <label className="task-label">
                                    <input
                                        type="checkbox"
                                        checked={habit.completedToday}
                                        onChange={(e) => handleHabitCheck(habit, e.target.checked)}
                                    />
                                    <span className="task-title">{habit.title}</span>
                                </label>
                            </li>
                        ))}
                    </ul>
                </div>
            )}

            <ConfirmModal
                isOpen={isModalOpen}
                onClose={() => setModalOpen(false)}
                onConfirm={handleCheckboxChange}
                message={
                    selectedHabit
                        ? `Mark "${selectedHabit.title}" as ${selectedHabit.checked ? "completed" : "incomplete"}?`
                        : ""
                }
            />
        </div>
    );
};

export default TodayTaskList;
