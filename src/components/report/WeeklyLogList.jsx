import React, { useEffect, useState } from "react";
import "../../styles/WeeklyLogList.css";
import WeeklyLogCard from "../habit/WeeklyLogCard";
import {getMonday, toLocalYYYYMMDD} from "../../utils/dateUtils";

// Get weekly ranges from start to today (Monday to Sunday)
const getWeeklyDateRanges = (startDateLocal) => {
    const weeks = [];
    let current = getMonday(startDateLocal);
    const now = new Date();
    const todayMonday = getMonday(now);

    while (current <= todayMonday) {
        const weekStart = new Date(current);
        const weekEnd = new Date(current);
        weekEnd.setDate(weekStart.getDate() + 6);
        weeks.unshift({
            startDate: toLocalYYYYMMDD(weekStart),
            endDate: toLocalYYYYMMDD(weekEnd)
        });
        current.setDate(current.getDate() + 7);
    }

    return weeks;
};

// Check if a habit is active in the week
const isHabitActiveInWeek = (habit, weekStart, weekEnd) => {
    return habit.startDate <= weekEnd && (!habit.endDate || habit.endDate >= weekStart);
};

const WeeklyLogList = ({ habits = [], loading, user }) => {
    const [weeklyHabitsList, setWeeklyHabitsList] = useState([]);

    useEffect(() => {
        const fetchAndOrganize = async () => {
            const createdAtLocal = new Date(user.createdAt);
            const weeks = getWeeklyDateRanges(createdAtLocal);

            if (habits.length === 0) return;

            const result = weeks.map((week, index) => {
                const habitsForWeek = habits.filter(habit =>
                    isHabitActiveInWeek(habit, week.startDate, week.endDate)
                );
                return {
                    weekNumber: weeks.length - index,
                    habits: habitsForWeek,
                    weekStartStr: week.startDate,
                    weekEndStr: week.endDate
                };
            });

            setWeeklyHabitsList(result);
        };

        if (!loading) {
            fetchAndOrganize();
        }
    }, [habits, user.email, loading]);

    return (
        <div className="weekly-logs-section">
            {loading ? (
                <p className="weekly-logs-loading-text">Loading weekly logs...</p>
            ) : weeklyHabitsList.length === 0 ? (
                <p className="weekly-logs-empty-text">No habits found for any week.</p>
            ) : (
                weeklyHabitsList.map((week) => (
                    <WeeklyLogCard
                        key={week.weekNumber}
                        weekNumber={week.weekNumber}
                        habits={week.habits}
                        startDate={week.weekStartStr}
                        defaultOpen={week.weekNumber === weeklyHabitsList.length}
                    />
                ))
            )}
        </div>
    );
};

export default WeeklyLogList;
