import React from "react";
import styles from "./App.module.css";
import {BrowserRouter, Routes, Route } from "react-router-dom";
import PrivacyPolicy from "../PrivacyPolicy";
import Content from "../Content";

const App = () => {
  
  return (
    <BrowserRouter>   
    <Content/>
  </BrowserRouter>

  );
};

export default App;
