import React, { useEffect, useState } from "react";
import "../styles/Dashboard.css";

// UI Components
import UserInfoCard from "./profile/UserInfoCard";
import LogoutButton from "./authentication/LogoutButton";
import TodayTaskList from "./dashboard/TodayTaskList";
import CurrentWeekIndicator from "./header/CurrentWeekIndicator";
import AddHabitButton from "./dashboard/AddHabitButton";
import HabitOverviewGrid from "./habit/HabitOverviewGrid";
import WeeklyProgressBar from "./habit/WeeklyProgressBar";
import WeeklyLogList from "./report/WeeklyLogList";

// Utilities
import { getHabitsWithLogsByUserId } from "../services/habitService";
import TokenExpiryWatcher from "../services/TokenExpiryWatcher";
import { useNavigate } from "react-router-dom";
import { User } from "lucide-react"; // ✅ nice lightweight user icon

const Dashboard = ({ user: initialUser }) => {
    const [user, setUser] = useState(initialUser);
    const [habits, setHabits] = useState([]);
    const [loading, setLoading] = useState(true);
    const [fullyLoaded, setFullyLoaded] = useState(false);
    const [refreshKey, setRefreshKey] = useState(0);
    const [showUserCard, setShowUserCard] = useState(false); // ✅ controls visibility on mobile
    const navigate = useNavigate();
    const token = localStorage.getItem("token");

    useEffect(() => {
        const fetchHabitsThenLogs = async () => {
            setLoading(true);
            setFullyLoaded(false);
            const totalStart = performance.now();

            try {
                const habitsWithEntities = await getHabitsWithLogsByUserId(user.id);
                setHabits(habitsWithEntities);
            } catch (err) {
                console.error("❌ Failed to load habits or logs:", err.message);
                setHabits([]);
            } finally {
                const totalEnd = performance.now();
                console.log(`🕒 Total fetch + update time: ${(totalEnd - totalStart).toFixed(2)} ms`);
                setLoading(false);
                setFullyLoaded(true);
            }
        };

        fetchHabitsThenLogs();
    }, [user.id, refreshKey]);

    const triggerRefresh = () => setRefreshKey(prev => prev + 1);

    return (
        <div className="dashboard-wrapper">
            <TokenExpiryWatcher
                token={token}
                onExpire={() => {
                    localStorage.clear();
                    navigate("/");
                }}
            />

            {!fullyLoaded && (
                <div className="full-screen-loader">
                    <div className="spinner" />
                </div>
            )}

            {/* Title Row */}
            <div className="dashboard-title-row">
                <h1 className="app-title">Habit Tracker</h1>
                <LogoutButton />
            </div>

            {/* Welcome Row */}
            <div className="dashboard-welcome-row">
                <h1 className="welcome-text">🎯 Welcome, {user.name}</h1>

                {/* ✅ Mobile-only user icon */}

                <button
                    className={`user-icon-btn ${showUserCard ? "active" : ""}`}
                    onClick={() => setShowUserCard(prev => !prev)}
                >
                    <User size={20} />
                </button>

            </div>

            {/* Week + Add Habit Row */}
            <div className="dashboard-week-add-row">
                <CurrentWeekIndicator user={user} />
                <AddHabitButton userId={user.id} setHabitsFromAddHabit={setHabits} />
            </div>

            <div className="dashboard-row">
                {/* ✅ Desktop: always show card | Mobile: conditional toggle */}
                <div className={`user-info-container ${showUserCard ? "show" : ""}`}>
                    <UserInfoCard user={user} setUserFromProfile={setUser} />
                </div>

                <TodayTaskList
                    habits={habits}
                    loading={loading}
                    user={user}
                    setHabitsFromChild={setHabits}
                />
            </div>

            <div className="habit-overview-section">
                <HabitOverviewGrid
                    habits={habits}
                    loading={loading}
                    user={user}
                    setHabitsFromHabitOverview={setHabits}
                />
            </div>

            <div className="weekly-progress-section">
                <WeeklyProgressBar
                    habits={habits}
                    loading={loading}
                    userId={user.id}
                />
            </div>

            <WeeklyLogList habits={habits} loading={loading} user={user} />
        </div>
    );
};

export default Dashboard;
