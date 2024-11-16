import { ReactNode } from "react"
import classes from "./Box.module.scss"
export default function Box({
  children  
}: {children : ReactNode}) {
  return (
    <div className={classes.root}>{children}</div>
  )
}
