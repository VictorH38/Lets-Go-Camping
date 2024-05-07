import React, { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getCookie, removeCookie } from '../utils/cookieHelper';
import { useAuth } from '../AuthContext';

const Navigation = () => {
    const { isLoggedIn, setIsLoggedIn } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        const userToken = getCookie('token');
        setIsLoggedIn(!!userToken);
    }, []);

    const handleLogout = () => {
        removeCookie('token');
        removeCookie('user_id');
        setIsLoggedIn(false);
        navigate("/");
    };

    return (
        <nav className="navbar navbar-light">
            <div className="container-fluid">
                <ul className="navbar-list me-auto mb-2 mb-lg-0 d-flex justify-content-between">
                    <div className="d-flex flex-row">
                        <li className="nav-item">
                            <Link className="navbar-link" to="/">Home</Link>
                        </li>
                        {isLoggedIn ? (
                            <li className="nav-item">
                                <Link className="navbar-link" to="/search">Search</Link>
                            </li>
                        ) : null}
                        {isLoggedIn ? (
                            <li className="nav-item">
                                <Link className="navbar-link" to="/favorites">Favorites</Link>
                            </li>
                        ) : null}
                        {isLoggedIn ? (
                            <li className="nav-item">
                                <Link className="navbar-link" to="/friends">Friends</Link>
                            </li>
                        ) : null}
                    </div>

                    <div>
                        {!isLoggedIn ? (
                            <li className="nav-item">
                                <Link className="navbar-link" to="/login">Login</Link>
                            </li>
                        ) : (
                            <li className="nav-item">
                                <Link className="navbar-link" onClick={handleLogout} to="/">Logout</Link>
                            </li>
                        )}
                    </div>
                </ul>
            </div>
        </nav>
    );
};

export default Navigation;
