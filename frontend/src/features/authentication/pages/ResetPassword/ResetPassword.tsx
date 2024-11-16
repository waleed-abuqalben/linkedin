import Box from "../../components/Box/Box";
import Layout from "../../components/Layout/Layout";
import classes from "./ResetPassword.module.scss"
export default function ResetPassword() {
  return (
    <Layout className={classes.root}>
      <Box>
        <h1>Reset Password</h1>
        <form>
        <p>
              Enter your email and we’ll send a verification code if it matches an existing LinkedIn
              account.
            </p>
        </form>
      </Box>
    </Layout>
  )
}
