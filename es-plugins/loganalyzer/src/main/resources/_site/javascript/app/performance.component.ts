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
import {Component, OnInit} from 'angular2/core';
import {Router} from 'angular2/router';
import {LogAnalyzerService} from './loganalyzer.service';
import {LogSearchResult} from './loganalyzer.app.component';
import {DataTableComponent} from './datatable.component';
import {Observable}     from 'rxjs/Observable';

@Component({
    selector: 'loganalyzer-performance',
    template: `<loganalyzer-datatable [logSearchResult]="logSearchResult" (updateTable)="updateTable($event)">`,
    styles: [''],
    directives: [DataTableComponent]
})
export class PerformanceComponent implements OnInit {

    constructor(private _logAnalyzerService:LogAnalyzerService, private _router:Router) {
    }

    errorMessage:string;
    logSearchResult:LogSearchResult;

    ngOnInit() {
        this._logAnalyzerService.getPerformance().subscribe(result => this.logSearchResult = result);
    }

    updateTable(event:any) {
        console.log("Received lazy load event, reloading performance table...", event);
        this._logAnalyzerService.getPerformance(event.sortField, event.first, event.rows).subscribe(result => this.logSearchResult = result);
    }

}
