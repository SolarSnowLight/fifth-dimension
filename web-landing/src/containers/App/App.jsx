import React from "react";
import { BrowserRouter } from "react-router-dom";

import useRoutes from "../../routes";
import PrivacyPolicy from "../PrivacyPolicy";
import Content from "../Content";

import styles from "./App.module.css";

const App = () => {
  const routes = useRoutes();

  return(
    <BrowserRouter>
    {routes}
    </BrowserRouter>
  )
};

export default App;
