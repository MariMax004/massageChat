const path = require("path");
const HTMLWebpackPlugin = require("html-webpack-plugin");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const CopyWebpackPlugin = require("copy-webpack-plugin");

module.exports = {
    mode: "production",
    entry: "./src/index.tsx",
    module: {
        rules: [
            {
                test: /\.ts|\.tsx$/,
                loader: "ts-loader",
                exclude: /node_module/,
            },
            {
                test: /\.(sa|sc|c)ss$/,
                use: ["style-loader", "css-loader", "sass-loader"],
            },
        ]
    },
    resolve: {
        extensions: [".tsx", ".ts", ".js"],
    },
    output: {
        filename: "bundle.js",
        path: path.resolve(__dirname, "dist"),
        publicPath: "/",
    },
    plugins: [
        new HTMLWebpackPlugin({
            template: "./public/index.html",
            favicon: "./public/favicon.svg",
        }),
        new CleanWebpackPlugin(),
        new CopyWebpackPlugin({
            patterns: [
                { from: "./public/favicon.svg" }
            ]
        }),
    ],
    devServer: {
        historyApiFallback: true,
    }
}