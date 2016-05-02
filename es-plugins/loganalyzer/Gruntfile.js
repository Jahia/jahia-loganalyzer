/*
 * #%L
 * Jahia Log Analyzer ElasticSearch Site Plugin
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2007 - 2016 Jahia
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
module.exports = function (grunt) {
    grunt.initConfig({
        // ----- Environment
        // read in some metadata from project descriptor
        project: grunt.file.readJSON('package.json'),
        // define some directories to be used during build
        dir: {
            // location where TypeScript source files are located
            "source_ts": "src/main/ts",
            // location where TypeScript/Jasmine test files are located
            "source_test_ts": "src/test/ts",
            // location where all build files shall be placed
            "target": "target",
            // location to place (compiled) javascript files
            "target_js": "target/js",
            // location to place (compiles) javascript test files
            "target_test_js": "target/js-test",
            // location to place documentation, etc.
            "target_report": "target/report"
        },
        // ----- TypeScript compilation
        //  See https://npmjs.org/package/grunt-typescript
        typescript: {
            // Compiles the code into a single file. Also generates a typescript declaration file
            compile: {
                src: ['<%= dir.source_ts %>/**/*.ts'],
                dest: '<%= dir.target_js %>/<%= project.name %>.js',
                options: {
                    basePath: '<%= dir.source_ts %>',
                    target: 'es5',
                    declaration: true,
                    comments: true
                }
            },
            // Compiles the tests.
            compile_test: {
                src: ['<%= dir.source_test_ts %>/**/*.ts'],
                dest: '<%= dir.target_test_js %>',
                options: {
                    basePath: '<%= dir.source_test_ts %>',
                    target: 'es5'
                }
            }
        },
        // ------- Unit tests with code coverage
        //  See https://github.com/gruntjs/grunt-contrib-jasmine
        jasmine: {
            run: {
                // the code to be tested
                src: ['<%= dir.target_js %>/**/*.js'],
                options: {
                    // the tests
                    specs: '<%= dir.target_test_js %>/**/*Spec.js'
                }
            }
        }
    });
    // ----- Setup tasks
    grunt.loadNpmTasks('grunt-typescript');
    grunt.loadNpmTasks('grunt-contrib-jasmine');
    grunt.registerTask('default', ['typescript:compile', 'typescript:compile_test'/*,'jasmine'*/]);
};