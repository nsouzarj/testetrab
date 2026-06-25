import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CandidatoService } from './services/candidato.service';
import { forkJoin } from 'rxjs';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  candidatosPorEstado: any[] = [];
  imcMedioPorFaixaEtaria: any[] = [];
  percentualObesos: any = null;
  idadeMediaPorTipo: any[] = [];
  doadoresPorReceptor: any[] = [];

  carregandoInicial = true;
  carregando = false;
  mensagemErro = '';
  mensagemSucesso = '';
  dadosCarregados = false;

  constructor(private candidatoService: CandidatoService) {}

  ngOnInit(): void {
    this.carregarRelatorios();
  }

  /**
   * Busca todos os relatórios do backend de forma paralela.
   * Desativa o indicador de carregamento inicial apenas quando todas terminam.
   */
  carregarRelatorios(): void {
    this.carregandoInicial = true;

    forkJoin({
      estado: this.candidatoService.getPorEstado(),
      imc: this.candidatoService.getImcMedio(),
      obesidade: this.candidatoService.getPercentualObesos(),
      idade: this.candidatoService.getIdadeMedia(),
      doadores: this.candidatoService.getDoadoresPorReceptor()
    })
    .pipe(
      finalize(() => {
        // Pequena pausa (600ms) para dar suavidade à transição visual da animação
        setTimeout(() => {
          this.carregandoInicial = false;
        }, 600);
      })
    )
    .subscribe({
      next: (res) => {
        this.candidatosPorEstado = res.estado;
        this.imcMedioPorFaixaEtaria = res.imc;
        this.percentualObesos = res.obesidade;
        this.idadeMediaPorTipo = res.idade;
        this.doadoresPorReceptor = res.doadores;
        this.dadosCarregados = res.estado.length > 0;
        this.mensagemErro = '';
      },
      error: (err) => {
        console.error('Erro ao carregar relatórios:', err);
        this.mensagemErro = 'Não foi possível carregar os relatórios. Verifique se o servidor Spring Boot (Porta 8080) e o MySQL estão rodando.';
        this.dadosCarregados = false;
      }
    });
  }

  /**
   * Manipulador para quando o usuário seleciona um arquivo JSON para upload.
   */
  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (!file) return;

    if (file.type !== 'application/json' && !file.name.endsWith('.json')) {
      this.mensagemErro = 'Por favor, selecione um arquivo JSON válido.';
      this.mensagemSucesso = '';
      return;
    }

    this.carregando = true;
    this.mensagemErro = '';
    this.mensagemSucesso = '';

    const reader = new FileReader();
    reader.onload = (e: any) => {
      try {
        const candidatos = JSON.parse(e.target.result);
        if (!Array.isArray(candidatos)) {
          throw new Error('O arquivo JSON deve conter um array de candidatos.');
        }

        this.candidatoService.importar(candidatos).subscribe({
          next: (importados) => {
            this.carregando = false;
            this.mensagemSucesso = `Importação concluída com sucesso! ${importados} novos candidatos foram cadastrados no MySQL.`;
            this.carregarRelatorios();
          },
          error: (err) => {
            this.carregando = false;
            this.mensagemErro = 'Erro ao enviar dados para o servidor. Tente novamente.';
            console.error(err);
          }
        });
      } catch (err: any) {
        this.carregando = false;
        this.mensagemErro = `Erro ao ler o arquivo JSON: ${err.message}`;
      }
    };
    reader.readAsText(file);
  }
}
