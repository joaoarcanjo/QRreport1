import { InputProps } from "./FormComponents"

export const nameInputForm = (register: any, errors: any, isRequired: boolean | undefined): InputProps => {
    return {
        inputLabelName: 'Name',
        register: register("name", {required: isRequired? 'Is required' : '',  minLength: 1, maxLength: 50}),
        style: {borderColor: errors.name ? 'red': 'black'},
        name: "name",
        type: "text",
        errorMessage: errors.name && 'Invalid name'
    }
}

export const emailInputForm = (register: any, errors: any, isRequired: boolean | undefined): InputProps => {
    return {
        inputLabelName: 'Email',
        register: register("email", {required: isRequired? 'Is required' : '', pattern: /.+@.+/, minLength: 4, maxLength: 320}),
        style: {borderColor: errors.email ? 'red': 'black'},
        name: "email",
        type: "email",
        errorMessage: errors.email && 'Invalid email'
    }
}

export const phoneInputForm = (register: any, errors: any, isRequired: boolean | undefined): InputProps => {
    return {
        inputLabelName: 'Phone number',
        register: register("phone", {required: isRequired? 'Is required' : '', minLength: 1, maxLength: 50}),
        style: {borderColor: errors.phone ? 'red': 'black'},
        name: "phone",
        type: "tel",
        errorMessage: errors.phone && 'Invalid phone number'
    }
}

export const passwordInputForm = (register: any, errors: any, isRequired: boolean | undefined): InputProps => {
    return {
        inputLabelName: 'Password',
        register: register("password", {required: isRequired? 'Is required' : '', minLength: 1, maxLength: 50}),
        style: {borderColor: errors.password ? 'red': 'black'},
        name: "password",
        type: "password",
        errorMessage: errors.password && 'Invalid password'
    }
}

export const passwordVerifyInputForm = (register: any, errors: any, getValues: any, isRequired: boolean | undefined): InputProps => {
    return {
        inputLabelName: 'Repeat your password *',
        register: register("passwordVerify", {required: isRequired? 'Is required' : '', minLength: 1, maxLength: 127, validate: (value: any) => (value === getValues("password") && getValues("password") !== null)}),
        style: {borderColor: errors.passwordVerify ? 'red': 'black'},
        name: "passwordVerify",
        type: "password",
        errorMessage: errors.passwordVerify && 'Verify if both passwords are equal.'
    }
}