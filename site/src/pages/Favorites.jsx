    import React, { useState, useEffect, useRef } from "react";
    import { getCookie } from "../utils/cookieHelper";
    import { DragDropContext, Droppable, Draggable } from "react-beautiful-dnd";

    function Favorites({ testRefMissing = false }) {
        const [currentUser, setCurrentUser] = useState(null);
        const [favorites, setFavorites] = useState([]);
        const [selectedPark, setSelectedPark] = useState(null);
        const [confirmationVisible, setConfirmationVisible] = useState(false);
        const [selectedParkToRemove, setSelectedParkToRemove] = useState(null);
        const detailsRef = useRef({});

        useEffect(() => {
            fetchCurrentUser();
            fetchFavorites();
        }, []);

        useEffect(() => {
            Object.keys(detailsRef.current).forEach(parkId => {
                let detailEl = detailsRef.current[parkId];
                if (testRefMissing) {
                    detailEl = null;
                }

                if (detailEl && parseInt(parkId, 10) === selectedPark) {
                    detailEl.style.maxHeight = `${detailEl.scrollHeight}px`;
                } else if (detailEl) {
                    detailEl.style.maxHeight = null;
                }
            });
        }, [favorites, selectedPark]);

        const fetchCurrentUser = async () => {
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
                        const currentUser = allUsers.find(user => user.id === parseInt(userId));
                        setCurrentUser(currentUser);
                    }
                } catch (error) {
                    console.error('Failed to fetch current user', error);
                }
            }
        };

        const toggleUserPublicity = async () => {
            const token = getCookie('token');
            if (token && currentUser) {
                const updatedPublicity = !currentUser.isPublic;
                try {
                    const response = await fetch(`/api/users/public`, {
                        method: 'POST',
                        headers: {
                            'Authorization': `Bearer ${token}`,
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            user_id: currentUser.id,
                            is_public: updatedPublicity
                        })
                    });
                    if (response.ok) {
                        fetchCurrentUser();
                    } else {
                        console.error('Failed to update user publicity');
                    }
                } catch (error) {
                    console.error('Error updating user publicity', error);
                }
            }
        };

        const fetchFavorites = async () => {
            const token = getCookie('token');
            const userId = getCookie('user_id');
            if (userId && token) {
                try {
                    const response = await fetch(`/api/parks/favorite/${userId}`, {
                        headers: {
                            'Authorization': `Bearer ${token}`
                        }
                    });
                    const responseData = await response.json();
                    if (responseData && responseData.data && responseData.data.parks) {
                        setFavorites(responseData.data.parks.map(favoritePark => favoritePark.park));
                    }
                } catch (error) {
                    console.error('Failed to fetch favorites', error);
                }
            }
        };

        const removeFavorite = async (parkId) => {
            setSelectedParkToRemove(parkId);
            setConfirmationVisible(true);
        };

        const handleRemoveFavorite = async () => {
            const token = getCookie('token');
            const userId = getCookie('user_id');

            try {
                const res = await fetch('/api/parks/unfavorite', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        user_id: userId,
                        park_id: selectedParkToRemove,
                    })
                });

                if (res.ok) {
                    fetchFavorites();
                    setConfirmationVisible(false);
                } else {
                    const errorResponse = await res.json();
                    console.error('Failed to remove park from favorites:', errorResponse.error);
                }
            } catch (error) {
                console.error('An error occurred while removing park from favorites:', error);
            }
        };

        const toggleParkDetails = (parkId) => {
            setSelectedPark(selectedPark === parkId ? null : parkId);
        };

        const onDragEnd = async (result) => {
            const newFavorites = Array.from(favorites);
            const [removed] = newFavorites.splice(result.source.index, 1);
            newFavorites.splice(result.destination.index, 0, removed);

            setFavorites(newFavorites);

            const parkIds = newFavorites.map(park => park.id);

            const token = getCookie('token');
            const userId = getCookie('user_id');
            try {
                const response = await fetch('/api/parks/favorite/ranks', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        user_id: userId,
                        park_ids: parkIds
                    })
                });

                if (!response.ok) {
                    console.error('Failed to update favorite park ranks');
                }
            } catch (error) {
                console.error('An error occurred while updating favorite park ranks:', error);
            }
        };

        return (
            <div className="container my-5">
                <div className="row justify-content-center">
                    <div className="col-md-8">
                        <div id="favorites-header">
                            <h2 id="favorites-title">{favorites.length === 0 ? 'You have no favorite parks' : 'My Favorite Parks'}</h2>

                            <button
                                id="publicity-button"
                                data-testid={`publicity-button`}
                                role="button"
                                onClick={toggleUserPublicity}
                            >
                                {currentUser?.isPublic ? 'Public' : 'Private'}
                            </button>
                        </div>

                        {favorites.length > 0 && (
                            <h5 id="drag-message">Drag your favorite parks to rank them from highest to lowest</h5>
                        )}

                        {favorites.length > 0 && (
                            <DragDropContext onDragEnd={onDragEnd}>
                                <Droppable droppableId="droppable">
                                    {(provided, snapshot) => (
                                        <div
                                            {...provided.droppableProps}
                                            ref={provided.innerRef}
                                        >
                                            {favorites.map((park, index) => (
                                                <Draggable key={park.id} draggableId={park.id.toString()} index={index}>
                                                    {(provided, snapshot) => (
                                                        <div
                                                            ref={provided.innerRef}
                                                            {...provided.draggableProps}
                                                            {...provided.dragHandleProps}
                                                            data-testid={`droppable-${park.id}`}
                                                        >
                                                            <div
                                                                className={`card mb-3 park-card ${selectedPark === park.id ? 'expanded' : ''}`}
                                                                data-testid={`favorite-park-card-${park.id}`}
                                                                onClick={() => toggleParkDetails(park.id)}
                                                            >
                                                                <div className="card-body">
                                                                    <h5
                                                                        id={`favorite-park-title-${park.id}`}
                                                                        className="card-title"
                                                                    >
                                                                        {park.fullName}
                                                                    </h5>
                                                                    <h6 className="card-subtitle mb-2 text-muted">States: {park.states}</h6>
                                                                    <p className="card-text">{park.description}</p>

                                                                    <div
                                                                        ref={(el) => detailsRef.current[park.id] = el}
                                                                        data-parkid={park.id}
                                                                        className="park-details"
                                                                    >
                                                                        <p className="card-text"><strong>Address:</strong> {park.address}</p>
                                                                        <p className="card-text"><strong>Latitude:</strong> {park.latitude}</p>
                                                                        <p className="card-text"><strong>Longitude:</strong> {park.longitude}</p>
                                                                        <p className="card-text"><strong>Information Regarding
                                                                            Directions:</strong> {park.directionsInfo}
                                                                        </p>
                                                                        <p className="card-text weather-text">
                                                                            <strong>Weather:</strong> {park.weather}
                                                                        </p>
                                                                    </div>
                                                                </div>

                                                                <button
                                                                    id={`favorites-button-${park.id}`}
                                                                    data-testid={`remove-favorite-button-${park.id}`}
                                                                    className="favorites-button"
                                                                    role="button"
                                                                    onClick={(e) => {
                                                                        e.stopPropagation();
                                                                        removeFavorite(park.id);
                                                                    }}
                                                                >
                                                                    Remove from Favorites
                                                                </button>

                                                                {confirmationVisible && park.id === selectedParkToRemove && (
                                                                    <div className="confirmation-dialog remove-confirmation">
                                                                        <p className="remove-confirmation-text">Are you sure you want to remove this
                                                                            park from favorites?</p>
                                                                        <button
                                                                            className="remove-yes"
                                                                            onClick={(e) => {
                                                                                e.stopPropagation();
                                                                                handleRemoveFavorite();
                                                                            }}
                                                                        >
                                                                            Yes
                                                                        </button>
                                                                        <button
                                                                            className="remove-no"
                                                                            onClick={(e) => {
                                                                                e.stopPropagation();
                                                                                setConfirmationVisible(false);
                                                                            }}
                                                                        >
                                                                            No
                                                                        </button>
                                                                    </div>
                                                                )}
                                                            </div>
                                                        </div>
                                                    )}
                                                </Draggable>
                                            ))}
                                            {provided.placeholder}
                                        </div>
                                    )}
                                </Droppable>
                            </DragDropContext>
                        )}
                    </div>
                </div>
            </div>
        );
    }

    export default Favorites;
