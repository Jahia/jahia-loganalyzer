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
import {bootstrap}    from 'angular2/platform/browser';
import {ROUTER_PROVIDERS} from 'angular2/router';
import {LogAnalyzerAppComponent} from './loganalyzer.app.component';
import {HTTP_PROVIDERS}    from 'angular2/http';
// Add all operators to Observable
import 'rxjs/Rx';

bootstrap(LogAnalyzerAppComponent, [ROUTER_PROVIDERS, HTTP_PROVIDERS]);