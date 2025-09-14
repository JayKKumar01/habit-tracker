// Util to get today's date as local yyyy-mm-dd string
export const getLocalDateStr = (date = new Date()) => {
    const yyyy = date.getFullYear();
    const mm = String(date.getMonth() + 1).padStart(2, '0');
    const dd = String(date.getDate()).padStart(2, '0');
    return `${yyyy}-${mm}-${dd}`;
};

export const toLocalYYYYMMDD = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}-${month}-${day}`;
};

export const getMonday = (date) => {
    const result = new Date(date);
    const day = result.getDay(); // Sunday = 0, Monday = 1, ..., Saturday = 6
    const diff = (day === 0 ? -6 : 1) - day; // Shift so Monday is the start
    result.setDate(result.getDate() + diff);
    result.setHours(0, 0, 0, 0);
    return result;
};


export const getTodayWeekDay = () => {
    return new Date().toLocaleDateString("en-US", { weekday: "long" }).toUpperCase();
}

export const getCurrentWeekDates = () => {
    const date = new Date();
    const currentDayIndex = date.getDay(); // 0 = Sunday, 1 = Monday, ..., 6 = Saturday

    const diffFromMonday = (currentDayIndex + 6) % 7;
    const startOfWeek = new Date(date);
    startOfWeek.setDate(date.getDate() - diffFromMonday);

    const result = [];
    for (let i = 0; i < 7; i++) {
        const day = new Date(startOfWeek);
        day.setDate(startOfWeek.getDate() + i);

        const yyyy = day.getFullYear();
        const mm = String(day.getMonth() + 1).padStart(2, '0');
        const dd = String(day.getDate()).padStart(2, '0');

        result.push(`${yyyy}-${mm}-${dd}`); // LOCAL yyyy-mm-dd (no UTC conversion)
    }

    return result;
};



// ✅ Export constant list of all days in order
export const daysOfWeek = [
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY",
    "SATURDAY",
    "SUNDAY"
];
