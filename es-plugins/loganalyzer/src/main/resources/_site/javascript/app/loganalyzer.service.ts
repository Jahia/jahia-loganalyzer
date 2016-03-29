import {Injectable} from 'angular2/core';
import {Http, Response} from 'angular2/http';
import {Observable}     from 'rxjs/Observable';
import {LogSearchResult} from './loganalyzer.app.component';

@Injectable()
export class LogAnalyzerService {

    constructor(private http:Http) {
    }

    private _logAnalyzerUrl = 'http://localhost:9200/loganalyzer-*';  // URL to web api

    getPerformance() {

        return this.http.get(this._logAnalyzerUrl + '/performance-details/_search?q=*&sort=timestamp:desc&from=0&size=10')
            .map(res => <LogSearchResult> res.json())
            .catch(this.handleError);

    }

    getExceptions() {

        return this.http.get(this._logAnalyzerUrl + '/exceptions-details/_search?q=*&sort=timestamp:desc&from=0&size=10')
            .map(res => <LogSearchResult> res.json())
            .catch(this.handleError);
    }

    getThreadDumps() {

        return this.http.get(this._logAnalyzerUrl + '/threaddumps-details/_search?q=*&sort=timestamp:desc&from=0&size=10')
            .map(res => <LogSearchResult> res.json())
            .catch(this.handleError);
    }

    getLogs() {
        return this.http.get(this._logAnalyzerUrl + '/log-details/_search?q=*&sort=timestamp:desc&from=0&size=10')
            .map(res => <LogSearchResult> res.json())
            .catch(this.handleError);
    }

    private handleError(error:Response) {
        // in a real world app, we may send the error to some remote logging infrastructure
        // instead of just logging it to the console
        console.error(error);
        return Observable.throw(error.json().error || 'Server error');
    }

}