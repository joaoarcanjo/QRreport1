import { InputProps, TextAreaProps } from "./FormComponents"

export const simpleInputForm = (register: any, errors: any, isRequired: boolean | undefined, name: string, type: string): InputProps => {

    return {
        inputLabelName: name + (isRequired? ' *' : ''),
        register: register(name, {required: isRequired? 'Is required' : '',  minLength: 1, maxLength: 50}),
        style: {borderColor: errors.name ? 'red': 'black'},
        name: name,
        type: type === 'string'? 'text': 'number',
        errorMessage: errors.name && `Invalid ${name}`
    }
}

export const simpleTextAreaForm = (register: any, errors: any, isRequired: boolean | undefined, name: string, text: string):TextAreaProps => {
    return {
        textAreaLabelName: name + (isRequired? ' *' : ''),
        register: register(name, {required: isRequired? 'Is required' : '', maxLength: 200 }),
        style: {borderColor: errors.description ? 'red': 'black'},
        text: text,
        errorMessage: errors.description && `Invalid ${name}`
    }
}

export const emailInputForm = (register: any, errors: any, isRequired: boolean | undefined, name: string): InputProps => {
    return {
        inputLabelName: name + (isRequired? ' *' : ''),
        register: register(name, {required: isRequired? 'Is required' : '', pattern: /.+@.+/, minLength: 4, maxLength: 320}),
        style: {borderColor: errors.email ? 'red': 'black'},
        name: name,
        type: name,
        errorMessage: errors.email && `Invalid ${name}`
    }
}

export const phoneInputForm = (register: any, errors: any, isRequired: boolean | undefined, name: string): InputProps => {
    return {
        inputLabelName: name + (isRequired? ' *' : ''),
        register: register(name, {required: isRequired? 'Is required' : '', minLength: 1, maxLength: 50}),
        style: {borderColor: errors.phone ? 'red': 'black'},
        name: name,
        type: "tel",
        errorMessage: errors.phone && `Invalid ${name}`
    }
}

export const passwordInputForm = (register: any, errors: any, isRequired: boolean | undefined, name: string): InputProps => {
    return {
        inputLabelName: name + (isRequired? ' *' : ''),
        register: register(name, {required: isRequired? 'Is required' : '', minLength: 1, maxLength: 50}),
        style: {borderColor: errors.password ? 'red': 'black'},
        name: name,
        type: "password",
        errorMessage: errors.password && 'Invalid password'
    }
}

export const passwordVerifyInputForm = (register: any, errors: any, getValues: any, isRequired: boolean | undefined): InputProps => {
    return {
        inputLabelName: 'Repeat your password' + (isRequired? ' *' : ''),
        register: register("passwordVerify", {required: isRequired? 'Is required' : '', minLength: 1, maxLength: 127, validate: (value: any) => (value === getValues("password") && getValues("password") !== null)}),
        style: {borderColor: errors.passwordVerify ? 'red': 'black'},
        name: "passwordVerify",
        type: "password",
        errorMessage: errors.passwordVerify && 'Verify if both passwords are equal.'
    }
}