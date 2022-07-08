import { FaEdit } from "react-icons/fa"
import { Link } from "react-router-dom"
import { Building, State, Company } from "../Models";

export function CompanyRep() {

    /* Main user info section */
    function CompanyState({state}: {state: State}) {

        const stateColor = state.name === 'inactive' ? 'bg-red-600' : 'bg-green-600';
        const stateElement = <span className={`${stateColor} py-1 px-2 rounded text-white text-sm`}>{state.name}</span>
        
        return <span className="ml-auto">{stateElement}</span>
    }

    function CompanyInfo({company}: {company: Company}) {

        return (
            <div className='bg-white p-3 border-t-4 border-blue-900 space-y-3'>
                <div className='items-center space-y-4'>
                    <div className="flex items-center space-x-2 font-semibold text-gray-900 leading-8">
                        <span className='text-gray-900 font-bold text-xl leading-8 my-1'>{company.name}</span>
                        <Link to = {`/updatecompany/${company.id}`}>
                            <button className="my-1">
                                <FaEdit style= {{ color: 'blue', fontSize: "1.4em" }} /> 
                            </button>
                        </Link>
                    </div>
                    <div className='flex flex-col space-y-4'>
                        <p> Number of buildings: {company.numberOfBuildings} </p>
                    </div>
                    <div> <CompanyState state={company.state}/> </div>
                </div>
            </div>
        )
    }

    function BuildingItem({building} : {building: Building}) {

        const bgColor = building.state?.name === 'active' ? 'bg-white' : 'bg-red-100'
        
        return (
            <Link to={`/buildings/${building.id}`}>
                <div className={`p-5 ${bgColor} rounded-lg border border-gray-200 shadow-md hover:bg-gray-200 divide-y space-y-4`}> 
                    <h5 className='text-xl font-md text-gray-900'>{building.name}</h5>
                    <p>Number of rooms: {building.numberOfRooms}</p>
                </div>
            </Link> 
        )
    }

    function Buildings({buildings}: {buildings: Building[]}) {
        return (
            <div className='flex flex-col space-y-3'>
                {Array.from(buildings).map((comment, idx) => <BuildingItem key={idx} building={comment}/>)}
            </div>
        )
    }

    const mockCompany = {'id': 22, 'name': 'ISEL', 'state': { 'id': 1, 'name': 'active' }, 'numberOfBuildings': 5 }

    const mockValues = [
        {'id': 1, 'name': 'Bloco D', 'floors': 1, 'state': { 'id': 1, 'name': 'active' }, 'numberOfSpaces': 423 },
        {'id': 2, 'name': 'Bloco B', 'floors': 2, 'state': { 'id': 1, 'name': 'inactive' }, 'numberOfSpaces': 123 },
        {'id': 3, 'name': 'Bloco G', 'floors': 5, 'state': { 'id': 1, 'name': 'active' }, 'numberOfSpaces': 42 },
        {'id': 4, 'name': 'Bloco F', 'floors': 5, 'state': { 'id': 1, 'name': 'inactive' }, 'numberOfSpaces': 44 },
        {'id': 5, 'name': 'Bloco A', 'floors': 3, 'state': { 'id': 1, 'name': 'active' }, 'numberOfSpaces': 231 }
    ]

    return (
        <div className='w-full px-3 pt-3 space-y-3'>
            <CompanyInfo company={mockCompany}/>
            <div className='flex space-x-4'>
                <Link className='w-full' to='/createBuilding'> {/*O componente recebe o id da company, como depois vamos retirar estes links, é tranquilo*/}
                    <button className='w-full py-2 px-4 bg-green-600 hover:bg-green-700 rounded-md text-white text-sm'>
                            Add building
                    </button>
                </Link>
                <button className='w-full py-2 px-4 bg-red-700 hover:bg-red-900 rounded-md text-white text-sm'>
                    Deactivate
                </button>
            </div>
            <Buildings buildings={mockValues}/>
        </div>
    )
}