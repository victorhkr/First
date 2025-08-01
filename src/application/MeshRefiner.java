package application;

import java.util.ArrayList;

public class MeshRefiner {
    public static ArrayList<Ponto> refineMesh(ArrayList<Ponto> arrPontos, int numeroPontosAtual) {
        ArrayList<Ponto> arrPontosRefinados = new ArrayList<>();
        
        // Preservar todos os pontos originais (incluindo propriedades)
        for (Ponto ponto : arrPontos) {
            arrPontosRefinados.add(new Ponto(ponto));
        }
        
        int numeroPontosContorno = numeroPontosAtual - 3;

        final double RAIO_INFLUENCIA = 100;
        final double ESPACAMENTO_MIN = 20;
        final double FATOR_EXPANSAO = 1.8;

        ArrayList<Ponto> pontosRefinamento = new ArrayList<>();
        for (int idx = 3; idx < numeroPontosContorno+3; idx++) {
            pontosRefinamento.add(arrPontos.get(idx));
        }

        for (Ponto pontoRef : pontosRefinamento) {
            double espacamentoAtual = ESPACAMENTO_MIN;
            double raioAtual = espacamentoAtual;
            while (raioAtual < RAIO_INFLUENCIA) {
                int pontosNaCamada = (int) (2 * Math.PI * raioAtual / espacamentoAtual);
                if (pontosNaCamada < 8) pontosNaCamada = 8;
                for (int i = 0; i < pontosNaCamada; i++) {
                    double angulo = 2 * Math.PI * i / pontosNaCamada;
                    double x = pontoRef.x + raioAtual * Math.cos(angulo);
                    double y = pontoRef.y + raioAtual * Math.sin(angulo);
                    boolean pontoValido = true;
                    for (Ponto existente : arrPontosRefinados) {
                        double distX = x - existente.x;
                        double distY = y - existente.y;
                        if (distX * distX + distY * distY < espacamentoAtual * espacamentoAtual) {
                            pontoValido = false;
                            break;
                        }
                    }
                    if (pontoValido) {
                        // Novos pontos não são de contorno
                        arrPontosRefinados.add(new Ponto(x, y));
                    }
                }
                raioAtual = raioAtual * FATOR_EXPANSAO;
            }
        }

        final double ESPACAMENTO_GROSSO = 40;
        for (int i = 0; i < 40; i++) {
            for (int j = 0; j < 30; j++) {
                double x = 5 + ESPACAMENTO_GROSSO * i;
                double y = 5 + ESPACAMENTO_GROSSO * j;
                boolean pontoProximo = false;
                for (Ponto existente : arrPontosRefinados) {
                    double distX = x - existente.x;
                    double distY = y - existente.y;
                    if (distX * distX + distY * distY < 400) {
                        pontoProximo = true;
                        break;
                    }
                }
                if (!pontoProximo) {
                    // Pontos adicionais não são de contorno
                    arrPontosRefinados.add(new Ponto(x, y));
                }
            }
        }

        return arrPontosRefinados;
    }
}