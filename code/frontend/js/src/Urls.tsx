export const BASE_URL_API = "http://localhost:8080"
export const API_VERSION = "/v1"

export const SIGNUP_URL_API = BASE_URL_API + API_VERSION + "/signup"

export const PERSON_PROFILE = BASE_URL_API + API_VERSION + "/profile"

export const PERSONS_URL_API = () => {
    return BASE_URL_API + API_VERSION + `/persons`
}

export const PERSON_URL_API = (personId: string | undefined): string => {
    return BASE_URL_API + API_VERSION + `/persons/${personId}`
}

export const TICKETS_URL_API = BASE_URL_API + API_VERSION + `/tickets?sort_by=name&direction=desc`

export const TICKETS_EMPLOYEE_STATES_URL_API = () => {
    return BASE_URL_API + API_VERSION + `/tickets/states`
}

export const TICKET_URL_API = (ticketId: string | undefined): string => {
    return BASE_URL_API + API_VERSION + `/tickets/${ticketId}`
}

export const COMPANIES_URL_API = BASE_URL_API + API_VERSION + `/companies`

export const COMPANY_URL_API = (companyId: string | undefined): string => {
    return BASE_URL_API + API_VERSION + `/companies/${companyId}`
} 

export const BUILDINGS_URL_API = (companyId: string | undefined): string => {
    return BASE_URL_API + API_VERSION + `/companies/${companyId}/buildings`
} 

export const BUILDING_URL_API = (companyId: string | undefined, buildingId: string | undefined): string => {
    return BASE_URL_API + API_VERSION + `/companies/${companyId}/buildings/${buildingId}`
} 

export const ROOM_URL_API = (companyId: string | undefined, buildingId: string | undefined, roomId: string | undefined): string => {
    return BASE_URL_API + API_VERSION + `/companies/${companyId}/buildings/${buildingId}/rooms/${roomId}`
} 

export const ROOM_DEVICE_URL_API = (companyId: string | undefined, buildingId: string | undefined, roomId: string | undefined, deviceId: number | undefined): string => {
    return BASE_URL_API + API_VERSION + `/companies/${companyId}/buildings/${buildingId}/rooms/${roomId}/devices/${deviceId}`
} 

export const QRCODE_URL_API = (url: string | undefined): string => {
    if(!url) return ''
    return BASE_URL_API + url
}

export const DEVICES_URL_API = BASE_URL_API + API_VERSION + `/devices`

export const DEVICE_URL_API = (deviceId: string | undefined): string => {
    return BASE_URL_API + API_VERSION + `/devices/${deviceId}`
}

export const REPORT_FORM_URL_API = (hash: string): string => {
    return BASE_URL_API + API_VERSION + `/report/${hash}`
}

export const CATEGORIES_URL_API =  BASE_URL_API + API_VERSION + `/categories`

export const BASE_URL = "http://localhost:3000"

export const PERSONS_URL = "/persons"

export const TICKETS_URL = "/tickets"

export const LOGIN_URL = "/login"

export const PROFILE_URL = "/profile"

export const TICKET_URL = (ticketId: number | undefined): string => {
    return TICKETS_URL + `/${ticketId}`
}

export const PERSON_URL = (personId: string | undefined): string => {
    return `/persons/${personId}`
}