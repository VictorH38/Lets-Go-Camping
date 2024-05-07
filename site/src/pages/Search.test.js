import { render, screen, waitFor, act } from "@testing-library/react";
import '@testing-library/jest-dom';
import userEvent from '@testing-library/user-event';
import Search from "./Search";
import React from "react";
import {AuthProvider} from "../AuthContext";
import {MemoryRouter} from "react-router-dom";

beforeEach(() => {
    global.fetch = jest.fn();
    fetch.mockClear();

    fetch.mockImplementation((url, options) => {
        if (url.includes('api/parks/search?searchName=Test Park')) {
            return Promise.resolve({
                ok: true,
                json: () => Promise.resolve({ data: {
                    parks: [1],
                    amenities: [1],
                    activities: [1]
                }})
            });
        } else if (url.endsWith('/api/parks/favorite')) {
            if (options.method === 'POST') {
                return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve({ data: {} }),
                });
            }
        } else if (url.includes('/api/parks/favorite/')) {
            return Promise.resolve({
                ok: true,
                json: () => Promise.resolve({ data: { parks: [1] } }),
            });
        } else if (url.includes('api/parks/amenities')) {
            return Promise.resolve({
                ok: true,
                json: () => Promise.resolve({
                    data: [
                        {
                            name: "Test Amenity",
                            id: 1
                        },
                        {
                            id: 7,
                            name: "Restrooms"
                        }
                    ] }),
            });
        } else if (url.includes('api/parks/activities')) {
            return Promise.resolve({
                ok: true,
                json: () => Promise.resolve({
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
                }),
            });
        } else if(url.includes('api/parks/search?searchName=Grand Canyon National Park')) {
           return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve({
                        data: {
                            parks: [
                                {
                                    park: {
                                        id: 1,
                                        fullName: `Grand Canyon National Park`,
                                        states: "AZ,NV"
                                    },
                                    activities: [
                                        {
                                            id: 2,
                                            name: "Not Fishing"
                                        }
                                    ],
                                    amenities: [
                                        {
                                            id: 7,
                                            name: "Restrooms"
                                        }
                                    ]
                                }
                            ]
                        }
                    })
                });
        } if(url.includes('api/parks/search?searchName=Nonexistent Park')) {
            return Promise.resolve({
                ok: true,
                json: () => Promise.resolve({
                    data: {
                        parks: []
                    }
                })
            });
        } else if(url.includes('api/parks/search?searchName=')) {
            return Promise.resolve({
                    ok: true,
                    json: () => Promise.resolve({
                        data: {
                            parks: new Array(471).fill({}).map((_, i) => {

                                // by default, this park will not match test filters due to not matching state
                                let $states = "A,B,C";
                                let $activities = [
                                    {
                                        id: 2,
                                        name: "Not Fishing"
                                    }
                                ];
                                let $amenities = [
                                    {
                                        id: 7,
                                        name: "Restrooms"
                                    }
                                ];

                                if(i === 20) {

                                    // this park will be included in filtered results
                                    $states = "CA,B,C";
                                    $amenities.push({
                                        id: 1,
                                        name: "Test Amenity"
                                    })
                                    $activities.push({
                                        id: 1,
                                        name: "Fishing"
                                    })
                                } else if(i === 25) {

                                    // this park will be filtered out due to no matching amenities
                                    $states = "CA,B,C";
                                    $activities.push({
                                        id: 1,
                                        name: "Fishing"
                                    })

                                } else if(i === 30) {

                                    // this park will be filtered out due to no matching activities
                                    $states = "CA,B,C";
                                    $amenities.push({
                                        id: 1,
                                        name: "Test Amenity"
                                    });
                                }

                                return {
                                    park: {
                                        id: i + 1,
                                        fullName: `Park ${i + 1}`,
                                        states: $states
                                    },
                                    activities: $activities,
                                    amenities: $amenities
                                }
                            })
                        }
                    })
                });
        }
        return Promise.reject(new Error(`Unexpected URL ${url} in fetch mock`));
    });
});

describe("Search component tests", () => {

    test("renders the search input and button", async () => {
        render(
            <MemoryRouter>
                <AuthProvider>
                    <Search />
                </AuthProvider>
            </MemoryRouter>
        );

        await act(async() => {
            const searchInput = await screen.getByPlaceholderText("Search for parks");
            const searchButton = await screen.getByRole('button', { name: 'Search' });

            await waitFor(() => {
                expect(searchInput).toBeInTheDocument();
                expect(searchButton).toBeInTheDocument();
            });
        });
    });

    test("displays '1 Result(s)' for specific search 'Grand Canyon National Park'", async () => {
        render(
            <MemoryRouter>
                <AuthProvider>
                    <Search />
                </AuthProvider>
            </MemoryRouter>
        );
        const user = userEvent.setup();

        await act(async () => {
            const searchInput = await screen.getByPlaceholderText("Search for parks");
            const searchButton = await screen.getByRole('button', { name: 'Search' });

            await user.type(searchInput, "Grand Canyon National Park");
            await user.click(searchButton);
        });

        await waitFor(async() => expect(await screen.getByText("1 Result(s)")).toBeInTheDocument());
    });

    test("displays '471 Result(s)' when user types nothing and searches", async () => {
        render(
            <MemoryRouter>
                <AuthProvider>
                    <Search />
                </AuthProvider>
            </MemoryRouter>
        );
        const user = userEvent.setup();

        await act(async () => {
            const searchButton = await screen.getByRole('button', { name: 'Search' });
            await user.click(searchButton);
        });

        await waitFor(async() => expect(await screen.getByText("471 Result(s)")).toBeInTheDocument());
    });

    test("handle search filtering", async () => {

        render(
            <MemoryRouter>
                <AuthProvider>
                    <Search />
                </AuthProvider>
            </MemoryRouter>
        );
        const user = userEvent.setup();
      
        // make sure activities/amenities are loaded
        await waitFor(async() => {
            expect(await screen.getByText("Fishing").selected).toBe(false);
        });
        await waitFor(async() => {
            expect(await screen.getByText("Test Amenity").selected).toBe(false);
        });

        await act(async () => {
            const searchButton = await screen.getByRole('button', { name: 'Search' });
            await user.click(searchButton);

            // apply state filter
            await userEvent.selectOptions(
                // Find the select element
                await screen.getByTestId("state"),
                // Find and select the CA option
                await screen.getByText("California")
            )

            // apply activity filter
            await userEvent.selectOptions(
                // Find the select element
                await screen.getByTestId("activity"),
                // Find and select the fishing option
                await screen.getByText("Fishing")
            )


            // apply amenity filter
            await userEvent.selectOptions(
                // Find the select element
                await screen.getByTestId("amenity"),
                // Find and select the test option
                await screen.getByText("Test Amenity")
            )
        });

        // make sure options are selected
        await waitFor(async() => expect(await screen.getByText("California").selected).toBe(true));
        await waitFor(async() => expect(await screen.getByText("Fishing").selected).toBe(true));
        await waitFor(async() => expect(await screen.getByText("Test Amenity").selected).toBe(true));
        await waitFor(async() => expect(await screen.getByText("1 Result(s)")).toBeInTheDocument());
    });

    test("handle empty search results", async () => {

        render(
            <MemoryRouter>
                <AuthProvider>
                    <Search />
                </AuthProvider>
            </MemoryRouter>
        );
        const user = userEvent.setup();
        const searchInput = screen.getByPlaceholderText("Search for parks");
        const searchButton = screen.getByRole('button', { name: 'Search' });

        await act(async () => {
            await user.type(searchInput, "Nonexistent Park");
            await user.click(searchButton);
        });

        await waitFor(async() => await expect(fetch).toHaveBeenCalledTimes(3));

        const noResultsMessage = screen.getByText(`No results for "Nonexistent Park"`);
        await waitFor(() => {
            expect(noResultsMessage).toBeInTheDocument();
            expect(screen.queryByText("Test Park")).not.toBeInTheDocument();
        });
    });
});
