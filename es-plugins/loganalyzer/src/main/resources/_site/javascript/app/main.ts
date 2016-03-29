import {bootstrap}    from 'angular2/platform/browser';
import {ROUTER_PROVIDERS} from 'angular2/router';
import {LogAnalyzerAppComponent} from './loganalyzer.app.component';
import {HTTP_PROVIDERS}    from 'angular2/http';
// Add all operators to Observable
import 'rxjs/Rx';

bootstrap(LogAnalyzerAppComponent, [ROUTER_PROVIDERS, HTTP_PROVIDERS]);