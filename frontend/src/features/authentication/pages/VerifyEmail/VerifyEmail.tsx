import { useState } from "react";
import Box from "../../components/Box/Box";
import Input from "../../components/Input/Input";
import Layout from "../../components/Layout/Layout";
import classes from "./VerifyEmail.module.scss"
import Button from "../../components/Button/Button";
import { useNavigate } from "react-router-dom";
export default function VerifyEmail() {
  const [errorMessage, setErrorMessage] = useState("");
  const [message, setMessage] = useState("");
  const navigate = useNavigate();
  const [isloading, setIsLoading] = useState(false);
  
  const validateEmail = async (code: string) => {
    setMessage("");
    try {
      const response = await fetch(
        `${
          import.meta.env.VITE_API_URL
        }/api/v1/authentication/validate-email-verification-token?token=${code}`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      if (response.ok) {
        setErrorMessage("");
        navigate("/");
      }
      const { message } = await response.json();
      setErrorMessage(message);
    } catch (e) {
      console.log(e);
      setErrorMessage("Something went wrong, please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  const sendEmailVerificationToken = async () => {
    setErrorMessage("");
    try {
      const response = await fetch(
        `${import.meta.env.VITE_API_URL}/api/v1/authentication/send-email-verification-token`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );
      if (response.ok) {
        setErrorMessage("");
        setMessage("Code sent successfully. Please check your email.");
        return;
      }
      const { message } = await response.json();
      setErrorMessage(message);
    } catch (e) {
      console.log(e);
      setErrorMessage("Something went wrong, please try again.");
    } finally {
      setIsLoading(false);
    }
  };
  
  return (
    <Layout className={classes.root}>
      <Box>
        <h1>Verify you Email</h1>
        <form
        onSubmit={async (e) => {
          e.preventDefault();
          setIsLoading(true);
          const code = e.currentTarget.code.value;
          await validateEmail(code);
          setIsLoading(false);
        }}
        >
        <p>Only one step left to complete your registration. Verify your email address.</p>
        <Input type="text" label="Verification code" key="code" name="code" />
        {message ? <p style={{ color: "green" }}>{message}</p> : null}
        {
          errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>
        }
        <Button 
         type="submit"  disabled={isloading} >
          Validate email
        </Button>
        <Button 
        outline type="button" disabled={isloading}
        onClick={() => {
          sendEmailVerificationToken();
        }}
        >Send again</Button>
        </form>
      </Box>
    </Layout>
  )
}
