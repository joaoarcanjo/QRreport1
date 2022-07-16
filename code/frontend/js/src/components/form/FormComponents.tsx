import { CSSProperties, FormEventHandler } from "react"
import { UseFormRegisterReturn } from "react-hook-form"
import { Link } from "react-router-dom"

type HeaderProps = {
    heading: string,
    children?: React.ReactNode
}

export type OptionsType = { 
    value: any,
    label: string
}

export type OptionsProps = {
    optionsText: string,
    otherValueText?: string,
    defaultOtherValue?: any,
    register: UseFormRegisterReturn,
    options?: OptionsType[]
}

export type InputProps = {
    inputLabelName: string,
    register: UseFormRegisterReturn,
    style?: CSSProperties,
    name: string,
    type: string,
    errorMessage?: string | undefined 
}

export type TextAreaProps = {
    textAreaLabelName: string,
    register: UseFormRegisterReturn,
    style: CSSProperties,
    text: string,
    errorMessage?: string | undefined 
}

type CreateButtonProps = {
    text: string,
    redirectUrl: string
}

type FormProps = {
    onSubmitHandler: FormEventHandler<HTMLFormElement>,
    children?: React.ReactNode
}

export function HeaderParagraph({paragraph}: {paragraph: string}) {
    return (
        <div className="mt-6 flex justify-center">
            {paragraph}
        </div>
    )
}

export function Header({heading, children}: HeaderProps){
    return(
        <div className="mb-10">
            <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
                {heading}
            </h2>
            <div className="mt-6">
                {children}
            </div>
        </div>
    )
}

export function SubmitButton({text}: {text:string}) {
    return (
        <div>
            <button className="w-full py-2 px-4 bg-green-600 hover:bg-green-700 rounded-md text-white text-sm">
                {text}
            </button>
        </div>
    )
}

export function CreateButton({text, redirectUrl}: CreateButtonProps) {
    return (
        <div>
            <Link to ={redirectUrl}>
                <button className="w-full py-2 px-4 bg-blue-600 hover:bg-blue-700 rounded-md text-white text-sm">
                    {text}
                </button>
            </Link>
        </div>
    )
}

export function InputLabel({text}: {text:string}) {
    return (
        <label className="text-sm font-bold text-gray-600 block">
            {text}
        </label>
    )
}

export function Options({value, setValue}: {value: OptionsProps, setValue?: React.Dispatch<React.SetStateAction<string>>}) {
    const options = value.options?.map((option, idx) => {
        return <option key={idx} value={option.label}>{option.value}</option>
    })
    return (
        <>
            <InputLabel text={value.optionsText}/>
            <select {...value.register}  
                className= "w-full p-2 border rounded-lg" onChange={e => {if(setValue) {setValue(e.target.value)}}}>
                {options}
                {value.otherValueText !== undefined && 
                <option value={value.defaultOtherValue}>{value.otherValueText}</option>}
            </select>
        </>
    )
}

export function Input({value}: {value: InputProps}) {
    return (
        <div>
            <InputLabel text= {value.inputLabelName}/>
            <input
                {...value.register}
                className= "w-full p-2 border rounded-lg"
                style= {value.style}
                name= {value.name}
                type={value.type}
                autoComplete="on"/>
            <InputError error= {value.errorMessage}/>
        </div>
    )
}

export function Paragraph({value}: {value: string}) {
    return <p>{value}</p>
}

export function InputError({error} : {error : string | undefined | boolean}) {
    return <p className="text-sm text-red-500"> {error} </p>
}

export function TextArea({value}: {value: TextAreaProps}) { //está a dar um warning
    return (
        <div>
            <InputLabel text= {value.textAreaLabelName}/>
            <textarea 
                {...value.register} 
                className="w-full p-2 border rounded-lg" 
                style= {value.style}>
                {value.text}
            </textarea>
            <InputError error= {value.errorMessage}/>
        </div>
    )
}

export function Form ({onSubmitHandler, children} : FormProps) {
    return (
        <form className="space-y-3" onSubmit= { onSubmitHandler }>
            {children}
        </form>
    )
}