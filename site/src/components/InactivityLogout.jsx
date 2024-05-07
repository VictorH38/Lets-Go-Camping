import React, { useState } from 'react';
import { useIdleTimer } from "react-idle-timer";
import { getCookie, removeCookie } from "../utils/cookieHelper";
import { useAuth } from '../AuthContext';
import { useNavigate, Link } from "react-router-dom";

const InactivityLogout = () => {
    const TIME_BEFORE_IDLE = 1000 * 30 * 60;
    const { setIsLoggedIn } = useAuth();
    const navigate = useNavigate();

    const logout = () => {
        if (getCookie("user_id") != null)
        {
            removeCookie("token");
            removeCookie("user_id");
            setIsLoggedIn(false);
            navigate("/login");
            alert("You have been logged out due to being inactive for 30 minutes");
        }
    }

    const onIdle = () => {
        logout();
    }

    const {getRemainingTime} = useIdleTimer({
        onIdle,
        timeout: TIME_BEFORE_IDLE,
    })

    return <></>;
}
export default InactivityLogout;