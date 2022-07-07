import { createContext, useContext } from 'react'

export const SESSION_KEY = 'IsLogin'
export const NAME_KEY = 'UserName'
export const ROLE_KEY = 'UserRole'

export async function loginUser(email: string, password: string): Promise<boolean> /*Promise<Response>*/ {
  console.log('Login User function called')
  console.log(`${email} + ${password}`)

  const response = await fetch("http://localhost:8080/v1/login",
    {
      method: "POST",
      headers: {"content-type": "application/json"},
      body: JSON.stringify({ email: email, password: password }),
      credentials: "include"
    })
  console.log(response.status)
  //return response
  return Promise.resolve(true)
}

export async function logoutUser(): Promise<boolean> /*Promise<number>*/ {
  /*const response = await fetch("http://localhost:8080/v1/logout",  
  { 
     method: "POST",
     credentials: "include"
  })
  console.log(response.status)
  return response.status*/
  return Promise.resolve(true)
}

export function createRepository() {
  return {
    login: loginUser,
    logout: logoutUser
  }
}

export type ContextType = {
  isLoggedIn: boolean,
  userName: string | null,
  userRole: string | null,
  login: (email: string, password: string) => void,
  logout: () => void
}

export const LoggedInContext = createContext<ContextType | undefined>(undefined)

export function useLoggedInState() {
  return useContext(LoggedInContext)
}