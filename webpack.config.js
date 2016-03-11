module.exports = {
    context: __dirname + "/src/main/resources/html/javascript/app",
    entry: "./main.js",
    output: {
        path: __dirname + "/target/classes/html/javascript/app",
        filename: "bundle.js"
    }
};