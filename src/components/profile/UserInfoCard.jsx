import React, { useState, useEffect } from "react";
import "../../styles/UserInfoCard.css";
import { Pencil } from "lucide-react";
import EditProfileForm from "./EditProfileForm";
import { getProfile, saveOrUpdateProfile } from "../../services/profileService";

const UserInfoCard = ({ user, setUserFromProfile }) => {
    const [bio, setBio] = useState("Loading...");
    const [isEditing, setIsEditing] = useState(false);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const getCleanBio = (bio) => bio?.trim() || "No bio set";

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const profile = await getProfile(user.id);

                if (profile && typeof profile === "object") {
                    setUserFromProfile((prev) => ({ ...prev, profile }));
                    setBio(getCleanBio(profile.bio));
                } else {
                    setUserFromProfile((prev) => ({ ...prev, profile: null }));
                    setBio("No bio set");
                }
            } catch (err) {
                console.warn("Error fetching profile:", err);
                setBio("No bio set");
            }
        };

        if (user.profile) {
            setBio(getCleanBio(user.profile.bio));
        } else {
            fetchProfile();
        }
    }, [user.id, user.profile, setUserFromProfile]);

    const handleEditClick = () => setIsEditing(true);

    const handleEditSubmit = async ({ name, bio }) => {
        setLoading(true);
        setError("");

        try {
            await saveOrUpdateProfile(user.id, { name, bio });

            setUserFromProfile((prev) => ({
                ...prev,
                name,
                profile: { ...(prev.profile || {}), bio },
            }));

            setBio(getCleanBio(bio));
            setIsEditing(false);
        } catch (err) {
            setError(err.message || "Failed to save changes.");
        } finally {
            setLoading(false);
        }
    };

    if (isEditing) {
        return (
            <EditProfileForm
                user={user}
                onSubmit={handleEditSubmit}
                onCancel={() => setIsEditing(false)}
                loading={loading}
                error={error}
            />
        );
    }

    return (
        <div className="user-info-card">
            <div className="user-info-inner">
                <div className="user-info-header">
                    <h2>👤 User Profile</h2>
                    <button
                        className="edit-btn"
                        onClick={handleEditClick}
                        title="Edit profile"
                        disabled={loading}
                    >
                        <Pencil size={20} color="#1976d2" strokeWidth={2.2} />
                    </button>
                </div>

                <div className="user-info-content">
                    <p><strong>Name:</strong> {user.name}</p>
                    <p><strong>Email:</strong> {user.email}</p>
                    <p><strong>Joined:</strong> {new Date(user.createdAt).toLocaleDateString()}</p>
                    <p><strong>Bio:</strong> {bio}</p>
                </div>
            </div>
        </div>
    );
};

export default UserInfoCard;
