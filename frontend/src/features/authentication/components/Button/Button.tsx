import { ButtonHTMLAttributes } from "react";
import classes from "./Button.module.scss"
type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
    outline? : boolean;
  }
export default function Button({outline, children, ...others}:ButtonProps) {
  return (
    <button {...others} className={`${classes.root} 
            ${outline ? classes.outline : ""}`}>
            {children}    
    </button>
  )
}
