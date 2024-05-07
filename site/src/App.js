import React from "react";
import {Route, Routes} from "react-router-dom";
import Home from "./pages/Home";
import LoginPage from "./pages/LoginPage"
import SignupPage from "./pages/SignupPage"
import Search from "./pages/Search";
import {Header} from "./components/Header";
import {Footer} from "./components/Footer";
import InactivityLogout from "./components/InactivityLogout";
import Navigation from "./components/Navigation";
import Favorites from "./pages/Favorites";
import Friends from "./pages/Friends";

function App() {
    return (
        <div>
            <Header/>
            <Navigation/>
            <Routes>
                <Route path="/" element={<Home/>}/>
                <Route path="/login" element={<LoginPage/>}/>
                <Route path="/signup" element={<SignupPage/>}/>
                <Route path="/search" element={<Search/>}/>
                <Route path="/favorites" element={<Favorites/>}/>
                <Route path="/friends" element={<Friends/>}/>
            </Routes>
            <Footer/>
            <InactivityLogout/>
        </div>
    )
}

export default App;