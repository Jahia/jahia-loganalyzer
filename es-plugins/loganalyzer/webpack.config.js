module.exports = {
    context: __dirname + "/src/main/resources/_site/javascript/app",
    entry: "./main.js",
    output: {
        path: __dirname + "/target/classes/_site/javascript/app",
        filename: "bundle.js"
    }
};