package br.com.caelum.leilao.infra.repositorio;

import br.com.caelum.leilao.dominio.Pagamento;

/**
 * Created by ana on 21/02/18.
 */
public interface RepositorioPagamento {
    void salva(Pagamento pagamento);
}
