import { createContext, useContext } from 'react'
import { ProblemJson } from '../models/ProblemJson'
import { SIGNUP_URL_API } from '../Urls'

export const SESSION_KEY = 'IsLogin'
export const NAME_KEY = 'UserName'
export const EMAIL_KEY = 'UserEmail'
export const ACTIVE_ROLE = 'UserRole'
export const USER_ROLE = "user"
export const EMPLOYEE_ROLE = "employee"
export const MANAGER_ROLE = "manager"
export const ADMIN_ROLE = "admin"

export async function signupUser(name: string, phone: string, email: string, password: string, passwordVerify: string): Promise<Response> {
  const payload: any = {}

  payload['name'] = name
  payload['phone'] = phone
  payload['email'] = email
  payload['password'] = password
  payload['confirmPassword'] = passwordVerify

  const response = await fetch(SIGNUP_URL_API,
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Request-Origin': 'WebApp'
      },
      body: JSON.stringify(payload),
      credentials: "include"
    })
  return response
}

export async function loginUser(email: string, password: string): Promise<Response> {
  console.log("login")
  const response = await fetch('http://localhost:8080/v1/login',
    {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json', 
        'Request-Origin': 'WebApp'
      },
      body: JSON.stringify({ email: email, password: password }),
      credentials: "include"
    })
  return response
}

export async function logoutUser(): Promise<number> {
  const response = await fetch("http://localhost:8080/v1/logout",  
  { 
     method: "POST",
     headers: {'Request-Origin': 'WebApp'},
     credentials: "include"
  })
  console.log(response.status)
  return response.status
}

export function createRepository() {
  return {
    login: loginUser,
    logout: logoutUser,
    signup: signupUser
  }
}

export type ContextType = {
  problem: ProblemJson | undefined,
  isLoggedIn: boolean,
  userName: string | null,
  userRole: string | null,
  login: (email: string, password: string) => void,
  logout: () => void,
  signup: (name: string, phone: string, email: string, password: string, passwordVerify: string) => void,
  changeRole: (role: string) => void
}

export const LoggedInContext = createContext<ContextType | undefined>(undefined)

export const useLoggedInState = () => useContext(LoggedInContext)
