import React, {useEffect} from "react";
import "../../styles/WeeklyProgressBar.css";
import HabitProgress from "./HabitProgress";
import { getLocalDateStr } from "../../utils/dateUtils";

const WeeklyProgressBar = ({ habits, loading, userId }) => {

    const localDateStr = getLocalDateStr();

    const activeHabits = habits.filter(
        habit => !habit.endDate || habit.endDate > localDateStr
    );

    return (
        <div className="weekly-progress-wrapper">
            <h2>📈 Weekly Progress</h2>
            {loading ? (
                <p>Loading progress...</p>
            ) : activeHabits.length === 0 ? (
                <p>No active habits to track this week.</p>
            ) : (
                <div className="progress-grid">
                    {activeHabits.map(habit => (
                        <HabitProgress key={habit.id} habit={habit} userId={userId} />
                    ))}
                </div>
            )}
        </div>
    );
};

export default WeeklyProgressBar;
