import React from "react";
import { BrowserRouter, Route, Switch } from "react-router-dom";

import "./style/App.scss";

const App: React.FC = () => {
    return (
        <BrowserRouter>
            <Switch>
                <Route exact path="/">
                    <h1>CROSSROADS</h1>
                </Route>
            </Switch>
        </BrowserRouter>
    )
}

export default App;
