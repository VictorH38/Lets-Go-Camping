import React, {useState, useEffect, useRef} from "react";
import {getCookie} from "../utils/cookieHelper";

function Friends() {
    const [currentUser, setCurrentUser] = useState(null);
    const [users, setUsers] = useState([]);
    const [selectedFriend, setSelectedFriend] = useState(null);
    const [suggestedParks, setSuggestedParks] = useState({});
    const detailsRef = useRef({});

    useEffect(() => {
        fetchUsers();
    }, []);

    useEffect(() => {
        calculateSuggestedParks();
    }, [currentUser, users]);

    useEffect(() => {
        const friendDetails = document.querySelectorAll('.friend-details');
        friendDetails.forEach((detail) => {
            if (selectedFriend && detail.getAttribute('data-friend-id') === selectedFriend.toString()) {
                detail.style.maxHeight = `${detail.scrollHeight}px`;
            } else {
                detail.style.maxHeight = null;
            }
        });
    }, [selectedFriend]);

    const toggleFriendDetails = (userId) => {
        setSelectedFriend(selectedFriend === userId ? null : userId);
    };

    const fetchUsers = async () => {
        const token = getCookie('token');
        const userId = getCookie('user_id');
        if (token && userId) {
            try {
                const response = await fetch(`/api/users`, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
                const responseData = await response.json();
                if (responseData && responseData.data && responseData.data.users) {
                    const allUsers = responseData.data.users;
                    const filteredUsers = allUsers.filter(user => user.id !== parseInt(userId));
                    const currentUser = allUsers.find(user => user.id === parseInt(userId));
                    setUsers(filteredUsers);
                    setCurrentUser(currentUser);
                }
            } catch (error) {
                console.error('Failed to fetch users', error);
            }
        }
    };

    const calculateSuggestedParks = async () => {
        const suggestedParks = {};

        users.forEach(user => {
            const commonParks = user.favoriteParks.filter(favoritePark1 =>
                currentUser.favoriteParks.some(favoritePark2 =>
                    favoritePark1.park.id === favoritePark2.park.id
                )
            );

            if (commonParks.length > 0) {
                let maxAverageRank = 500;
                let suggestedPark = null;

                commonParks.forEach(commonPark => {
                    const currentUserRank = currentUser.favoriteParks.find(favorite => favorite.park.id === commonPark.park.id).rank;
                    const friendRank = user.favoriteParks.find(favorite => favorite.park.id === commonPark.park.id).rank;
                    const averageRank = (currentUserRank + friendRank) / 2;

                    if (averageRank < maxAverageRank) {
                        maxAverageRank = averageRank;
                        suggestedPark = commonPark;
                    }
                });

                suggestedParks[user.id] = suggestedPark;
            }
        });

        setSuggestedParks(suggestedParks);
    };

    return (
        <div className="container my-5">
            <div className="row justify-content-center">
                <div className="col-md-8">
                    <h2 id="friends-title">{users.length === 0 ? 'You have no friends' : 'My Friends'}</h2>

                    {users.length > 0 && (
                        <>
                            <h5 id="friends-message">Click on a friend to see their favorite parks and a suggested park
                                for you two to go to together!</h5>

                            <div className="friends-list">
                                {users.map((user) => (
                                    <div
                                        key={user.id}
                                        className={`card mb-3 friend-item ${selectedFriend === user.id ? 'expanded' : ''}`}
                                        data-testid={`user-card-${user.id}`}
                                        onClick={() => toggleFriendDetails(user.id)}
                                    >
                                        <div className="card-body">
                                            <h3
                                                id={`friend-name-${user.id}`}
                                                className="card-title"
                                            >
                                                {user.name}
                                            </h3>

                                            <div
                                                ref={(el) => detailsRef.current[user.id] = el}
                                                data-friend-id={user.id}
                                                className="friend-details"
                                            >
                                                {user.isPublic ? (
                                                    <>
                                                        <h6>Favorite Parks:</h6>
                                                        <ul>
                                                            {user.favoriteParks.map((favoritePark) => (
                                                                <li key={favoritePark.park.id}>{favoritePark.park.fullName}</li>
                                                            ))}
                                                        </ul>
                                                        <h6>Suggested Park:</h6>
                                                        <p className="suggested-park">{suggestedParks[user.id] ? suggestedParks[user.id].park.fullName : 'You have no common favorite parks to suggest'}</p>
                                                    </>
                                                ) : (
                                                    <h6 id="private-message">Favorite parks are private</h6>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Friends;
