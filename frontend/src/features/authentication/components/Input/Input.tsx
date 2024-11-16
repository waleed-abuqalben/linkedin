import { InputHTMLAttributes } from "react"
import classes from "./Input.module.scss"
type InputProps = InputHTMLAttributes<HTMLInputElement> & {
  label : string;
}
export default function Input({label, ...authorProps} : InputProps) {
  return (
    <div className={classes.root}>
      <label>{label}</label>
      <input {...authorProps}/>
    </div>
  )
}
