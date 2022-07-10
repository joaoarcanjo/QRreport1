export type Building = {
    id: number,
    name: string,
    floors: number,
    state: State,
    timestamp?: Date
    numberOfRooms?: number
}

export type Company = {
    id: number,
    name: string,
    state: State,
    timestamp?: Date
    numberOfBuildings?: number
}

export type Room = {
    id: number,
    name: string,
    floor: number,
    state: State,
    numberOfReports?: number
}

export type State = {
    id: number;
    name: string
}

export type Employee = {
    id: string;
    name: string;
    currentWorks: number;
    avaliation: number;
}

export type Ticket = {
    id: number;
    subject: string;
    description: string;
    category: string;
    buildingName: string;
    roomName: string;
    possibleTransitions: State[];
}

export type TicketItem = {
    id: number,
    subject: string,
    description?: string,
    employeeState: string, 
    userState: string,
}

export type Comment = {
    id: number;
    authorName: string;
    comment: string;
}

export type Person = {
    id: number,
    name: string,
    phone: string,
    email: string,
    state: string,
    roles: string[],
    skills?: string[],
    timestamp: Date,
    numberOfReports?: string,
    reportsRejected?: string
}

export type PersonItem = {
    id: number,
    name: string,
    phone: string,
    email: string,
    state: State,
    roles: Role[],
    skills?: Skill[]
}

export type Role = {
    name: string
}

export type Skill = {
    name: string
}

export type Device = {
    id: number,
    name: string,
    state: State
}

export type CategoryItem = {
    id: number,
    name: string,
    state: string,
    timestamp: Date
}