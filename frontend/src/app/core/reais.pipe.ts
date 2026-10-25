import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'reais' })
export class ReaisPipe implements PipeTransform {
  transform(cents: number | null | undefined): string {
    return ((cents ?? 0) / 100).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }
}
