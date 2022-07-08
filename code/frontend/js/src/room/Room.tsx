import { useState } from "react";
import { FaEdit } from "react-icons/fa";
import { Link } from "react-router-dom";
import Popup from "reactjs-popup";
import { Device, Room, State } from "../Models";
import './Popup.css';

export function RoomRep() {
    
    const [popup, setPopup] = useState(false);

    function PopupComp() {
        return (
            <Popup className='popup-overlay' open = {popup} modal>
                <div className='w-full p-16'>
                    <div className='bg-white p-8 space-y-3'>
                        <div className="image overflow-hidden">
                            <img className="h-auto w-full mx-auto"
                                src="https://br.qr-code-generator.com/wp-content/themes/qr/new_structure/markets/core_market/generator/dist/generator/assets/images/websiteQRCode_noFrame.png"
                                alt=""/>
                        </div>
                    </div>
                </div>
            </Popup>
        )
    }

    function RoomState({state}: {state: State}) {

        const stateColor = state.name === 'inactive' ? 'bg-red-600' : 'bg-green-600';
        const stateElement = <span className={`${stateColor} py-1 px-2 rounded text-white text-sm`}>{state.name}</span>
        
        return <span className="ml-auto">{stateElement}</span>
    }

    function RoomInfo({room}: {room: Room}) {

        return (
            <div className='bg-white p-3 border-t-4 border-blue-900 space-y-3'>
                <div className='items-center space-y-4'>
                    <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                        <span className='text-gray-900 font-bold text-xl leading-8 my-1'>{room.name}</span>
                        <Link to = {`/updateRoom/${room.id}`}>
                            <button className="my-1">
                                <FaEdit style= {{ color: 'blue', fontSize: "1.4em" }} /> 
                            </button>
                        </Link>
                    </div>
                    <div className='flex flex-col space-y-4'>
                        <p> Number of reports: {room.numberOfReports} </p>
                    </div>
                    <div> <RoomState state={room.state}/> </div>
                </div>
            </div>
        )
    }

    function DeviceItem({device}: {device: Device}) {

        const bgColor = device.state?.name === 'active' ? 'bg-white' : 'bg-red-100'
        
        return (
            <div className={`p-5 ${bgColor} rounded-lg border border-gray-200 shadow-md`}> 
                <div className="flex items-center">
                    <span className='text-xl font-md text-gray-900'>{device.name}</span>
                    {device.state.name === 'active' ? 
                    <button className={'bg-slate-400 ml-auto py-4 px-4 rounded text-white text-sm'}
                        onClick={()=> setPopup(!popup)}> Generate new QRcode</button> : ''}
                </div>
            </div>
        )
    }

    function Devices({devices}: {devices: Device[]}) {
        return (
            <div className='flex flex-col space-y-4'>
                {Array.from(devices).map((device, idx) => <DeviceItem key={idx} device={device}/>)}
            </div>
        )
    }

    const mockRoom = {'id': 1, 'name': 'Secretaria', 'state': { 'id': 1, 'name': 'active' }, 'floor': 1, 'numberOfReports': 2}

    const mockDevicesValues = [
        {'id': 1, 'name': 'Toilet 1', 'state': { 'id': 1, 'name': 'active' }},
        {'id': 2, 'name': 'Door', 'state': { 'id': 1, 'name': 'active' }},
        {'id': 3, 'name': 'Lamp', 'state': { 'id': 1, 'name': 'inactive'}},
        {'id': 4, 'name': 'Faucet', 'state': { 'id': 1, 'name': 'active' }}
    ]

    return (
        <div>
            <div className='w-full px-3 pt-3 space-y-3'>
                <RoomInfo room={mockRoom}/>
                <div className='flex space-x-4'>
                    <Link className='w-1/2' to='/addDevice'>
                        <button className='w-full py-2 px-4 bg-green-600 hover:bg-green-700 rounded-md text-white text-sm'>
                            Add device
                        </button>
                    </Link>
                    <button className='w-1/2 py-2 px-4 bg-red-700 hover:bg-red-900 rounded-md text-white text-sm'>
                        Deactivate
                    </button>
                </div>
                <Devices devices={mockDevicesValues}/>
                <PopupComp/>
            </div>
        </div>
    )
}
