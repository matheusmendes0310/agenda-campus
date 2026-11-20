import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { beforeEach, describe, expect, it } from 'vitest';
import { EventoService } from '../../../core/services/evento.service';
import { InscricaoService } from '../../../core/services/inscricao.service';
import { ParticipanteService } from '../../../core/services/participante.service';
import { InscricaoForm } from './inscricao-form';

describe('Formulário de inscrição', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [InscricaoForm],
      providers: [
        provideRouter([]),
        { provide: EventoService, useValue: { listar: () => of([
          { id: 1, nome: 'Evento ocupado', data: '2026-12-05', local: 'Lab', inscritos: 1, vagas: 1 },
          { id: 2, nome: 'Evento disponível', data: '2026-12-06', local: 'Lab', inscritos: 0, vagas: 1 },
        ]) } },
        { provide: ParticipanteService, useValue: { listar: () => of([
          { id: 3, nome: 'Ana', email: 'ana@example.org' },
        ]) } },
        { provide: InscricaoService, useValue: { buscarPorId: () => of({ eventoId: 1, participanteId: 3 }) } },
      ],
    });
  });

  it('desabilita o evento lotado e ignora sua seleção pela URL', async () => {
    const fixture = TestBed.createComponent(InscricaoForm);
    fixture.componentRef.setInput('eventoId', '1');
    fixture.detectChanges();
    await fixture.whenStable();

    const eventos = (fixture.nativeElement as HTMLElement).querySelector<HTMLSelectElement>('#evento')!;
    expect(eventos.options[1].disabled).toBe(true);
    expect(eventos.options[2].disabled).toBe(false);
    expect(fixture.componentInstance.form.controls.eventoId.value).toBeNull();
    expect(fixture.nativeElement.textContent).toContain('Não há mais vagas para este evento.');
  });

  it('mantém o evento atual selecionado ao editar uma inscrição de evento lotado', async () => {
    const fixture = TestBed.createComponent(InscricaoForm);
    fixture.componentRef.setInput('id', '10');
    fixture.detectChanges();
    await fixture.whenStable();

    const eventos = (fixture.nativeElement as HTMLElement).querySelector<HTMLSelectElement>('#evento')!;
    expect(eventos.options[1].disabled).toBe(false);
    expect(eventos.options[1].selected).toBe(true);
    expect(fixture.componentInstance.form.controls.eventoId.value).toBe(1);
  });
});
