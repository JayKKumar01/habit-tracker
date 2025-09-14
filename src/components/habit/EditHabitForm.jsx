import React, { useState } from "react";
import "../../styles/EditHabitForm.css";
import { getLocalDateStr } from "../../utils/dateUtils";

const EditHabitForm = ({ habit, onSubmit, onCancel }) => {
    const localDateStr = getLocalDateStr();
    const [title, setTitle] = useState(habit.title);
    const [description, setDescription] = useState(habit.description || "");
    const [isActive, setIsActive] = useState(true); // always true initially
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        const trimmedTitle = title.trim();
        const trimmedDescription = description.trim();

        // Basic validation
        if (!trimmedTitle) {
            setError("Title is required.");
            return;
        }

        if (trimmedTitle.length > 21) {
            setError("Title can't be too long.");
            return;
        }

        if (!trimmedDescription) {
            setError("Description is required.");
            return;
        }

        if (trimmedDescription.length > 100) {
            setError("That description is a bit too long.");
            return;
        }

        // If active and nothing changed
        const isNoChange =
            isActive &&
            trimmedTitle === habit.title &&
            trimmedDescription === (habit.description || "");

        if (isNoChange) {
            setError("No changes made.");
            return;
        }

        // Construct full update payload
        const updates = {
            title: trimmedTitle,
            description: trimmedDescription,
            endDate: isActive ? null : localDateStr,
        };

        setLoading(true);
        try {
            await onSubmit(updates);
        } catch (err) {
            console.error(err);
            setError("Failed to update habit.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="habit-form-backdrop">
            <div className="habit-form-modal">
                <h3>Edit Habit</h3>

                {error && <p className="error">{error}</p>}

                <form onSubmit={handleSubmit}>
                    <label htmlFor="status-toggle" className="status-label">
                        <div className="status-toggle-wrapper">
                            <span className="status-text">{isActive ? "Active" : "Inactive"}</span>
                            <div className={`toggle-switch ${isActive ? "active" : "inactive"}`}>
                                <input
                                    id="status-toggle"
                                    type="checkbox"
                                    checked={isActive}
                                    onChange={() => setIsActive(!isActive)}
                                />
                                <span className="slider"></span>
                            </div>
                        </div>
                    </label>

                    <label htmlFor="title">Title</label>
                    <input
                        id="title"
                        type="text"
                        placeholder="Enter title"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        disabled={!isActive}
                        required
                    />

                    <label htmlFor="description">Description</label>
                    <textarea
                        id="description"
                        placeholder="Enter description"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        rows={3}
                        disabled={!isActive}
                        required
                    />

                    <div className="form-buttons">
                        <button type="submit" disabled={loading}>
                            {loading ? "Saving..." : "Save"}
                        </button>
                        <button type="button" onClick={onCancel} disabled={loading}>
                            Cancel
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default EditHabitForm;
