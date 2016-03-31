import {Component, OnInit} from 'angular2/core';
import {Router} from 'angular2/router';
import {LogAnalyzerService} from './loganalyzer.service';
import {LogSearchResult} from './loganalyzer.app.component';
import {DataTableComponent} from './datatable.component';

@Component({
    selector: 'loganalyzer-logs',
    template: `<loganalyzer-datatable [logSearchResult]="logSearchResult">`,
    styles: [''],
    directives: [DataTableComponent]
})
export class LogsComponent implements OnInit {

    constructor(private _logAnalyzerService:LogAnalyzerService, private _router:Router) {
    }

    errorMessage:string;
    logSearchResult:LogSearchResult;

    ngOnInit() {
        this._logAnalyzerService.getLogs().subscribe(
            logSearchResult => this.logSearchResult = logSearchResult,
            errorMessage => this.errorMessage = <any>errorMessage
        );
    }

}
