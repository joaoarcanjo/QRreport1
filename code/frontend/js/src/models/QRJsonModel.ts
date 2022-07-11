export type MediaType = 'application/vnd.daw+json'
export type HttpMethod = 'GET' | 'HEAD' | 'POST' | 'PUT' | 'PATCH' | 'DELETE' | 'OPTIONS' | 'TRACE'
type PropertyType = 'number' | 'string' | 'array'
type ActionType = 'application/json' | 'application/x-www-form-urlencoded'


/**
 * Describes links.
 */
export type Link = {
    rel: string[],
    href: string,
    templated?: string,
}

/**
 * Describes the properties of an action.
 */
 export type Property = {
    name: string,
    type: PropertyType,
    itemsType?: PropertyType,
    required?: boolean,
    possibleValues?: PropertyValue
}

type PropertyValue = {
    href: string | null,
    values: any,
}

/**
 * Describes actions.
 */
export type Action = {
    name: string,
    title: string,
    method: HttpMethod,
    href: string,
    type: ActionType,
    properties: Property[],
}

/**
 * Describes entities.
 */
 export type Entity<T> = {
    class: string[],
    rel?: string[],
    properties: T,
    entities?: Entity<any>[],
    actions?: Action[],
    links?: Link[],
  }

export function getAction<T>(entity: Entity<T>, actionName: string): Action | undefined {
    return entity?.actions?.find(action => action.name === actionName)
}

export function getLink<T>(entity: Entity<T>, rel: string): Link | undefined {
    return entity.links?.find(link => link.rel.includes(rel))
}
