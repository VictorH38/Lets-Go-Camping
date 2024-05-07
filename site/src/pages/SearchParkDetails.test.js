import { render, fireEvent, screen, waitFor, act } from "@testing-library/react";
import '@testing-library/jest-dom';
import Search from "./Search";
import React from "react";
import userEvent from "@testing-library/user-event";

beforeEach(() => {
    global.fetch = jest.fn();
    fetch.mockClear();
});

const mockParks = {
    data: {
        parks: [
            {
                park: {
                    id: 193,
                    fullName: 'Grand Canyon National Park',
                    description: "Grand Canyon National Park, in Northern Arizona, encompasses 278 miles (447 km) of the Colorado River and adjacent uplands. Located on ancestral homeland of 11 Associated Tribes, Grand Canyon is one of the most spectacular examples of erosion anywhere in the world—unmatched in the incomparable vistas it offers visitors from the rims. The South Rim is open. The North Rim is CLOSED for the winter.",
                    states: "AZ",
                    latitude: "36.0001165336",
                    longitude: "-112.121516363",
                    address: "20 South Entrance Road, AZ 86023",
                    directionsInfo: "South Rim: Open all year, is located 60 miles north of Williams, Arizona (via route 64 from Interstate 40) and 80 miles northwest of Flagstaff (via route 180). Grand Canyon lies entirely within the state of Arizona. North Rim: Open for the season between May 15 and October 15, 2024. The North Rim is located 30 miles south of Jacob Lake on Highway 67; the actual rim of the canyon is an additional 14 miles south. Jacob Lake, AZ is located in northern Arizona on Highway 89A, not far from the Utah border.",
                    directionsURL: "http://www.nps.gov/grca/planyourvisit/directions.htm",
                    designation: "National Park",
                    weather: "This weather varies with cold winters and mild pleasant summers, moderate humidity, and considerable diurnal temperature changes at the higher elevations, with hot and drier summers at the bottom of the Grand Canyon along with cool damp winters. Summer thunderstorms and winter snowfall adds to the weather variety in this region.",
                    shortName: "Grand Canyon"
                },
                amenities: [
                    { id: 13, name: 'Baby Changing Station' },
                    { id: 14, name: 'Food/Drink - Snacks' },
                    { id: 15, name: 'ATM/Cash Machine' }
                ],
                activities: [
                    { id: 101, name: 'Hiking' },
                    { id: 102, name: 'Camping' },
                    { id: 103, name: 'Bird Watching' },
                    // assuming there are 32 activities, adding a few as example
                ],
                parkImages: [
                    { id: 1132, title: 'Grand Canyon Mather Point Sunset on the South Rim', caption: "People come from all over the world to view Grand Canyon's sunset", url: 'https://www.nps.gov/common/uploads/structured_data/3C7B12D1-1DD8-B71B-0BCE0712F9CEA155.jpg', altText: 'The canyon glows orange as people visit Mather Point, a rock outcropping that juts into Grand Canyon' },
                    { id: 1133, title: 'Grand Canyon National Park: View from Cape Royal on the North Rim', caption: 'A popular outdoor site for weddings and receptions… miles (37 km) from the North Rim developed area.', url: 'https://www.nps.gov/common/uploads/structured_data/3C7B143E-1DD8-B71B-0BD4A1EF96847292.jpg', altText: 'The Cape Royal viewpoint curves into the distance and closer rock formations jut into the canyon.' },
                    { id: 1134, title: 'Grand Canyon National Park: Desert View Watchtower (South Rim)', caption: 'The Watchtower is located at Desert View, the east…a on the South Rim of Grand Canyon National Park.', url: 'https://www.nps.gov/common/uploads/structured_data/3C7B15A4-1DD8-B71B-0BFADECB506765CC.jpg', altText: 'The Desert View Watchtower looms 70 feet into the air over a vast and dramatic view of the canyon.' },
                    // More images can be added here
                ],
                isFavorite: false
            }
        ]
    }
};

const mockActivities = {
    data: [
        {
            name: "Fishing",
            id: 1
        },
        {
            id: 2,
            name: "Not Fishing"
        }
    ]
};

const mockAmenities = {
    data: [
        {
            name: "Test Amenity",
            id: 1
        },
        {
            id: 7,
            name: "Restrooms"
        }
    ]
};

describe("Search component tests", () => {
    test("search and display parks", async () => {
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockActivities,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockAmenities,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockParks,
        });

        render(<Search />);
        const user = userEvent.setup();
        const searchInput = screen.getByPlaceholderText("Search for parks");
        const searchButton = screen.getByRole('button', { name: 'Search' });

        await act(async () => {
            await user.type(searchInput, "Test Park");
            await user.click(searchButton);
        });

        waitFor(() => {
            expect(fetch).toHaveBeenCalledTimes(1);

            expect(screen.getByText("Test Park")).toBeInTheDocument();
            expect(screen.getByText("A description of the test park")).toBeInTheDocument();
            expect(screen.getByText("States: CA")).toBeInTheDocument();
        });
    });

    test("expand and collapse park details", async () => {
        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockActivities,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockAmenities,
        });

        fetch.mockResolvedValueOnce({
            ok: true,
            json: async () => mockParks,
        });

        render(<Search />);
        const user = userEvent.setup();
        const searchInput = screen.getByPlaceholderText("Search for parks");
        const searchButton = screen.getByRole('button', { name: 'Search' });

        await act(async () => {
            await user.type(searchInput, "Test Park");
            await user.click(searchButton);
        });

        const firstParkCard = screen.getByTestId('park-card-0');
        await act(async () => {
            await user.click(firstParkCard);
        });

        waitFor( () => {
            expect(screen.queryByText("Address: 123 Park St")).toBeInTheDocument();
        });

        await act(async () => {
            await user.click(firstParkCard);
        });

        await waitFor(() => {
            expect(screen.queryByText("Address: 123 Park St")).not.toBeInTheDocument();
        });
    });
});
