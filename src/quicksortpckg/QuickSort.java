package quicksortpckg;

/**
 * Classe utilizada para demonstrar o uso do algoritmo de ordenação
 * Quick Sort.
 */
public class QuickSort {
	/**
	 * Método para a ordenação de um vetor de inteiros.
	 * @param vetor - Vetor de inteiros que sera ordenado.
	 */
	public void ordenarVetor(double[] vetor) {
		quickSort(vetor, 0, vetor.length - 1);
	}

	/**
	 * Método que irá chamar a divisão do vetor nos três vetores do conceito.
	 * Na realidade o vetor do meio esta sendo incarado apenas como o elemento
	 * pivô retornado pelo método dividir.
	 *
	 * @param vetor - Vetor de inteiros que passara pelo Quick Sort.
	 * @param inicio - Indice inicial do vetor que sera considerado no Quick Sort.
	 * @param fim - Indice final do vetor que sera considerado no Quick Sort.
	 */
	private void quickSort(double[] vetor, int inicio, int fim) {
		if(fim > inicio) {
			//Chamada da rotina que ira dividir o vetor em 3 partes.
			int indexPivo = dividir(vetor, inicio, fim);
			/* Chamada recursiva para redivisao do vetor de elementos menores
        que o pivô. */
			quickSort(vetor, inicio, indexPivo - 1);
			/* Chamada recursiva para redivisao do vetor de elementos maiores
        que o pivô. */
			quickSort(vetor, indexPivo + 1, fim);
		}
	}

	/**
	 * Método que ira dividir o vetor e encontrar o indice do pivô.
	 * @param vetor - Vetor de inteiros
	 * @param inicio - Indice inicial do vetor.
	 * @param fim - Indice final do vetor.
	 * @return O indice do pivo.
	 */
	private int dividir(double[] vetor, int inicio, int fim) {
		double pivo;
		int pontEsq, pontDir = fim;
		pontEsq = inicio + 1;
		pivo = vetor[inicio];

		while(pontEsq <= pontDir) {
			/* Vai correr o vetor ate que ultrapasse o outro ponteiro
        ou ate que o elemento em questão seja menor que o pivô. */
			while(pontEsq <= pontDir && vetor[pontEsq] <= pivo) {
				pontEsq++;
			}

			/* Vai correr o vetor ate que ultrapasse o outro ponteiro
        que o elemento em questão seja maior que o pivô. */
			while(pontDir >= pontEsq && vetor[pontDir] > pivo) {
				pontDir--;
			}

			/* Caso os ponteiros ainda nao tenham se cruzado, significa que valores
        menores e maiores que o pivô foram localizados em ambos os lados.
        Trocar estes elementos de lado. */
			if(pontEsq < pontDir) {
				trocar(vetor, pontDir, pontEsq);
				pontEsq++;
				pontDir--;
			}
		}

		trocar(vetor, inicio, pontDir);
		return pontDir;
	}



	/**
	 * Método para trocar dois elementos de um vetor.
	 *
	 * @param vetor - Vetor de inteiros que tera seus elementos trocados.
	 * @param i - Indice do elemento que sera trocado.
	 * @param j - Indice do elemento que sera trocado.
	 */
	private void trocar(double[] vetor, int i, int j) {
		double temp = vetor[i];
		vetor[i] = vetor[j];
		vetor[j] = temp;
	}





}