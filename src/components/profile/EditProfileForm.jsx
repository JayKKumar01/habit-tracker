import React, { useState } from "react";
import "../../styles/EditProfileForm.css";

const MAX_NAME_LENGTH = 13;

const EditProfileForm = ({ user, onSubmit, onCancel, loading, error }) => {
    const [name, setName] = useState(user.name || "");
    const [bio, setBio] = useState(user?.profile?.bio || "");
    const [formError, setFormError] = useState("");

    const handleSubmit = (e) => {
        e.preventDefault();
        const trimmedName = name.trim();

        if (!trimmedName) {
            setFormError("Name cannot be empty or just spaces.");
            return;
        }

        if (trimmedName.length > MAX_NAME_LENGTH) {
            setFormError("Name cannot be this long!");
            return;
        }

        setFormError("");
        onSubmit({ name: trimmedName, bio });
    };

    return (
        <div className="edit-profile-form">
            <form onSubmit={handleSubmit}>
                <h2>✏️ Edit Profile</h2>

                <div className="form-group">
                    <label htmlFor="name">Name</label>
                    <input
                        id="name"
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        maxLength={MAX_NAME_LENGTH + 10} // allow typing a bit more, but trim & validate before submit
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="bio">Bio</label>
                    <textarea
                        id="bio"
                        rows={4}
                        value={bio}
                        onChange={(e) => setBio(e.target.value)}
                        placeholder="Write something about yourself..."
                    />
                </div>

                {(formError || error) && (
                    <p className="form-error">{formError || error}</p>
                )}

                <div className="form-actions">
                    <button type="submit" disabled={loading}>
                        {loading ? "Saving..." : "Save"}
                    </button>
                    <button type="button" onClick={onCancel} disabled={loading}>
                        Cancel
                    </button>
                </div>
            </form>
        </div>
    );
};

export default EditProfileForm;
