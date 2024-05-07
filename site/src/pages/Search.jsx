import React, {useState, useEffect, useRef, createRef} from "react";
import { getCookie } from "../utils/cookieHelper";

function Search() {
    const [search, setSearch] = useState('');
    const [results, setResults] = useState([]);
    const [selectedPark, setSelectedPark] = useState(null);
    const [searchPerformed, setSearchPerformed] = useState(false);
    const [favorites, setFavorites] = useState([]);
    const [confirmationVisible, setConfirmationVisible] = useState(false);
    const [selectedParkToRemove, setSelectedParkToRemove] = useState(null);
    const [selectedState, setSelectedState] = useState("");
    const [selectedActivity, setSelectedActivity] = useState("");
    const [selectedAmenity, setSelectedAmenity] = useState("");
    const [amenities, setAmenities] = useState([]);
    const [activities, setActivities] = useState([]);
    const detailsRef = useRef([]);

    useEffect(() => {
        async function loadData() {
            await getActivities();
            await getAmenities();
            await fetchFavorites();
        }

        loadData();
    }, []);

    async function getActivities() {
        const res = await fetch(`api/parks/activities`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${getCookie('token')}`
            }
        });
        const response = await res.json();
        setActivities(response.data);
    }

    async function getAmenities() {
        const res = await fetch(`api/parks/amenities`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${getCookie('token')}`
            }
        });
        const response = await res.json();
        if (response.data) {
            setAmenities(response.data);
        }
    }

    const handleSearch = async (e) => {
        e.preventDefault();
        setSearchPerformed(true);
        const token = getCookie('token');
        const res = await fetch(`api/parks/search?searchName=${search}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });
        const response = await res.json();
        if (response.data && Array.isArray(response.data.parks)) {
            setResults(response.data.parks);
        } else {
            setResults([]);
        }
    };

    useEffect(() => {
        detailsRef.current = results.map((_, i) => detailsRef.current[i] || createRef());

        setSelectedPark(null);
    }, [results]);

    const toggleParkDetails = (index) => {
        if (selectedPark === index) {
            setSelectedPark(null);
        } else {
            setSelectedPark(index);
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
                if (responseData.data && responseData.data.parks) {
                    setFavorites(responseData.data.parks.map(favoritePark => favoritePark.park.id));
                }
            } catch (error) {
                console.error('Failed to fetch favorites', error);
            }
        }
    };

    const toggleFavorite = async (parkId) => {
        const token = getCookie('token');
        const userId = getCookie('user_id');

        const isFavorite = favorites.includes(parkId);

        if (!isFavorite) {
            try {
                const res = await fetch('/api/parks/favorite', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`
                    },
                    body: JSON.stringify({
                        user_id: userId,
                        park_id: parkId,
                    })
                });
                if (res.ok) {
                    fetchFavorites();
                } else {
                    const errorResponse = await res.json();
                    console.error('Failed to add park to favorites:', errorResponse.error);
                }
            } catch (error) {
                console.error('An error occurred while adding park to favorites:', error);
            }
        } else {
            setSelectedParkToRemove(parkId);
            setConfirmationVisible(true);
        }
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

    let filteredResults = results.map((result, i) => {
        // Check if the current state matches any of the park's states
        const isStateMatch = selectedState ? result.park.states.split(",").includes(selectedState) : true;

        // Check if any activity in the park matches the selected activity
        const isActivityMatch = selectedActivity ? result.activities.some(activity => `${activity.id}` === selectedActivity) : true;

        // Check if any amenity in the park matches the selected amenity
        const isAmenityMatch = selectedAmenity ? result.amenities.some(amenity => `${amenity.id}` === selectedAmenity) : true;

        if (!isStateMatch || !isActivityMatch || !isAmenityMatch) {
            return;
        }

        return (
            <div
                className={`card mb-3 park-card ${selectedPark === i ? 'expanded' : ''}`}
                data-testid={`park-card-${i}`}
                key={i}
                onClick={() => toggleParkDetails(i)}
            >
                <div className="card-body">
                    <h5 id={`park-title-${result.park.id}`} data-testid={`park-title-${result.park.id}`} className="card-title">{result.park.fullName}</h5>
                    <h6 className="card-subtitle mb-2 text-muted">States: {result.park.states}</h6>
                    <p className="card-text">{result.park.description}</p>

                    <div
                        ref={el => detailsRef.current[i] = el}
                        className="park-details"
                    >
                        <p id={`search-address-${result.park.id}`} className="card-text">
                            <strong>Address:</strong> {result.park.address}
                        </p>
                        <p id={`search-latitude-${result.park.id}`} className="card-text">
                            <strong>Latitude:</strong> {result.park.latitude}
                        </p>
                        <p id={`search-longitude-${result.park.id}`} className="card-text">
                            <strong>Longitude:</strong> {result.park.longitude}
                        </p>
                        <p id={`search-amenities-${result.park.id}`} className="card-text">
                            <strong>Amenities:</strong> {result.amenities.map(amenity => amenity.name).join(', ')}
                        </p>
                        <p id={`search-activities-${result.park.id}`} className="card-text">
                            <strong>Activities:</strong> {result.activities.map(activity => activity.name).join(', ')}
                        </p>
                        <p id={`search-directions-${result.park.id}`} className="card-text"><strong>Information
                            Regarding
                            Directions:</strong> {result.park.directionsInfo}
                        </p>
                        <p id={`search-weather-${result.park.id}`} className="card-text weather-text">
                            <strong>Weather:</strong> {result.park.weather}
                        </p>

                        <p className="card-text"><strong>Pictures</strong></p>
                        {result.parkImages && result.parkImages.length >= 3 ? (
                            <div className="park-images-container">
                                {[0, 1, 2].map(index => (
                                    <div key={index} className="park-image">
                                        <p>{result.parkImages[index].title}</p>

                                        <img
                                            src={result.parkImages[index].url}
                                            style={{width: "100%", height: "auto"}}
                                            alt={result.parkImages[index].altText}
                                            loading={"lazy"}
                                        />
                                    </div>
                                ))}
                            </div>
                        ) : (
                            <p>No sufficient images to display.</p>
                        )}
                    </div>
                </div>

                <button
                    id={`search-favorites-button-${result.park.id}`}
                    className="favorites-button"
                    role="button"
                    onClick={(e) => {
                        e.stopPropagation();
                        toggleFavorite(result.park.id);
                    }}
                >
                    {favorites.includes(result.park.id) ? 'Remove from Favorites' : 'Add to Favorites'}
                </button>

                {confirmationVisible && result.park.id === selectedParkToRemove && (
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
        );
    }).filter(result => result !== undefined); // remove filtered results

    return (
        <div className="container my-5">
            <div className="row justify-content-center">
                <div className="col-md-8">
                    <form onSubmit={handleSearch} id="search-form">
                        <div className="input-group mb-2">
                            <input
                                type="text"
                                id="search-field"
                                className="form-control"
                                placeholder="Search for parks"
                                value={search}
                                onChange={(e) => setSearch(e.target.value)}
                            />
                            <button type="submit" id="search-button" className="btn btn-primary"
                                    data-testid="search-button">Search
                            </button>
                        </div>
                        <div className="input-group">
                            <div className="input-group">
                                <select
                                    aria-label="Select State"
                                    name="state"
                                    data-testid="state"
                                    id="state"
                                    className="form-select"
                                    value={selectedState}
                                    onChange={(e) => setSelectedState(e.target.value)}
                                >
                                    <option key="placeholder" value="" disabled>State...</option>
                                    <option key="AL" value="AL">Alabama</option>
                                    <option key="AK" value="AK">Alaska</option>
                                    <option key="AZ" value="AZ">Arizona</option>
                                    <option key="AR" value="AR">Arkansas</option>
                                    <option key="CA" value="CA">California</option>
                                    <option key="CO" value="CO">Colorado</option>
                                    <option key="CT" value="CT">Connecticut</option>
                                    <option key="DE" value="DE">Delaware</option>
                                    <option key="DC" value="DC">District Of Columbia</option>
                                    <option key="FL" value="FL">Florida</option>
                                    <option key="GA" value="GA">Georgia</option>
                                    <option key="HI" value="HI">Hawaii</option>
                                    <option key="ID" value="ID">Idaho</option>
                                    <option key="IL" value="IL">Illinois</option>
                                    <option key="IN" value="IN">Indiana</option>
                                    <option key="IA" value="IA">Iowa</option>
                                    <option key="KS" value="KS">Kansas</option>
                                    <option key="KY" value="KY">Kentucky</option>
                                    <option key="LA" value="LA">Louisiana</option>
                                    <option key="ME" value="ME">Maine</option>
                                    <option key="MD" value="MD">Maryland</option>
                                    <option key="MA" value="MA">Massachusetts</option>
                                    <option key="MI" value="MI">Michigan</option>
                                    <option key="MN" value="MN">Minnesota</option>
                                    <option key="MS" value="MS">Mississippi</option>
                                    <option key="MO" value="MO">Missouri</option>
                                    <option key="MT" value="MT">Montana</option>
                                    <option key="NE" value="NE">Nebraska</option>
                                    <option key="NV" value="NV">Nevada</option>
                                    <option key="NH" value="NH">New Hampshire</option>
                                    <option key="NJ" value="NJ">New Jersey</option>
                                    <option key="NM" value="NM">New Mexico</option>
                                    <option key="NY" value="NY">New York</option>
                                    <option key="NC" value="NC">North Carolina</option>
                                    <option key="ND" value="ND">North Dakota</option>
                                    <option key="OH" value="OH">Ohio</option>
                                    <option key="OK" value="OK">Oklahoma</option>
                                    <option key="OR" value="OR">Oregon</option>
                                    <option key="PA" value="PA">Pennsylvania</option>
                                    <option key="RI" value="RI">Rhode Island</option>
                                    <option key="SC" value="SC">South Carolina</option>
                                    <option key="SD" value="SD">South Dakota</option>
                                    <option key="TN" value="TN">Tennessee</option>
                                    <option key="TX" value="TX">Texas</option>
                                    <option key="UT" value="UT">Utah</option>
                                    <option key="VT" value="VT">Vermont</option>
                                    <option key="VA" value="VA">Virginia</option>
                                    <option key="WA" value="WA">Washington</option>
                                    <option key="WV" value="WV">West Virginia</option>
                                    <option key="WI" value="WI">Wisconsin</option>
                                    <option key="WY" value="WY">Wyoming</option>
                                </select>
                                {
                                    // display activities filter
                                    <select
                                        aria-label="Select Activity"
                                        name="activity"
                                        id="activity"
                                        data-testid="activity"
                                        className="form-select ml-2"
                                        value={selectedActivity}
                                        onChange={(e) => setSelectedActivity(e.target.value)}
                                    >
                                        <option value="" key="placeholder" disabled>Activity...</option>
                                        {
                                            activities.map((activity) => {
                                                return <option key={`activity-${activity.id}`} value={activity.id}>{activity.name}</option>
                                            })
                                        }
                                    </select>
                                }
                                {
                                    // display amenities filter
                                    <select
                                        aria-label="Select Amenity"
                                        name="amenity"
                                        data-testid="amenity"
                                        id="amenity"
                                        className="form-select ml-2"
                                        value={selectedAmenity}
                                        onChange={(e) => setSelectedAmenity(e.target.value)}
                                    >
                                        <option key="placeholder" value="" disabled>Amenity...</option>
                                        {
                                            amenities.map((amenity) => {
                                                return <option key={`amenity-${amenity.id}`} value={amenity.id}>{amenity.name}</option>
                                            })
                                        }
                                    </select>
                                }
                            </div>
                        </div>
                    </form>

                    {searchPerformed && results.length === 0 && search.trim() !== '' && (
                        <h4 id="no-results-message">No results for "{search}"</h4>
                    )}

                    {results.length > 0 && (
                        <div className="results-container">
                            <h4 id="results-length">{filteredResults.length} Result(s)</h4>
                            {filteredResults}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Search;
