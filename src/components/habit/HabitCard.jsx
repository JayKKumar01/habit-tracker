import React, { useEffect, useState } from "react";
import "../../styles/HabitCard.css";
import { deleteHabit, updateHabit } from "../../services/habitService";
import { getCurrentWeekDates, getLocalDateStr } from "../../utils/dateUtils";
import { Trash2, Pencil } from "lucide-react";
import ConfirmModal from "../modals/ConfirmModal";
import { updateHabitInList, deleteHabitInList } from "../state/habitState";
import EditHabitForm from "./EditHabitForm";
import HabitTags from "./HabitTags";

const daysShort = ["M", "T", "W", "T", "F", "S", "S"];
const daysLong = [
    "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY",
    "FRIDAY", "SATURDAY", "SUNDAY"
];

const HabitCard = ({ habit, user, setHabitsFromHabitCard }) => {
    const localDateStr = getLocalDateStr();

    const [isDeleteHabitOpen, setIsDeleteHabitOpen] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [weekStatus, setWeekStatus] = useState([]);
    const [todayIndex, setTodayIndex] = useState(null);
    const [streak, setStreak] = useState(0);

    useEffect(() => {
        const currentWeekDates = getCurrentWeekDates();
        const status = [];
        let todayIdx = null;

        currentWeekDates.forEach((dateStr, idx) => {
            if (dateStr === localDateStr) todayIdx = idx;

            if (!habit.targetDays.includes(daysLong[idx])) {
                status.push("grey-na");
            } else if (dateStr > localDateStr) {
                status.push("grey");
            } else {
                const log = habit.logs?.find(log => log.date === dateStr);
                status.push(log?.completed ? "green" : "red");
            }
        });

        setWeekStatus(status);
        setTodayIndex(todayIdx);
    }, [habit, habit.tags, localDateStr]);

    useEffect(() => {
        if (todayIndex === null || weekStatus.length === 0) return;

        let count = 0;
        for (let i = todayIndex; i >= 0; i--) {
            if (!habit.targetDays.includes(daysLong[i])) continue;
            if (weekStatus[i] !== "green") break;
            count++;
        }
        setStreak(count);
    }, [habit, todayIndex, weekStatus]);

    const handleHabitDelete = async () => {
        try {
            const res = await deleteHabit(user.id, habit.id);
            setHabitsFromHabitCard(prev => deleteHabitInList(prev, habit.id));
            console.log(res);
        } catch (error) {
            return "Failed to delete habit: " + error.message;
        }
    };

    const handleEditSubmit = async (updates) => {
        const updatedData = {
            habitId: habit.id,
            title: updates.title,
            description: updates.description,
            endDate: updates.endDate
        };

        const res = await updateHabit(updatedData, user.id);
        console.log(res);
        setHabitsFromHabitCard(prev => updateHabitInList(prev, habit.id, updates));
        setIsEditing(false);
    };

    const handleTagsChange = (newTags) => {
        setHabitsFromHabitCard(prev => updateHabitInList(prev, habit.id, { tags: newTags }));
    };

    if (isEditing) {
        return (
            <EditHabitForm
                habit={habit}
                onSubmit={handleEditSubmit}
                onCancel={() => setIsEditing(false)}
            />
        );
    }

    return (
        <div className="habit-card">
            <button className="edit-btn" onClick={() => setIsEditing(true)}>
                <Pencil size={18} color="#f39c12" strokeWidth={2} />
            </button>

            <button
                className="delete-btn"
                onClick={() => setIsDeleteHabitOpen(true)}
            >
                <Trash2 size={18} color="#e74c3c" strokeWidth={2} />
            </button>

            <h3>{habit.title}</h3>
            {habit.description && (
                <p className="habit-description">{habit.description}</p>
            )}
            <p><strong>Frequency:</strong> {habit.frequency}</p>
            <p><strong>Streak:</strong> {streak} 🔥</p>

            <HabitTags user={user} habit={habit} onChange={handleTagsChange} />

            <div className="weekly-tracker">
                {daysShort.map((day, idx) => (
                    <div
                        key={idx}
                        className={`day-indicator ${weekStatus[idx] || "grey"} ${todayIndex === idx ? "today-border" : ""}`}
                    >
                        {day}
                    </div>
                ))}
            </div>

            <ConfirmModal
                isOpen={isDeleteHabitOpen}
                onClose={() => setIsDeleteHabitOpen(false)}
                onConfirm={handleHabitDelete}
                message={`Delete habit "${habit.title}"?`}
            />
        </div>
    );
};

export default HabitCard;
