import React, { useState } from "react";
import "../../styles/HabitTags.css";
import { Plus, Trash2 } from "lucide-react";
import ConfirmModal from "../modals/ConfirmModal";
import InputModal from "../modals/InputModal";
import { addHabitTag, removeHabitTag } from "../../services/tagService";

const themeTags = [
    "mindfulness", "productivity", "health", "focus", "discipline",
    "routine", "fitness", "gratitude", "balance", "consistency"
];

const HabitTags = ({ user, habit, onChange }) => {
    const [selectedTagId, setSelectedTagId] = useState(null);
    const [isConfirmOpen, setIsConfirmOpen] = useState(false);
    const [isInputOpen, setIsInputOpen] = useState(false);

    const tags = Array.isArray(habit.tags) ? habit.tags : [];
    const sortedTags = [...tags].sort((a, b) => a.id - b.id);

    const selectedTag = sortedTags.find(tag => tag.id === selectedTagId);

    const handleTagClick = (tagId) => {
        setSelectedTagId(prev => (prev === tagId ? null : tagId));
    };

    const openAddModal = () => setIsInputOpen(true);
    const requestDeleteSelected = () => {
        if (selectedTagId !== null) setIsConfirmOpen(true);
    };

    const handleAddTagConfirm = async (newTagName) => {
        const tagName = (newTagName || "").trim().toLowerCase();
        if (!tagName) return "Tag cannot be empty.";
        if (tags.some(tag => tag.name.toLowerCase() === tagName)) return "This tag already exists.";
        if (tags.length >= 10) return "Maximum limit exceeded.";

        try {
            const addedTag = await addHabitTag(user.id, { habitId: habit.id, name: tagName });

            if (!addedTag?.id || !addedTag?.name) return "Invalid tag received from server.";

            console.log("✅ Tag successfully added:", addedTag);
            onChange([...tags, addedTag]);
        } catch (err) {
            console.error("❌ Failed to add tag:", err);
            return "Failed to add tag.";
        }

        return "";
    };

    const handleDeleteSelected = async () => {
        if (!selectedTag) return;

        const isFreqTag = selectedTag.name.toLowerCase() === habit.frequency.toLowerCase();
        if (isFreqTag) {
            return `Cannot delete tag "${selectedTag.name}" as it matches habit's frequency.`;
        }

        try {
            await removeHabitTag(user.id, { habitId: habit.id, tagId: selectedTag.id });
            console.log("🗑️ Tag removed:", selectedTag);

            onChange(tags.filter(tag => tag.id !== selectedTag.id));
            setSelectedTagId(null);
        } catch (err) {
            console.error("❌ Failed to delete tag:", err);
            return "Failed to delete tag.";
        }
    };

    return (
        <div className="tags-section">
            <div className="tags-header">
                <p className="tags-label">Tags:</p>
                <button
                    className="tag-action-btn"
                    onClick={selectedTagId === null ? openAddModal : requestDeleteSelected}
                >
                    {selectedTagId === null ? <Plus size={16} /> : <Trash2 size={16} />}
                </button>
            </div>

            <div className="habit-tags-scroll">
                <div className="habit-tags-inner">
                    {sortedTags.map(tag => (
                        <span
                            key={tag.id}
                            className={`habit-tag ${selectedTagId === tag.id ? "selected" : ""}`}
                            onClick={() => handleTagClick(tag.id)}
                        >
                            {tag.name}
                        </span>
                    ))}
                </div>
            </div>

            <ConfirmModal
                isOpen={isConfirmOpen}
                onClose={() => setIsConfirmOpen(false)}
                onConfirm={handleDeleteSelected}
                message={
                    selectedTag
                        ? `Delete tag "${selectedTag.name}"?`
                        : "Delete selected tag?"
                }
            />

            <InputModal
                isOpen={isInputOpen}
                onClose={() => setIsInputOpen(false)}
                onConfirm={handleAddTagConfirm}
                title="Enter a new tag:"
                placeholder="e.g., productivity"
                suggestions={themeTags}
            />
        </div>
    );
};

export default HabitTags;
