import React from "react";
import "../../styles/CurrentWeekIndicator.css";
import {getMonday} from "../../utils/dateUtils";

const CurrentWeekIndicator = ({ user }) => {
    const getWeekAndDay = () => {
        const createdDate = new Date(user.createdAt);
        const now = new Date();

        const createdMonday = getMonday(createdDate);
        const todayMonday = getMonday(now);

        const weeksPassed = Math.floor((todayMonday - createdMonday) / (7 * 24 * 60 * 60 * 1000));
        const currentWeek = weeksPassed + 1;

        // Make week start from Monday
        const daysOfWeek = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
        const adjustedDayIndex = (now.getDay() + 6) % 7; // Shift Sunday=0 to end
        const currentDay = daysOfWeek[adjustedDayIndex];

        return `Week ${currentWeek} - ${currentDay}`;
    };

    return <div className="current-week">{getWeekAndDay()}</div>;
};

export default CurrentWeekIndicator;
