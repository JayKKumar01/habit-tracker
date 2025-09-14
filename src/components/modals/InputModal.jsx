import React, { useState, useEffect } from "react";
import "../../styles/InputModal.css";

const InputModal = ({
                        isOpen,
                        onClose,
                        onConfirm,
                        title = "Enter value:",
                        placeholder = "",
                        confirmText = "Confirm",
                        cancelText = "Cancel",
                        initialValue = "",
                        suggestions = []
                    }) => {
    const [inputValue, setInputValue] = useState(initialValue);
    const [isProcessing, setProcessing] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        if (isOpen) {
            setInputValue(initialValue);
            setError("");
        }
    }, [isOpen, initialValue]);

    if (!isOpen) return null;

    const handleConfirm = async () => {
        try {
            setProcessing(true);
            setError("");
            const err = await onConfirm(inputValue.trim());
            if (typeof err === "string" && err.length > 0) {
                setError(err);
            } else {
                setInputValue("");
                onClose();
            }
        } catch (e) {
            setError(e.message || "Something went wrong.");
        } finally {
            setProcessing(false);
        }
    };

    const handleSuggestionClick = (tag) => {
        setInputValue(tag);
    };

    return (
        <div className="input-modal-overlay">
            <div className="input-modal-content">
                <p className="input-modal-message">{title}</p>
                <input
                    type="text"
                    className="input-modal-input"
                    placeholder={placeholder}
                    value={inputValue}
                    onChange={(e) => setInputValue(e.target.value)}
                    disabled={isProcessing}
                />
                {suggestions?.length > 0 && (
                    <div className="input-modal-suggestions">
                        {suggestions.map((tag, i) => (
                            <button
                                key={i}
                                type="button"
                                className={`input-suggestion-btn ${tag === inputValue.trim() ? "selected" : ""}`}
                                onClick={() => handleSuggestionClick(tag)}
                                disabled={isProcessing}
                            >
                                {tag}
                            </button>
                        ))}
                    </div>
                )}
                {error && <p className="input-modal-error">{error}</p>}
                <div className="input-modal-actions">
                    <button
                        className="input-confirm-btn"
                        onClick={handleConfirm}
                        disabled={isProcessing || !inputValue.trim()}
                    >
                        {isProcessing ? "Processing..." : confirmText}
                    </button>
                    <button
                        className="input-cancel-btn"
                        onClick={onClose}
                        disabled={isProcessing}
                    >
                        {cancelText}
                    </button>
                </div>
            </div>
        </div>
    );
};

export default InputModal;
