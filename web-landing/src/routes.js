import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";

import Content from "./containers/Content";
import Payment from "./containers/Payment";
import PaymentFailed from "./containers/Payment/PaymentFailed";
import PaymentSuccess from "./containers/Payment/PaymentSuccess";
import PrivacyPolicy from "./containers/PrivacyPolicy";
import UserAgreement from "./containers/UserAgreement";

const useRoutes = () => {
  return (
    <Routes>
      <Route path={"/5measurement"} element={<Content />} />
      <Route path={"/5measurement/policy"} element={<PrivacyPolicy />} />
      <Route path={"/5measurement/agreement"} element={<UserAgreement />} />
      <Route path={"/5measurement/payment"} element={<Payment />} />

      <Route path={"/5measurement/payment/success"} element={<PaymentSuccess />} />
      <Route path={"/5measurement/payment/failed"} element={<PaymentFailed />} />

      <Route
        path="/"
        element={<Navigate to={"/5measurement"} />}
      />
    </Routes>
  );
};

export default useRoutes;
