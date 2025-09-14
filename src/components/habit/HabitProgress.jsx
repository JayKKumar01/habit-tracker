import React, { useEffect, useState } from "react";
import "../../styles/HabitProgress.css";
import { getCurrentWeekDates } from "../../utils/dateUtils";

const HabitProgress = ({ habit, userId }) => {
    const { title, id, targetDays = [] } = habit;
    const [completionRate, setCompletionRate] = useState(0);

    const daysLong = [
        "MONDAY",
        "TUESDAY",
        "WEDNESDAY",
        "THURSDAY",
        "FRIDAY",
        "SATURDAY",
        "SUNDAY",
    ];

    useEffect(() => {
        const calculateProgress = async () => {
            const weekDates = getCurrentWeekDates();

            let applicableDays = 0;
            let completedDays = 0;

            weekDates.forEach((dateStr, idx) => {
                const dayName = daysLong[idx]; // Get day name for current date index
                if (targetDays.includes(dayName)) {
                    applicableDays++;
                    const log = habit.logs?.find(log => log.date === dateStr);
                    if (log?.completed) completedDays++;
                }
            });

            const rate = applicableDays === 0 ? 0 : Math.round((completedDays / applicableDays) * 100);

            // console.log(`📊 ${title}: ${completedDays}/${applicableDays} completed (${rate}%)`);

            setCompletionRate(rate);
        };

        calculateProgress();
    }, [habit, userId]);

    return (
        <div className="progress-card">
            <div className="progress-title">{title}</div>
            <div className="progress-bar">
                <div
                    className="progress-fill"
                    style={{ width: `${completionRate}%` }}
                ></div>
            </div>
            <div className="progress-percent">{completionRate}%</div>
        </div>
    );
};

export default HabitProgress;
