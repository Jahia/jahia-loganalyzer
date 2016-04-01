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
