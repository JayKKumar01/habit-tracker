import React, { useState } from "react";
import "../../styles/WeeklyLogCard.css";

const dayLabels = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"];
const daysLong = [
    "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY",
    "FRIDAY", "SATURDAY", "SUNDAY"
];

// Generate an array of 7 date strings starting from the given Monday (local time)
const getWeekDateStrings = (startDateStr) => {
    if (!startDateStr) return [];

    const [year, month, day] = startDateStr.split("-").map(Number);
    const start = new Date(year, month - 1, day); // Note: month is 0-based

    return Array.from({ length: 7 }, (_, i) => {
        const d = new Date(start);
        d.setDate(start.getDate() + i);
        const yyyy = d.getFullYear();
        const mm = String(d.getMonth() + 1).padStart(2, "0");
        const dd = String(d.getDate()).padStart(2, "0");
        return `${yyyy}-${mm}-${dd}`;
    });
};

const getTodayDateStr = () => {
    const now = new Date();
    const yyyy = now.getFullYear();
    const mm = String(now.getMonth() + 1).padStart(2, "0");
    const dd = String(now.getDate()).padStart(2, "0");
    return `${yyyy}-${mm}-${dd}`;
};

const WeeklyLogCard = ({ weekNumber, habits = [], startDate, defaultOpen = false }) => {
    const [isOpen, setIsOpen] = useState(defaultOpen);
    const weekDates = getWeekDateStrings(startDate);
    const todayStr = getTodayDateStr();

    return (
        <div
            className="weekly-log-card"
            style={{ maxHeight: isOpen ? "500px" : "50px", overflow: "hidden" }}
        >
            <div className="log-card-header" onClick={() => setIsOpen((prev) => !prev)}>
                <h3>
                    📅 Week {weekNumber} Summary
                    <span className="toggle-icon">{isOpen ? "▲" : "▼"}</span>
                </h3>

                {isOpen && (
                    <div className="day-label-row">
                        <span className="habit-title-header">Habit</span>
                        {dayLabels.map((day) => (
                            <span key={day} className="day-label">{day}</span>
                        ))}
                    </div>
                )}
            </div>

            {isOpen && (
                <div className="habit-log-cards-scroll">
                    {habits.map((habit) => {
                        const logsByDate = {};
                        (habit.logs || []).forEach(log => {
                            logsByDate[log.date] = log.completed;
                        });

                        const statusForWeek = weekDates.map((date, idx) => {
                            const dayName = daysLong[idx];
                            const isTarget = habit.targetDays?.includes(dayName);
                            const isFuture = date > todayStr;

                            if (!isTarget) return "grey-na";
                            if (isFuture) return "grey";
                            const completed = logsByDate[date];
                            return completed ? "green" : "red";
                        });

                        return (
                            <div key={habit.id} className="habit-log-card">
                                <span className="habit-name">{habit.title}</span>
                                {statusForWeek.map((status, idx) => (
                                    <div
                                        key={idx}
                                        className={`day-status-box ${status} ${
                                            weekDates[idx] === todayStr ? "today-border" : ""
                                        }`}
                                    >
                                        {{
                                            green: "✔",
                                            red: "✖",
                                            grey: "—",
                                            "grey-na": "🚫"
                                        }[status]}
                                    </div>
                                ))}
                            </div>
                        );
                    })}
                </div>
            )}
        </div>
    );
};

export default WeeklyLogCard;
