import React from "react";
import { createRoot } from "react-dom/client";
import { ThemeProvider } from "@mui/material";

import App from "./App";
import theme from "./style/theme";

const container = document.getElementById("root");
const root = createRoot(container!);
root.render(<ThemeProvider theme={theme}><App/></ThemeProvider>);
