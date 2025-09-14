import React, { useEffect, useState } from "react";
import "../../styles/ConfirmModal.css";

const ConfirmModal = ({ isOpen, onClose, onConfirm, message }) => {
    const [isProcessing, setProcessing] = useState(false);
    const [error, setError] = useState("");

    // Reset error and processing state every time modal opens
    useEffect(() => {
        if (isOpen) {
            setError("");
            setProcessing(false);
        }
    }, [isOpen]);

    if (!isOpen) return null;

    const handleConfirm = async () => {
        try {
            setProcessing(true);
            const result = await onConfirm(); // Can return an error string
            if (typeof result === "string" && result.length > 0) {
                setError(result);
            } else {
                onClose(); // only close on success
            }
        } catch (err) {
            setError(err.message || "Something went wrong.");
        } finally {
            setProcessing(false);
        }
    };

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <p className="modal-message">{message}</p>
                {error && <p className="modal-error">{error}</p>}
                <div className="modal-actions">
                    <button
                        className="confirm-btn"
                        onClick={handleConfirm}
                        disabled={isProcessing}
                    >
                        {isProcessing ? "Processing..." : "Confirm"}
                    </button>
                    <button
                        className="cancel-btn"
                        onClick={onClose}
                        disabled={isProcessing}
                    >
                        Cancel
                    </button>
                </div>
            </div>
        </div>
    );
};

export default ConfirmModal;
