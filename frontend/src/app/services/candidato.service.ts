import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CandidatoService {
  private apiUrl = `${window.location.protocol}//${window.location.hostname}:8080/api/candidatos`;

  constructor(private http: HttpClient) {}

  importar(candidatos: any[]): Observable<number> {
    return this.http.post<number>(`${this.apiUrl}/importar`, candidatos);
  }

  getPorEstado(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/relatorios/estado`);
  }

  getImcMedio(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/relatorios/imc-faixa-etaria`);
  }

  getPercentualObesos(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/relatorios/percentual-obesos`);
  }

  getIdadeMedia(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/relatorios/idade-tipo-sanguineo`);
  }

  getDoadoresPorReceptor(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/relatorios/doadores-por-receptor`);
  }
}
