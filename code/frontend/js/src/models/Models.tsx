export type Building = {
    id: number,
    name: string,
    floors: number,
    state: string,
    timestamp: Date
    numberOfRooms?: number //TODO
}

export type Company = {
    id: number,
    name: string,
    state: string,
    timestamp: Date
    numberOfBuildings?: number //TODO
}

export type Room = {
    id: number,
    name: string,
    floor: number,
    state: string,
    timestamp: Date,
    numberOfReports?: number //TODO
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
    creationTimestamp: Date;
    employeeState: string;
    userState: string;
    possibleTransitions: State[];
}

export type TicketItem = {
    id: number,
    subject: string,
    description?: string,
    company: string,
    building: string,
    room: string,
    employeeState: string, 
    userState: string,
}

export type CommentInfo = {
    id: number,
    comment: string,
    timestamp: Date
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
    numberOfReports?: string, //TODO
    reportsRejected?: string //TODO
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
    state: string,
    category: string,
    timestamp: Date
}

export type Anomaly = {
    id: number,
    anomaly: string
}

export type DeviceQrCode = {
    device: Device,
    hash?: String,
}

export type CategoryItem = {
    id: number,
    name: string,
    state: string,
    timestamp: Date
}

export type QrCode = {
    qrcode: string
}

export type FormInfo = {
    company: string,
    building: string,
    room: string,
    device: string
}

export type Category = {
    id: number,
    name: string,
    state: string,
    timestamp: Date
}

export type LoginUser = {
    name: string,
    email: string,
    phone?: string,
    activeRole: string,
}

export type EmployeeState = {
    name: string
}