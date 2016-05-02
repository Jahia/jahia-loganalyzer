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