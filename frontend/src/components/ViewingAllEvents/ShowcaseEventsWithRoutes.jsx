import React, { useState, useEffect } from 'react'
import Pagination from '@mui/material/Pagination'
import { useNavigate } from 'react-router-dom'
import EventImage from '../global/EventImage.jsx'

export default function EventPagination({ events, title, navigationLink }) {

    const pageTitle = title || []
    const [page, setPage] = useState(1)
    const itemsPerPage = 9
    const [selectedDateType, setSelectedDateType] = useState('all')
    const [searchFilter, setSearchFilter] = useState('ename')
    const [userInput, setUserInput] = useState('')
    const [filteredEvents, setFilteredEvents] = useState(events || [])
    const eventNavigationBaseURL = navigationLink || ""
    const navigate = useNavigate()

    const dateTypes = [
        { value: 'today', label: 'Today' },
        { value: 'upcoming', label: 'Upcoming' },
        { value: 'past', label: 'Past' },
    ]

    const searchTypes = [
        { value: 'oname', label: 'Organiser' },
    ]
    function formatDate(value) {
        const date = new Date(value)
        return new Date(date.getFullYear(), date.getMonth(), date.getDate())
    }


    useEffect(() => {
        let originalEvents = [...events]
        setPage(1)
        if (userInput.length === 0 && selectedDateType === 'all' && searchFilter === 'ename') {
            setFilteredEvents(originalEvents)
        } else {
            let newEventList = []
            for (let event of originalEvents) {
                let originalName = event.ename.toLowerCase()
                let originalOrganiser = event.organiser.toLowerCase()
                let lowerUserInput = userInput.toLowerCase()

                if (selectedDateType !== 'all') {
                    const today = new Date()
                    const eventDate = formatDate(event.start)
                    today.setHours(0, 0, 0, 0)
                    eventDate.setHours(0, 0, 0, 0)
                    console.log(event, eventDate, today)


                    if (selectedDateType === 'today' && eventDate.getTime() !== today.getTime()) {
                        continue
                    } else if (selectedDateType === 'upcoming' && eventDate <= today) {
                        continue
                    } else if (selectedDateType === 'past' && eventDate >= today) {
                        continue
                    }
                }
                if (searchFilter === 'ename' && (originalName.includes(lowerUserInput) || originalName == lowerUserInput)) {
                    newEventList.push(event)
                } else if (searchFilter === 'oname' && (originalOrganiser.includes(lowerUserInput) || originalOrganiser == lowerUserInput)) {
                    newEventList.push(event)
                }
            }

            setFilteredEvents(newEventList)
        }

    }, [selectedDateType, searchFilter, userInput])

    const pageCount = Math.ceil(filteredEvents.length / itemsPerPage)
    const startIndex = (page - 1) * itemsPerPage
    const currentEvents = filteredEvents.slice(startIndex, startIndex + itemsPerPage)

    const handleDateChange = (e) => {
        setSelectedDateType(e.target.value)
    }

    const handleFilterChange = (e) => {
        setSearchFilter(e.target.value)
    }
    const handleClick = (event) => () => {
    
        if (eventNavigationBaseURL === "") {
            window.alert("Sorry, please try again later")
        } else {
            navigate(`${eventNavigationBaseURL}?eventId=${event.eventId}&eventName=${event.ename}`)
        }
    }
    console.log(currentEvents)
    return (
        <div className="w-full max-w-7xl mx-auto px-4 py-8">
            <h1 className="text-2xl font-bold mb-6 text-center">{pageTitle}</h1>

            <form className="bg-white rounded-2xl shadow-md p-6 mb-8 flex flex-col md:flex-row items-center gap-4">
                <input
                    type="text"
                    placeholder="Search for specific events"
                    value={userInput}
                    onChange={(e) => setUserInput(e.target.value)}
                    className="flex-1 border border-gray-300 rounded-lg px-4 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                />

                <div className="flex flex-col md:flex-row items-center gap-4">
                    <div className="flex flex-col">
                        <label htmlFor="date-select" className="text-sm font-medium text-gray-700 mb-1">Search by date:</label>
                        <select
                            id="date-select"
                            value={selectedDateType}
                            onChange={handleDateChange}
                            className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                        >
                            <option value="all">All</option>
                            {dateTypes.map((dateType) => (
                                <option key={dateType.value} value={dateType.value}>
                                    {dateType.label}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="flex flex-col">
                        <label htmlFor="filter-select" className="text-sm font-medium text-gray-700 mb-1">Filter By:</label>
                        <select
                            id="filter-select"
                            value={searchFilter}
                            onChange={handleFilterChange}
                            className="border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-indigo-400"
                        >
                            <option value="ename">Event Name</option>
                            {searchTypes.map((searchType) => (
                                <option key={searchType.value} value={searchType.value}>
                                    {searchType.label}
                                </option>
                            ))}
                        </select>
                    </div>
                </div>
            </form>
            {filteredEvents.length === 0 ? (
                <p className="text-center text-gray-500">No events found.</p>
            ) : (
                <>
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
                        {currentEvents.map(event => {
                            const eventDate = formatDate(event.start)
                            const today = new Date()
                            today.setHours(0, 0, 0, 0)
                            eventDate.setHours(0, 0, 0, 0)
                            const isUpcoming = new Date(event.start) > Date.now()
                            const isToday = eventDate.getTime() === today.getTime()
                            return (
                                <button onClick={handleClick(event)}>
                                    <div
                                        key={event.eventId}
                                        className={`group relative rounded-2xl shadow-lg transition-all duration-300 p-6 flex flex-col items-center text-center overflow-hidden ${isToday
                                            ? `bg-gradient-to-br from-[oklch(82.8%_0.189_84.429)] to-yellow-100 border border-[oklch(82.8%_0.189_84.429)]`
                                            : isUpcoming
                                                ? "bg-gradient-to-br from-emerald-50 to-green-100 border border-emerald-200"
                                                : "bg-gradient-to-br from-gray-50 to-slate-100 border border-gray-200"
                                            }`}
                                    >
                                        <div className={`absolute top-4 left-4 px-3 py-1 rounded-full text-xs font-medium ${isToday
                                            ? `bg-gradient-to-br from-yellow-100 to-yellow-100 border border-[oklch(82.8%_0.189_84.429)]`
                                            : isUpcoming
                                                ? "bg-gradient-to-br from-green-100 to-green-100 border border-emerald-200"
                                                : "bg-gradient-to-br from-slate-100 to-slate-100 border border-gray-200"
                                            }`}>
                                            {isToday ? "Today" : isUpcoming ? "Upcoming Event" : "Past Event"}
                                        </div>

                                        <br />
                                        <h3 className="font-bold text-xl text-gray-800 line-clamp-2 leading-tight">
                                            {event.ename}
                                        </h3>

                                        <div className="relative w-full mb-4 overflow-hidden rounded-xl">
                                            <img src={`data:image/jpeg;base64,${event.image}`} alt="Event" className="w-full h-48 object-cover"/> 
                                            
                                            <div className="absolute inset-0 bg-black opacity-0"></div>
                                        </div>

                                        <div className="space-y-2">
                                            <p className="text-sm font-medium text-gray-600">
                                                By {event.organiser}
                                            </p>
                                        </div>
                                    </div>
                                </button>
                            )
                        })}
                    </div>

                    {pageCount > 1 && (
                        <div className="flex justify-center">
                            <div className="bg-white rounded-2xl shadow-lg px-6 py-4">
                                <Pagination
                                    count={pageCount}
                                    page={page}
                                    onChange={(e, value) => setPage(value)}
                                    variant="outlined"
                                    shape="rounded"
                                    color="primary"
                                    size="large"
                                    showFirstButton
                                    showLastButton
                                />
                            </div>
                        </div>
                    )}
                </>
            )}
        </div>
    )
}
