package br.com.caelum.leilao.infra.repositorio;

import br.com.caelum.leilao.dominio.Leilao;

import java.util.List;

/**
 * Created by ana on 20/02/18.
 */
public interface RepositorioDeLeiloes {
    void salva(Leilao leilao);
    List<Leilao> encerrados();
    List<Leilao> correntes();
    void atualiza(Leilao leilao);
}
