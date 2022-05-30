import React from "react";
import { Routes, Route, Navigate } from "react-router-dom";

import AuthPage from "../containers/AuthPage";
import ArchiveContentPage from "../containers/ArchiveContentPage";
import ViewContentPage from "../containers/ViewContentPage";
import CreatorPage from "../containers/CreatorPage";
import CreatorRoutesConstants from "../constants/addresses/routes/creator.routes.constants";
import MainRoutesConstants from "../constants/addresses/routes/main.routes.constants";
import CreatorEditPage from "../containers/CreatorPage/CreatorEditPage";
import CategoryPage from "../containers/CategoryPage";

const useRoutes = (isAuthenticated, modules) => {
  if (isAuthenticated && modules != null && modules != undefined) {
    return (
      <Routes>
        {/* Маршрутизация для создателя */}

        {modules.admin === true && (
          <Route
            path={CreatorRoutesConstants.content_view}
            exact
            element={<ViewContentPage />}
          />
        )}

        {modules.admin === true && (
          <Route
            path={CreatorRoutesConstants.content_add}
            exact
            element={<CreatorPage />}
          />
        )}

        {modules.admin === true && (
          <Route
            path={CreatorRoutesConstants.content_edit}
            exact
            element={<CreatorEditPage />}
          />
        )}

        {modules.admin === true && (
          <Route
            path={CreatorRoutesConstants.content_category}
            exact
            element={<CategoryPage />}
          />
        )}

        {modules.admin === true && (
          <Route
            path={CreatorRoutesConstants.content_archive}
            exact
            element={<ArchiveContentPage />}
          />
        )}

        <Route
          path={MainRoutesConstants.auth}
          exact
          element={<Navigate to={CreatorRoutesConstants.content_add} />}
        />
      </Routes>
    );
  }

  return (
    <Routes>
      <Route path={MainRoutesConstants.auth} exact element={<AuthPage />} />
      <Route
        path="*"
        exact
        element={<Navigate to={MainRoutesConstants.auth} />}
      />
    </Routes>
  );
};

export default useRoutes;
