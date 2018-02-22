package br.com.caelum.leilao.servico.email;

import br.com.caelum.leilao.dominio.Leilao;

/**
 * Created by ana on 20/02/18.
 */
public interface EnviadorDeEmail {
    void envia(Leilao leilao);
}
