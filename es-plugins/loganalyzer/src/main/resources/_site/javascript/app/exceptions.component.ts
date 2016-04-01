import {Component, OnInit} from 'angular2/core';
import {Router} from 'angular2/router';
import {LogAnalyzerService} from './loganalyzer.service';
import {LogSearchResult} from './loganalyzer.app.component';
import {DataTableComponent} from './datatable.component';
import {Observable}     from 'rxjs/Observable';

@Component({
    selector: 'loganalyzer-exceptions',
    template: `<loganalyzer-datatable [logSearchResult]="logSearchResult">`,
    styles: [''],
    directives: [DataTableComponent]
})
export class ExceptionsComponent implements OnInit {

    constructor(private _logAnalyzerService:LogAnalyzerService, private _router:Router) {
    }

    errorMessage:string;
    logSearchResult:Observable<LogSearchResult>;

    ngOnInit() {
        this.logSearchResult = this._logAnalyzerService.getExceptions();
    }

}
