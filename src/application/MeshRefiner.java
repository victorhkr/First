package application;

import java.util.ArrayList;

public class MeshRefiner {
    public static ArrayList<Ponto> refineMesh(ArrayList<Ponto> arrPontos, int numeroPontosAtual) {
        ArrayList<Ponto> arrPontosRefinados = new ArrayList<>(arrPontos);
        int numeroPontosContorno = numeroPontosAtual - 3;

        final double RAIO_INFLUENCIA = 100;
        final double ESPACAMENTO_MIN = 5;
        final double FATOR_EXPANSAO = 1.5;
        final int CAMADAS = 5;

        ArrayList<Ponto> pontosRefinamento = new ArrayList<>();
        for (int idx = 3; idx < numeroPontosContorno; idx++) {
            pontosRefinamento.add(arrPontosRefinados.get(idx));
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
                        arrPontosRefinados.add(new Ponto(x, y));
                    }
                }
                espacamentoAtual *= FATOR_EXPANSAO;
                raioAtual += espacamentoAtual;
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
                    arrPontosRefinados.add(new Ponto(x, y));
                }
            }
        }

        return arrPontosRefinados;
    }
}