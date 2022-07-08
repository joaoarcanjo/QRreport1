export const PERSONS_URL = "http://localhost:8080/v1/persons"

export const PERSON_URL = (personId: string | undefined): string => {
    return `http://localhost:8080/v1/persons/${personId}`
}