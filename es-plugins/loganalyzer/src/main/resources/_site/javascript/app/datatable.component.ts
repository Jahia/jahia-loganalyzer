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
import {Component, Input, Output, OnInit, ChangeDetectionStrategy, EventEmitter } from 'angular2/core';
import {Observable}     from 'rxjs/Observable';
import {Router} from 'angular2/router';
import {LogAnalyzerService} from './loganalyzer.service';
import {LogSearchResult} from './loganalyzer.app.component';
import {Calendar,DataTable,Column,LazyLoadEvent} from 'primeng/primeng';

@Component({
    selector: 'loganalyzer-datatable',
    template: `
<form class="form-inline">
  <div class="form-group">
    <label for="startDate">Start date</label>
    <p-calendar [(value)]="startDate"></p-calendar>
  </div>
  <div class="form-group">
    <label for="endDate">End date</label>
    <p-calendar [(value)]="endDate"></p-calendar>
  </div>
</form>
<p-dataTable [value]="tableData" [scrollable]="true" [lazy]="true" (onLazyLoad)="loadData($event)" [paginator]="true" [rows]="15" [totalRecords]="totalRecords">
    <p-column *ngFor="#col of cols" [sortable]="true" [filter]="true" [field]="col.field" [header]="col.header"></p-column>
</p-dataTable>
`,
    styles: [''],
    directives: [Calendar, DataTable, Column],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DataTableComponent implements OnInit {

    constructor() {
    }

    private _logSearchResult:LogSearchResult;
    @Output() updateTable:EventEmitter<any> = new EventEmitter();
    private startDate:string;
    private endDate:string;

    private tableData:Array<any> = [];
    private totalRecords = 0;
    private cols:Array<any> = [];

    ngOnInit() {
    }

    @Input()
    set logSearchResult(logSearchResult:LogSearchResult) {
        if (logSearchResult === undefined) {
            return;
        }
        this.totalRecords = logSearchResult.hits.total;
        console.log('Updating data table from back-end. totalRecords=' + this.totalRecords);
        this.tableData = [];
        for (let hit of logSearchResult.hits.hits) {
            this.tableData.push(hit._source);
        }
        let columnKeys:Array<string> = this.keys(this.tableData[0]);
        this.cols = [];
        for (let columnKey of columnKeys) {
            this.cols.push({field: columnKey, header: columnKey});
        }
    }

    keys(myObject:any):Array<string> {
        return Object.keys(myObject);
    }

    loadData(event:LazyLoadEvent) {
        console.log("Lazy data loading requested", event);
        //event.first = First row offset
        //event.rows = Number of rows per page
        //event.sortField = Field name to sort in single sort mode
        //event.sortOrder = Sort order as number, 1 for asc and -1 for dec in single sort mode
        //multiSortMeta: An array of SortMeta objects used in multiple columns sorting. Each SortMeta has field and order properties.
        //filters: Filters object having field as key and filter value, filter matchMode as value
        //do a request to a remote datasource using a service and return the cars that match the lazy load criteria
        this.updateTable.emit(event);
    }

}
