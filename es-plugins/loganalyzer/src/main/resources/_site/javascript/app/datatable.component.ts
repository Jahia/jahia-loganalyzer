import {Component, Input, OnInit} from 'angular2/core';
import {Router} from 'angular2/router';
import {LogAnalyzerService} from './loganalyzer.service';
import {LogSearchResult} from './loganalyzer.app.component';
import {Calendar} from 'primeng/primeng';

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
<nav>
  <ul class="pagination">
    <li>
      <a href="#" aria-label="Previous">
        <span aria-hidden="true">&laquo;</span>
      </a>
    </li>
    <li><a href="#">1</a></li>
    <li><a href="#">2</a></li>
    <li><a href="#">4</a></li>
    <li><a href="#">5</a></li>
    <li>
      <a href="#" aria-label="Next">
        <span aria-hidden="true">&raquo;</span>
      </a>
    </li>
  </ul>
</nav>
    <table *ngIf="logSearchResult" class="table">
      <thead>
          <tr>
            <th *ngFor="#key of keys(logSearchResult.hits.hits[0]._source);" (click)="sortBy(key)" >{{key}}</th>
          </tr>
      </thead>
      <tbody>
      <tr *ngFor="#hit of logSearchResult.hits.hits">
        <td *ngFor="#key of keys(hit._source);">{{hit._source[key]}}</td>
      </tr>
      </tbody>
    </table>`,
    styles: [''],
    directives: [Calendar]
})
export class DataTableComponent implements OnInit {

    constructor() {
    }

    @Input() logSearchResult:LogSearchResult;
    private sortKeys:{ [key:string] : string } = {};
    private currentPage:number = 0;
    private totalPages:number = 0;
    private startDate:string;
    private endDate:string;

    ngOnInit() {
    }

    keys(myObject:any):Array<string> {
        return Object.keys(myObject);
    }

    sortBy(sortKey:string) {
        var sortOrder = this.sortKeys[sortKey];
        if (sortOrder === undefined) {
            sortOrder = 'asc';
        } else {
            if (sortOrder == 'desc') {
                sortOrder = 'asc';
            } else {
                sortOrder = 'desc';
            }
        }
        this.sortKeys[sortKey] = sortOrder;
        console.log("sortKeys", this.sortKeys);
    }

}
