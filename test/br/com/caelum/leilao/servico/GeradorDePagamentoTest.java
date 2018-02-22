package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.infra.relogio.Relogio;
import br.com.caelum.leilao.infra.repositorio.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.repositorio.RepositorioPagamento;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import sun.security.x509.AVA;
import sun.util.resources.cldr.aa.CalendarData_aa_DJ;

import java.util.Arrays;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by ana on 21/02/18.
 */
public class GeradorDePagamentoTest {
    RepositorioDeLeiloes leiloes;
    RepositorioPagamento pagamento;
    Avaliador avaliador;
    Relogio relogio;

    @Before
    public void setUp(){
        leiloes = mock(RepositorioDeLeiloes.class);
        pagamento = mock(RepositorioPagamento.class);
        relogio = mock(Relogio.class);
        avaliador = new Avaliador();
    }

    @Test
    public void deveGerarPagamentoParaUmLeilaoEncerrado(){
        Leilao leilao = new CriadorDeLeilao().para("PlayStation")
                .lance(new Usuario("José da Silva"), 2000.0)
                .lance(new Usuario("Maria Pereira"), 2500.0)
                .constroi();

        when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));

        GeradorDePagamento gerador = new GeradorDePagamento(leiloes, pagamento, avaliador);
        gerador.gera();

        ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);

        verify(pagamento).salva(argumento.capture());

        Pagamento pagamentoGerado = argumento.getValue();

        assertEquals(2500.0, pagamentoGerado.getValor(), 0.0001);
    }
    @Test
    public void deveEmpurrarParaProximoDiaUtil (){
        Leilao leilao = new CriadorDeLeilao().para("PlayStation")
                .lance(new Usuario("José da Silva"), 2000.0)
                .lance(new Usuario("Maria Pereira"), 2500.0)
                .constroi();

        when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));

        Calendar sabado = Calendar.getInstance();
        sabado.set(2012, Calendar.APRIL, 7);

        when(relogio.hoje()).thenReturn(sabado);

        GeradorDePagamento gerador = new GeradorDePagamento(leiloes, pagamento, avaliador);

        gerador.gera();

        ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
        verify(pagamento).salva(argumento.capture());
        Pagamento pagamentoGerado = argumento.getValue();

        assertEquals(Calendar.MONDAY, pagamentoGerado.getData().get(Calendar.DAY_OF_WEEK));
        assertEquals(9, pagamentoGerado.getData().get(Calendar.DAY_OF_MONTH));
    }
}