import {Injectable} from 'angular2/core';
import {Http, Response} from 'angular2/http';
import {Observable}     from 'rxjs/Observable';
import {LogSearchResult} from './loganalyzer.app.component';

@Injectable()
export class LogAnalyzerService {

    constructor(private http:Http) {
    }

    private _logAnalyzerUrl = 'http://localhost:9200/loganalyzer-*';  // URL to web api

    getPerformance(sort?:string, offset?:number, size?:number) {
        return this.getMetrics('performance', '*', sort, offset, size);
    }

    getExceptions(sort?:string, offset?:number, size?:number) {
        return this.getMetrics('exceptions', '*', sort, offset, size);
    }

    getThreadDumps(sort?:string, offset?:number, size?:number) {
        return this.getMetrics('threaddumps', '*', sort, offset, size);
    }

    getLogs(sort?:string, offset?:number, size?:number) {
        return this.getMetrics('log', '*', sort, offset, size);
    }

    getMetrics(metric:string, query:string = '*', sort:string = 'timestamp:desc', offset:number = 0, size:number = 10) {
        console.log("Requesting " + metric + " with query=" + query + " and sort=" + sort + " offset=" + offset + " size=" + size + "...");
        return this.http.get(this._logAnalyzerUrl + '/' + metric + '-details/_search?q=' + query
                + '&sort=' + sort + '&from=' + offset + '&size=' + size)
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