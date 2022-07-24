import { InputProps, TextAreaProps } from "./FormComponents"

export const inputLabelName = (name: string, isRequired: boolean | undefined) => {
    const required = isRequired === undefined ? ' *' : ''
    return name + required
}

export const requiredRegister = (isRequired: boolean | undefined) => {
    return isRequired === undefined ? 'Is required' : ''
}

export const simpleInputForm = (register: any, labelName: string, errors: any, isRequired: boolean | undefined, name: string, type: string): InputProps => {
    return {
        inputLabelName: inputLabelName(labelName, isRequired),
        register: register(name, {required: requiredRegister(isRequired), minLength: 1, maxLength: 50}),
        style: {borderColor: errors.name ? 'red': 'black'},
        name: name,
        type: type === 'string'? 'text': 'number',
        errorMessage: errors.name && `Invalid ${name}`
    }
}

export const simpleTextAreaForm = (labelName: string, register: any, errors: any, isRequired: boolean | undefined, name: string, text: string):TextAreaProps => {
    return {
        textAreaLabelName: inputLabelName(labelName, isRequired),
        register: register(name, {required: requiredRegister(isRequired), minLength: 1, maxLength: 200 }),
        style: {borderColor: errors.description ? 'red': 'black'},
        errorMessage: errors.description && `Invalid ${name}`
    }
}

export const emailInputForm = (register: any, errors: any, isRequired: boolean | undefined, name: string): InputProps => {
    return {
        inputLabelName: inputLabelName('Email', isRequired),
        register: register(name, {required: requiredRegister(isRequired), pattern: /.+@.+/, minLength: 4, maxLength: 320}),
        style: {borderColor: errors.email ? 'red': 'black'},
        name: name,
        type: name,
        errorMessage: errors.email && `Invalid ${name}`
    }
}

export const phoneInputForm = (register: any, errors: any, isRequired: boolean | undefined, name: string): InputProps => {
    return {
        inputLabelName: inputLabelName('Phone number', isRequired),
        register: register(name, {required: requiredRegister(isRequired), minLength: 1, maxLength: 50}),
        style: {borderColor: errors.phone ? 'red': 'black'},
        name: name,
        type: "tel",
        errorMessage: errors.phone && `Invalid ${name}`
    }
}

export const passwordInputForm = (register: any, errors: any, isRequired: boolean | undefined, name: string): InputProps => {
    return {
        inputLabelName: inputLabelName('Password', isRequired),
        register: register(name, {required: requiredRegister(isRequired), minLength: 1, maxLength: 50}),
        style: {borderColor: errors.password ? 'red': 'black'},
        name: name,
        type: "password",
        errorMessage: errors.password && 'Invalid password'
    }
}

export const passwordVerifyInputForm = (register: any, errors: any, getValues: any, isRequired: boolean | undefined): InputProps => {
    return {
        inputLabelName: inputLabelName('Repeat your password', isRequired),
        register: register("passwordVerify", {required: requiredRegister(isRequired), minLength: 1, maxLength: 127, validate: (value: any) => (value === getValues("password") && getValues("password") !== null)}),
        style: {borderColor: errors.passwordVerify ? 'red': 'black'},
        name: "passwordVerify",
        type: "password",
        errorMessage: errors.passwordVerify && 'Verify if both passwords are equal.'
    }
}