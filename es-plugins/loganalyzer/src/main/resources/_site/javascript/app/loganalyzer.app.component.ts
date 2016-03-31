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