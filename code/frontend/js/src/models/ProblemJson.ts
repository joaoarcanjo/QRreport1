export type ProblemJsonMediaType = 'application/problem+json'

/**
 * Describes InvalidParameters that can be inserted in the payload of a Bad Request error.
 */
export type InvalidParameters = {
    name: string,
    local: string,
    reason: string,
}

/**
 * Describes Problem Json type.
 */
export type ProblemJson = {
    type: string,
    title: string,
    detail?: string,
    instance: string,
    invalidParameters?: InvalidParameters,
    data?: object,
}
