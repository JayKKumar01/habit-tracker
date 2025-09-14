import React, { useState } from "react";
import "../../styles/AddHabitButton.css";
import HabitForm from "../habit/HabitForm";
import {addHabitLocally} from "../state/habitState";

const AddHabitButton = ({ userId, setHabitsFromAddHabit }) => {
    const [open, setOpen] = useState(false);

    const handleCreated = (newHabit) => {

        console.log("New habit created:", newHabit);

        setHabitsFromAddHabit(prev => addHabitLocally(prev, newHabit));
        setOpen(false);
    };




    return (
        <>
            <button className="add-habit-btn" onClick={() => setOpen(true)}>
                ➕ Add Habit
            </button>

            {open && (
                <HabitForm
                    userId={userId}
                    onSuccess={handleCreated}
                    onClose={() => setOpen(false)}
                />
            )}
        </>
    );
};

export default AddHabitButton;
