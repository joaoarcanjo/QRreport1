import { useState } from "react"
import { MdExpandLess, MdExpandMore, MdFilterList } from "react-icons/md"
import { TbArrowBigDown, TbArrowBigTop } from "react-icons/tb"
import { Link } from "react-router-dom"
import { Company } from "../Models"

export function ListCompanies() {
    
    function CompanyItemComponent({company}: {company: Company}) {

        const bgColor = company.state?.name === 'active' ? 'bg-white' : 'bg-red-100'
        
        return (
            <Link to={`/companies/${company.id}`}>
                <div className={`p-5 ${bgColor} rounded-lg border border-gray-200 shadow-md hover:bg-gray-200 divide-y space-y-4`}>  
                    <div>
                        <h5 className='mb-2 text-xl tracking-tight text-gray-900'>{company.name}</h5>
                        <p>Number of spaces: {company.numberOfBuildings}</p>
                    </div>
                </div>
            </Link>
        )
    }

    function Companies({companies}: {companies: Company[]}) {
        return (
            <div className='flex flex-col space-y-3'>
                {Array.from(companies).map((company, idx) => <CompanyItemComponent key={idx} company={company}/>)}
            </div>
        )
    }

    const mockCompanies = [
        {'id': 1, 'name': 'ISEL', 'state': { 'id': 1, 'name': 'active' }, 'numberOfBuildings': 12},
        {'id': 1, 'name': 'ISCAL', 'state': { 'id': 1, 'name': 'active' }, 'numberOfBuildings': 45},
        {'id': 1, 'name': 'AUCHAN', 'state': { 'id': 1, 'name': 'active' }, 'numberOfBuildings': 5},
        {'id': 1, 'name': 'SONAE', 'state': { 'id': 1, 'name': 'active' }, 'numberOfBuildings': 21},
    ]

    return (
        <div className='px-3 pt-3 space-y-4'>
            <h1 className='text-3xl mt-0 mb-2 text-blue-800'>Companies</h1>
            {/*<Filters/>*/}
            <Companies companies={mockCompanies}/>
            <div>
                <Link to='/createCompany'>
                    <button className='py-2 px-4 bg-green-600 hover:bg-green-700 rounded-md text-white text-sm'>
                            Add company
                    </button>
                </Link>
            </div>
        </div>
    )
}