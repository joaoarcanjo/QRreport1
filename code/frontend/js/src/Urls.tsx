export const BASE_URL_API = "http://localhost:8080"

export const PERSONS_URL_API = (sortBy: string, direction: string) => {
    return BASE_URL_API + `/v1/persons?sort_by=${sortBy}&direction=${direction}`
}

export const PERSON_URL_API = (personId: string | undefined): string => {
    return BASE_URL_API + `/v1/persons/${personId}`
}

export const TICKETS_URL_API = (sortBy: string, direction: string): string => {
    let url = BASE_URL_API + `/v1/tickets?sort_by=${sortBy}&direction=${direction}`
    return url
}

export const TICKET_URL_API = (ticketId: string | undefined): string => {
    let url = BASE_URL_API + `/v1/tickets/${ticketId}`
    return url
}

export const COMPANIES_URL_API = (): string => {
    return BASE_URL_API + `/v1/companies`
} 

export const COMPANY_URL_API = (companyId: string | undefined): string => {
    return BASE_URL_API + `/v1/companies/${companyId}`
} 

export const BUILDING_URL_API = (companyId: string | undefined, buildingId: string | undefined): string => {
    return BASE_URL_API + `/v1/companies/${companyId}/buildings/${buildingId}`
} 

export const ROOM_URL_API = (companyId: string | undefined, buildingId: string | undefined, roomId: string | undefined): string => {
    return BASE_URL_API + `/v1/companies/${companyId}/buildings/${buildingId}/rooms/${roomId}`
} 

export const ROOM_DEVICE_URL_API = (companyId: string | undefined, buildingId: string | undefined, roomId: string | undefined, deviceId: number | undefined): string => {
    return BASE_URL_API + `/v1/companies/${companyId}/buildings/${buildingId}/rooms/${roomId}/devices/${deviceId}`
} 

export const QRCODE_URL_API = (url: string | undefined): string => {
    if(!url) return ''
    return BASE_URL_API + url
}

export const DEVICES_URL_API = BASE_URL_API + `/v1/devices`

export const DEVICE_URL_API = (deviceId: string | undefined): string => {
    return BASE_URL_API + `/v1/devices/${deviceId}`
}

export const REPORT_FORM_URL_API = (hash: string): string => {
    return BASE_URL_API + `/v1/report/${hash}`
}

export const BASE_URL = "http://localhost:3000"

export const PERSONS_URL = "/persons"

export const TICKETS_URL = "/tickets"

export const PERSON_URL = (personId: string | undefined): string => {
    return `/persons/${personId}`
}