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
import {Component} from 'angular2/core';
import {RouteConfig, ROUTER_DIRECTIVES} from 'angular2/router';
import {PerformanceComponent} from './performance.component';
import {ExceptionsComponent} from './exceptions.component';
import {ThreadDumpsComponent} from './threaddumps.component';
import {LogsComponent} from './logs.component';
import {LogAnalyzerService} from './loganalyzer.service';

export class LogEntry {
    timestamp:number;
}

export class LogSearchResult {
    took:number;
    timed_out:boolean;
    hits:{
        total : number;
        hits : [ any ];
    }
}

@Component({
    selector: 'loganalyzer',
    template: `
    <h1>{{title}}</h1>
    <ul class="nav nav-tabs">
      <li role="presentation"><a [routerLink]="['Performance']">Performance</a></li>
      <li role="presentation"><a [routerLink]="['Exceptions']">Exceptions</a></li>
      <li role="presentation"><a [routerLink]="['ThreadDumps']">Thread dumps</a></li>
      <li role="presentation"><a [routerLink]="['Logs']">Logs</a></li>
    </ul>
    <router-outlet></router-outlet>
  `,
    styles: [],
    directives: [ROUTER_DIRECTIVES],
    providers: [LogAnalyzerService]
})
@RouteConfig([
    // {path: '/', redirectTo: ['Dashboard'] },
    {path: '/performance', name: 'Performance', component: PerformanceComponent, useAsDefault: true},
    {path: '/exceptions', name: 'Exceptions', component: ExceptionsComponent},
    {path: '/threaddumps', name: 'ThreadDumps', component: ThreadDumpsComponent},
    {path: '/logs', name: 'Logs', component: LogsComponent}
])
export class LogAnalyzerAppComponent {
    public title = 'Jahia Log Analyzer';
}