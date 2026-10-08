import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { CareRecord, CreateCareRecordRequest } from '../../models/care-record.model';

@Injectable({
  providedIn: 'root',
})
export class CareRecordService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/care-records`

  create(careRecord: CreateCareRecordRequest): Observable<CareRecord> {
    return this.http.post<CareRecord>(this.url, careRecord);
  }

  findAll(): Observable<CareRecord[]> {
    return this.http.get<CareRecord[]>(this.url);
  }

  complete(id: string): Observable<CareRecord> {
    return this.http.patch<CareRecord>(`${this.url}/${id}/complete`, {});
  }

  update(id: string, careRecord: CreateCareRecordRequest): Observable<CareRecord> {
    return this.http.put<CareRecord>(`${this.url}/${id}`, careRecord);
  }

  remove(id: string): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
