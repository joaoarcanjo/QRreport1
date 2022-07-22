import { useState } from "react";
import { AiFillCloseCircle } from "react-icons/ai";
import { MdFilterList } from "react-icons/md";
import { TbArrowBigTop, TbArrowBigDown } from "react-icons/tb";
import { CloseButton } from "../../components/Various";
import { ACTIVE_ROLE } from "../../user/Session";
import { ShowEmployeeStates } from "./ShowEmployeeStates";
import { ShowPossibleBuildings } from "./ShowPossibleBuildings";
import { ShowPossibleCompanies } from "./ShowPossibleCompanies";

function removeParameterFromUrl(url: string, parameter: string) {

    url = url.replace(new RegExp( "\\b" + parameter + "=[^&;]+[&;]?", "gi" ), "" ); 
    url = url.replace( /[&;]$/, "" );
    return url;
}

function changeAddParameter(url: string, parameterName: string, newValue: string) {
    let auxUrl = url
    if(url.includes(parameterName)) 
        auxUrl = removeParameterFromUrl(auxUrl, parameterName)
    return auxUrl + `&${parameterName}=${newValue}`
}

export function RemoveFilters({url, setCurrentUrl}: {url: string, setCurrentUrl: React.Dispatch<React.SetStateAction<string>>}) {
    
    const filtersIncluded = []
    if(url.includes('employeeState')) filtersIncluded.push({name: 'State', value: 'employeeState'})
    if(url.includes('company')) filtersIncluded.push({name: 'Company', value: 'company'})
    if(url.includes('building')) filtersIncluded.push({name: 'Building', value: 'building'})
    if(url.includes('sortBy')) filtersIncluded.push({name: 'Sort by', value: 'sortBy'})
    if(url.includes('direction')) filtersIncluded.push({name: 'Direction', value: 'direction'})

    if(filtersIncluded.length === 0) return <></>
    return (
        <div className="flex flex-wrap space-x-1 space-y-1 bg-gray-200 rounded px-1 py-1">{Array.from(filtersIncluded).map((filter, idx) => {
            return <button className={'flex space-x-1 px-1 py-1 text-xs text-center text-white bg-blue-700 rounded-lg hover:bg-blue-800'} key={idx} onClick={() => setCurrentUrl(removeParameterFromUrl(url, filter.value))}>
                <div>{filter.name}</div>
                <div><AiFillCloseCircle style= {{ color: '#db2a0a', fontSize: "1.4em" }}/></div>
            </button>})}
        </div>
    )
}

export function Filters({currentUrl, setCurrentUrl}: {currentUrl: string, setCurrentUrl: React.Dispatch<React.SetStateAction<string>>}) {

    const [auxUrl, setAuxUrl] = useState(currentUrl)
    const [direction, setDirection] = useState<string>('desc')
    const [sortBy, setSortBy] = useState<string>('date')
    const [employeeState, setEmployeeState] = useState<any>()
    const [company, setCompany] = useState<any>()
    const [building, setBuilding] = useState<any>()
    const [showFilters, setShowFilters] = useState(false)
    const isUser = sessionStorage.getItem(ACTIVE_ROLE) === 'user'

    const onClickHandler = () => {
        let url = auxUrl
        if (employeeState !== undefined) url = changeAddParameter(url, 'employeeState', employeeState.id)
        if (company !== undefined) url = changeAddParameter(url, 'company', company.id)
        if (building !== undefined) url = changeAddParameter(url, 'building', building.id)
        url = changeAddParameter(url, 'sortBy', sortBy)
        url = changeAddParameter(url, 'direction', direction)
        setCurrentUrl(url)
    }

    return (
        <div>
            {!showFilters && 
            <button className='bg-blue-800 hover:bg-blue-900 text-white font-bold rounded-lg text-sm px-5 h-12 inline-flex items-center' onClick= {() =>setShowFilters(true) }>
                Filters <MdFilterList style= {{ color: 'white', fontSize: '2em' }} />
            </button>}
            {showFilters && 
            <div className="space-y-4 p-3 bg-white rounded-lg border border-gray-200">
                <CloseButton onClickHandler={() => setShowFilters(false)}/>
                <div>
                    <RemoveFilters url={auxUrl} setCurrentUrl={setAuxUrl}/>
                </div>
                <div className='flex w-full gap-4'>
                    <select className='border rounded-lg' onChange={value => setSortBy(value.target.value)}>
                        <option value='date'>Date</option>
                        <option value='name'>Name</option>
                    </select>       
                    <button 
                        className='text-white font-bold rounded-lg text-sm px-5 h-12 inline-flex items-center'
                        onClick= {() => { setDirection(direction === 'desc' ? 'asc' : 'desc') }}>
                        {direction === 'asc' && <TbArrowBigTop style= {{ color: 'blue', fontSize: '2.5em' }} />}
                        {direction === 'desc' && <TbArrowBigDown style= {{ color: 'blue', fontSize: '2.5em' }} />}
                    </button>     
                </div>  
                {!isUser &&
                    <div className="space-y-2">
                        <ShowEmployeeStates setPayload={setEmployeeState} currentState={employeeState} />
                        <ShowPossibleCompanies setPayload={setCompany} currentCompany={company}/>
                        <ShowPossibleBuildings setPayload={setBuilding} company={company} currentBuilding={building}/> 
                    </div>
                }
                <button className='w-full bg-green-400 hover:bg-green-600 text-white font-bold rounded-lg text-sm px-2 h-8 content-center' onClick= {onClickHandler}>
                    Apply filters
                </button>
            </div>}
        </div>  
    )
}