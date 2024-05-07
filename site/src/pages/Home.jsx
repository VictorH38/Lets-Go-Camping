import React, {useEffect} from "react";
import homepageImage from '.././assets/images/homepage.jpeg';
import { useAuth } from '../AuthContext';
import {getCookie} from "../utils/cookieHelper";

function Home() {
    const { isLoggedIn, setIsLoggedIn } = useAuth();

    useEffect(() => {
        const userToken = getCookie('token');
        setIsLoggedIn(!!userToken);
    }, []);

    return (
        <div className={"container"}>
            <div id={"homepage-image-container"}>
                <img id={"homepage-image"} src={homepageImage} alt="Homepage"/>
            </div>

            {!isLoggedIn && (
                <h2 id="homepage-message">Login or Signup to search for parks and add them to your favorites</h2>
            )}
        </div>
    )
}

export default Home;
