import React from "react";
import "../../styles/HabitOverviewGrid.css";
import HabitCard from "./HabitCard";
import { getLocalDateStr } from "../../utils/dateUtils";

const HabitOverviewGrid = ({ habits, loading, user, setHabitsFromHabitOverview }) => {
    const localDateStr = getLocalDateStr();

    if (loading) {
        return (
            <div className="habit-overview-container">
                <h2>📋 Your Habits</h2>
                <p style={{ marginTop: "1rem", fontStyle: "italic" }}>Loading habits...</p>
            </div>
        );
    }

    const activeHabits = habits.filter(
        (habit) => !habit.endDate || habit.endDate > localDateStr
    );


    return (
        <div className="habit-overview-container">
            <h2>📋 Your Habits</h2>
            <div className="habit-scroll-row">
                {activeHabits.length > 0 ? (
                    activeHabits.map((habit) => (
                        <HabitCard
                            key={habit.id}
                            habit={habit}
                            user={user}
                            setHabitsFromHabitCard={setHabitsFromHabitOverview}
                        />
                    ))
                ) : (
                    <p style={{ marginTop: "1rem" }}>No active habits to show.</p>
                )}
            </div>
        </div>
    );
};

export default HabitOverviewGrid;
