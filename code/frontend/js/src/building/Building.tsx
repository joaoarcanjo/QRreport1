import { FaEdit } from "react-icons/fa"
import { Link } from "react-router-dom"
import { Room, Building, State } from "../Types";

export function BuildingRep() {

    function BuildingState({state}: {state: State}) {

        const stateColor = state.name === 'inactive' ? 'bg-red-600' : 'bg-green-600';
        const stateElement = <span className={`${stateColor} py-1 px-2 rounded text-white text-sm`}>{state.name}</span>
        
        return <span className="ml-auto">{stateElement}</span>
    }

    function BuildingInfo({building}: {building: Building}) {

        return (
            <div className='bg-white p-3 border-t-4 border-blue-900 space-y-3'>
                <div className='items-center space-y-4'>
                    <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                        <span className='text-gray-900 font-bold text-xl leading-8 my-1'>{building.name}</span>
                        <Link to = {`/updatebuilding/${building.id}`}>
                            <button className="my-1">
                                <FaEdit style= {{ color: 'blue', fontSize: "1.4em" }} /> 
                            </button>
                        </Link>
                    </div>
                    <div className='flex flex-col space-y-4'>
                        <p> Number of rooms: {building.numberOfRooms} </p>
                    </div>
                    <div> <BuildingState state={building.state}/> </div>
                </div>
            </div>
        )
    }

    function RoomItem({room} : {room: Room}) {

        const bgColor = room.state?.name === 'active' ? 'bg-white' : 'bg-red-100'
        
        return (
            <Link to={`/rooms/${room.id}`}>
                <div className={`p-5 ${bgColor} rounded-lg border border-gray-200 hover:bg-gray-200 shadow-md`}>  
                    <h5 className='text-xl font-md text-gray-900'>{room.name}</h5>
                    <p>Number of reports: {room.numberOfReports}</p>
                </div>
            </Link>
        )
    }

    function Rooms({rooms}: {rooms: Room[]}) {
        return (
            <div className='flex flex-col space-y-3'>
                {Array.from(rooms).map((comment, idx) => <RoomItem key={idx} room={comment}/>)}
            </div>
        )
    }

    const mockBuilding = {'id': 22, 'name': 'Bloco A', 'state': { 'id': 1, 'name': 'active' }, 'floors': 4, 'numberOfRooms': 5 }

    const mockRoomsValues = [
        {'id': 1, 'name': 'Secretaria', 'state': { 'id': 1, 'name': 'active' }, 'floor': 1, 'numberOfReports': 2},
        {'id': 2, 'name': 'Gabinete x', 'state': { 'id': 1, 'name': 'active' }, 'floor': 2, 'numberOfReports': 43},
        {'id': 3, 'name': 'Tesouraria', 'state': { 'id': 1, 'name': 'inactive' }, 'floor': -1, 'numberOfReports': 12},
        {'id': 4, 'name': 'Refeitório', 'state': { 'id': 1, 'name': 'active' }, 'floor': 4, 'numberOfReports': 33},
        {'id': 5, 'name': 'Sala de estudos', 'state': { 'id': 1, 'name': 'active' }, 'floor': 1, 'numberOfReports': 43},
    ]

    return (
        <div className='w-full px-3 pt-3 space-y-3'>
            <BuildingInfo building={mockBuilding}/>
            <div className='flex space-x-4'>
                <Link className='w-1/2' to='/createRoom'>
                    <button className='w-full py-2 px-4 bg-green-600 hover:bg-green-700 rounded-md text-white text-sm'>
                        Add room
                    </button>
                </Link>
                <button className='w-1/2 py-2 px-4 bg-red-700 hover:bg-red-900 rounded-md text-white text-sm'>
                    Deactivate
                </button>
            </div>
            <Rooms rooms={mockRoomsValues}/>
        </div>
    )
}