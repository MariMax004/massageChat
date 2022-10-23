declare module "@mui/material/styles";

import { createTheme } from "@mui/material";

const theme = createTheme({
    palette: {
        primary: {
            main: "#444444",
            dark: "#000000",
            contrastText: "#FFFFFF"
        },
        secondary: {
            main: "#A63AE0",
            dark: "#4B00A1",
            contrastText: "#FFFFFF"
        }
    },
    breakpoints: {
        values: {
            xs: 375,
            sm: 540,
            md: 760,
            lg: 1000,
            xl: 1200,
        }
    }
});

export default theme;
