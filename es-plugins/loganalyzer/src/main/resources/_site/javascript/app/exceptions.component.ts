import {Component, OnInit} from 'angular2/core';
import {Router} from 'angular2/router';
import {LogAnalyzerService} from './loganalyzer.service';
import {LogSearchResult} from './loganalyzer.app.component';

@Component({
    selector: 'loganalyzer-exceptions',
    template: `<table *ngIf="logSearchResult">
      <thead>
          <tr>
            <th *ngFor="#key of keys(logSearchResult.hits.hits[0]._source);">{{key}}</th>
          </tr>
      </thead>
      <tbody>
      <tr *ngFor="#hit of logSearchResult.hits.hits">
        <td *ngFor="#key of keys(hit._source);">{{hit._source[key]}}</td>
      </tr>
      </tbody>
    </table>`,
    styles: ['']
})
export class ExceptionsComponent implements OnInit {

    constructor(private _logAnalyzerService:LogAnalyzerService, private _router:Router) {
    }

    errorMessage:string;
    logSearchResult:LogSearchResult;

    ngOnInit() {
        this._logAnalyzerService.getExceptions().subscribe(
            logSearchResult => this.logSearchResult = logSearchResult,
            errorMessage => this.errorMessage = <any>errorMessage
        );
    }

    keys(myObject:any):Array<string> {
        return Object.keys(myObject);
    }

}
