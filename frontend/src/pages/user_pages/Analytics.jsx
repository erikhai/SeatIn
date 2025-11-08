import React, { useEffect, useState } from 'react';
import axios from 'axios';
import Landing from '../general_pages/Landing';
import {useLocation, useNavigate } from 'react-router-dom';
import LoadingScreen from '../../components/global/LoadingScreen';
import {BarChart, PieChart, Gauge, RadarChart} from '@mui/x-charts'

const Analytics = () => {
    // Store whether user is authorized to view page or not
    const[isAuthorized, setIsAuthorized] = useState(null);

    // Store whether event is completed or not
    const[isRunning, setIsRunning] = useState(false);

    //Allow setting the non-graphical analytics at the top of the page
    const [totalSeats, setTotalSeats] = useState(0);
    const [booked, setAmountBooked] = useState(0);
    const [remaining, setRemaining] = useState(0);
    const [revenue, setRevenue] = useState(0);

    //Store data required to create the graphs
    const[seatTiersTotal, setSeatTiersTotal] = useState(new Map());
    const[seatTierBooked, setTierBooked] = useState(new Map());
    const[seatTierRevenue, setSeatTierRevenue] = useState(new Map());
    const[seatTierTotalRevenue, setSeatTierTotalRevenue] = useState(new Map());

    //Store data for the chart
    const[barChartInfo, setBarChartInfo] = useState([]);
    const[pieChartInfo, setPieChartInfo] = useState([]);
    const[guageOneInfo, setGaugeOneInfo] = useState(0);
    const[guageTwoInfo, setGaugeTwoInfo] = useState(0);
    const[radarChartInfo, setRadarChartInfo] = useState([]);

    //Store data for the most sold and revenue
    const[mostSoldInfo, setMostSoldInfo] = useState(null);
    const[mostRevenueInfo, setMostRevenueInfo] = useState(null);

    const navigate = useNavigate();

    const BASE_URL = process.env.REACT_APP_BASE_URL;

    const location = useLocation();
    const query = new URLSearchParams(location.search);
    //Fetch the eventId so we can load it
    const eventId = query.get('eventId'); 
    const eventName = query.get('eventName');

    const cancelEvent = async(e) =>{
        e.preventDefault()
        try {
            const token = localStorage.getItem('accessToken');
            const res = await axios.post(`${BASE_URL}/event/delete-event`, {}, {
            headers: { 'Authorization': `Bearer ${token}` },
            params: { eventId }
            });

            alert("Event Cancelled")
            navigate('/')
        } catch (err) {
            console.log("Error fetching seats...", err);
        }
        };

    useEffect(() => {
        const fetch_analytics = async(event_id) => {
            try {
                console.log(BASE_URL);
                //Using JWT token retrieve analytics
                const token = localStorage.getItem("accessToken");
                console.log(token);
                const request = await axios.get(`${BASE_URL}/event/analytics/` + event_id, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                const event_status = await axios.get(`${BASE_URL}/event/get-event-status/` + event_id, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                //Update status of event
                setIsRunning(event_status.data);
                const analytics = request.data;
                // Store statistics of top view
                let total_seats = 0;
                let amount_booked = 0;
                let revenue = 0;
                let tier_price = 0;
                let tier_sales = 0;
                let tier_total = 0;
                let potentialRevenue = 0;

                // Store statistics of graphs
                let tiersTotal = new Map(seatTiersTotal);
                let tierBooked = new Map(seatTierBooked);
                let tierRevenue = new Map(seatTierRevenue);
                let tierTotalRevenue = new Map(seatTierTotalRevenue);

                //Since request passes in hashmap of analytics iterate through key value pairs where key is tier and values is info
                for(const tier in analytics) {
                    // Each tier has its sub-analytics which we must process
                    for(const tier_analytics in analytics[tier]) {
                        if(tier_analytics === "Seats") {
                            tier_total = analytics[tier][tier_analytics];
                            total_seats += tier_total;
                            tiersTotal.set(tier,tier_total);
                        }
                        if(tier_analytics === "Booked") {
                            tier_sales = analytics[tier][tier_analytics];
                            amount_booked += tier_sales;
                            tierBooked.set(tier,tier_sales);

                        }
                        if(tier_analytics === "Price") {
                            tier_price = analytics[tier][tier_analytics];
                        }
                    }
                    revenue += tier_sales * tier_price;
                    potentialRevenue += tier_total * tier_price;
                    tierRevenue.set(tier, tier_sales * tier_price);
                    tierTotalRevenue.set(tier, tier_total * tier_price);
                }
                // Set all required data
                setTotalSeats(total_seats);
                setAmountBooked(amount_booked);
                setGaugeTwoInfo(parseInt(amount_booked/total_seats*100));
                setGaugeOneInfo(parseInt(revenue/potentialRevenue*100));
                setRemaining(total_seats-amount_booked);
                setRevenue(revenue);
                setSeatTierRevenue(tierRevenue);
                setTierBooked(tierBooked);
                setSeatTiersTotal(tiersTotal);
                setSeatTierTotalRevenue(tierTotalRevenue);
                setIsAuthorized(true);
            } catch {
                console.log("Error fetching Analytics...");
                setIsAuthorized(false);
            }
        }
        fetch_analytics(eventId);
    },[]);

    console.log(eventName);

    useEffect(() => {
        const prepareDataForCharts = () => {
            let barChartMap = [];
            console.log(seatTierBooked);
            let max_booked = 0; let max_tier = null;
            for(const [tier,amountBooked] of seatTierBooked) {
                if(amountBooked > max_booked) {
                    max_tier = tier;
                    max_booked = amountBooked;
                }
                let tempMap = new Map();
                // First add the amount booked
                tempMap.set("data", [amountBooked]);
                tempMap.set("stack", tier );
                tempMap.set("label", tier);
                // Must convert it to object so MUI can work with it 
                barChartMap.push(Object.fromEntries(tempMap));
                let tempMapTwo = new Map();
                // Now add the remaining
                tempMapTwo.set("data", [seatTiersTotal.get(tier)-amountBooked]);
                tempMapTwo.set("stack", tier);
                tempMapTwo.set("label",tier + " Left");
                tempMapTwo.set("color", '#D3D3D3');
                // Must convert it to object such that MUI can work with it
                barChartMap.push(Object.fromEntries(tempMapTwo));
            }
            setBarChartInfo(barChartMap);
            setMostSoldInfo(max_tier);
            let maxRevenueTier = null; 
            let maxRevenue = 0;
            //Now generate the pieChart
            let pieChartMap = []
            for(const[tier,tierRevenue] of seatTierRevenue) {
                if(tierRevenue > maxRevenue) {
                    maxRevenueTier = tier;
                    maxRevenue = tierRevenue;
                }
                let tempMap = new Map();
                //Convert the revenue to % of total revenue
                tempMap.set("value", parseInt((tierRevenue/revenue) * 100));
                tempMap.set("label",tier);
                // Convert it to object such that MUI it is compatible with it
                pieChartMap.push(Object.fromEntries(tempMap));
            }
            setPieChartInfo(pieChartMap);
            setMostRevenueInfo(maxRevenueTier);
            //Now get values for radar chart
            let radarChartMap = []
            for(const[tier,tierSold] of seatTierBooked) {
                let tempMap = new Map();
                //Compute sell percentage of each attribute
                tempMap.set("label", tier);
                let radarValues = [];
                //Add in % sold
                radarValues.push(tierSold/seatTiersTotal.get(tier));
                //Add in revenue % sold
                radarValues.push(seatTierRevenue.get(tier)/seatTierTotalRevenue.get(tier));
                tempMap.set("data", radarValues);
                radarChartMap.push(Object.fromEntries(tempMap));
            }
            setRadarChartInfo(radarChartMap);
        }
        prepareDataForCharts();
    }, [seatTierBooked, seatTierTotalRevenue])
    // If still processing return loading screen
    if(isAuthorized === null) {
        return <LoadingScreen/>
    }
    // If not valud return user to landing page
    else if(isAuthorized === false) {
        return <Landing/>
    }
    return (
        <>
        <div className='flex flex-col h-screen'>
        <div className='flex mt[1%] justify-center'>
            <h1 className='text-3xl'><b>{eventName}</b></h1>
        </div>

        <div className='flex justify-center'>
            <h1>Event Analytics and Insights</h1>
        </div>

        <div className='flex space-x-10 justify-center h-1/6'>
            <div className='flex flex-col  w-1/4 h-lg shadow-2xl pl-[2%]'>
                <div>
                    <b className='text-2xl'>Total Seats</b>
                </div>
                <div className='flex items-center w-full h-1/2'>
                    <div className='flex w-1/2'>
                        <b className='text-left text-5xl'>{totalSeats}</b>
                    </div>
                    <div className='flex justify-end w-1/2 pr-[4%]'>
                        <img className = 'flex max-w-10 max-h-10' src='/business.png' alt="test"></img>
                    </div>
                </div>
            </div>
            <div className='flex flex-col items-start w-1/4 h-lg shadow-2xl pl-[2%]'>
                <div>
                    <b className='text-2xl'>Booked</b>
                </div>
                <div className='flex items-center w-full h-1/2'>
                    <div className='flex w-1/2'>
                        <b className='text-left text-5xl'>{booked}</b>
                    </div>
                    <div className='flex justify-end w-1/2 pr-[4%]'>
                        <img className = 'max-w-10 max-h-10' src='/calendar.png' alt="test"></img>
                    </div>
                </div>
            </div>
            <div className='flex flex-col items-start w-1/4 h-lg shadow-2xl pl-[2%]'>
                <div>
                    <b className='text-2xl'>Remaining</b>
                </div>
                <div className='flex items-center w-full h-1/2'>
                    <div className='flex w-1/2'>
                        <b className='text-left text-5xl'>{remaining}</b>
                    </div>
                    <div className='flex justify-end w-1/2 pr-[4%]'>
                        <img className = 'max-w-10 max-h-10' src='/user.png' alt="test"></img>
                    </div>
                </div>
            </div>
            <div className='flex flex-col items-start w-1/4 h-lg shadow-2xl pl-[2%]'>
                <div>
                    <b className='text-2xl'>Revenue</b>
                </div>
                <div className='flex items-center w-full h-1/2'>
                    {/*Ensure revenue can handle cases where it could potentially exceed flex box length */}
                    <div className='flex w-4/5 overflow-hidden'>
                        <b className='text-left text-5xl'>{revenue}</b>
                    </div>
                    <div className='flex justify-end w-1/5 pr-[4%]'>
                        <img className = 'max-w-10 max-h-10' src='/dollar-symbol.png' alt="test"></img>
                    </div>
                </div>
            </div>
        </div>

        <div className='flex mt-[2%] justify-center space-x-10 w-[100%]'  >
            <div className='flex items-start min-w-[50%] max-w-[50%] overflow-hidden h-lg shadow-2xl items-stretch'>
                <div className='w-1/2 max-w-[1/5]'>
                    <BarChart
                    xAxis={[{data: ['Seats Booked']}]}
                    series={barChartInfo}
                hideLegend
                height={300}
                width={window.width}
                />
                </div>

                <div className='flex flex-col justify-center items-center w-1/2'>
                    <b className='text-2xl text-center'>Most Sold Seating Tier:</b>
                    <br></br>
                    <b className='text-4xl text-center'>{mostSoldInfo || "None"}</b>
                </div>
               
            </div>

            <div className='flex items-start min-w-[50%] max-w-[50%] shadow-2xl pl-[2%] items-stretch'>
                <div className='w-1/2'>
                {/*Pie-Chart will display revenue by category if no revenue display no chart*/}
                {revenue === 0 && <PieChart series={
                    [{data: []}]} 
                    width={300} 
                    height={300}
                />}
                {/*If has data display what was computed above*/}
                {revenue != 0 && <PieChart
                series={[{data: pieChartInfo}]}
                width={window.width}
                height={300}
                />}
                </div>

                <div className='flex flex-col justify-center items-center w-1/2 overflow-hidden'>
                    <b className='text-2xl text-center'>Most Revenue:</b>
                    <br></br>
                    <b className='flex text-4xl text-center'>{mostRevenueInfo || "None"}</b>
                </div>

               
             </div>
        </div>

        <div className='flex mt-[2%] h-1/4 w-100'>
             <div className='flex w-3/4 justify-center items-center shadow-2xl items-stretch h-[100%]'>
                <div className='flex w-1/3'>
                    <Gauge value={guageOneInfo} text='Revenue' />
                </div>
                <div className='flex w-1/3'>
                    <Gauge value={guageTwoInfo} text='Sold' />
                </div>
                <div className='flex h-[100%] w-1/3 items-center justify-center'>
                {/*When creating radarchart normalize values to 1 to ensure consistency*/}
                <RadarChart
                shape="circular"
                series={
                    radarChartInfo
                    }
                    radar={{
                    metrics: ['Sold', 'Revenue'],
                    max: 1
                }}
                />
                </div>

            </div>

            {/* If event is not complete still give users option to cancel event */}
            {isRunning == true && 
            <div className='flex items-center justify-center w-1/4 pl-[2%]'>
            <button className=" bg-red-500 text-white rounded w-[75%] h-1/2" onClick={cancelEvent}>
                Cancel Event
            </button>
            </div>}

            {/* If event is complete do not allow them to cancel event */}
            {isRunning == false &&
            <div className='flex items-center justify-center w-1/4 pl-[2%]'>
            <button className=" bg-gray-500 text-white rounded w-[75%] h-1/2">
                Event Complete
            </button>
            </div>}
        </div>
        </div>
        </>
    )
}

export default Analytics