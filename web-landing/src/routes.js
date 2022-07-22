import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";

import Content from "./containers/Content";
import PrivacyPolicy from "./containers/PrivacyPolicy";
import UserAgreement from "./containers/UserAgreement";

const useRoutes = () => {
  return (
    <Routes>
      <Route path={"/5measurement"} exact element={<Content />} />
      <Route path={"/5measurement/policy"} exact element={<PrivacyPolicy />} />
      <Route path={"/5measurement/agreement"} exact element={<UserAgreement/>} />

      {
        /*<Route
        path="/"
        exact
        element={<Navigate to={"/5measurement"} />}
      />*/}
    </Routes>
  );
};

export default useRoutes;
