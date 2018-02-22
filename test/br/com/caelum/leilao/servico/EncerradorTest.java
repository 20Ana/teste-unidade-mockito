package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.repositorio.RepositorioDeLeiloes;
import br.com.caelum.leilao.servico.email.EnviadorDeEmail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by ana on 20/02/18.
 */
public class EncerradorTest {
    Calendar data = Calendar.getInstance();

    //instanciar
    RepositorioDeLeiloes daoFalso;
    EnviadorDeEmail carteiro;
    EncerradorDeLeilao encerrador;

    //cria leiloes
    Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma").naData(data).constroi();
    Leilao leilao2 = new CriadorDeLeilao().para("Geladeira").naData(data).constroi();

    @Before
    public void setUp (){
        daoFalso = mock(RepositorioDeLeiloes.class);
        carteiro = mock(EnviadorDeEmail.class);

        encerrador = new EncerradorDeLeilao(daoFalso, carteiro);
    }

    @Test
    public void encerraLeilaoAntigo (){
        data.set(1999, 1, 20);

        List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);

        when(daoFalso.correntes()).thenReturn(leiloesAntigos);

        encerrador.encerra();

        InOrder inOrder = inOrder(daoFalso, carteiro);
        inOrder.verify(daoFalso, times(1)).atualiza(leilao1);
        inOrder.verify(carteiro, times(1)).envia(leilao1);

        assertEquals(2, encerrador.getTotalEncerrados());
        assertTrue(leilao1.isEncerrado());
        assertTrue(leilao2.isEncerrado());
    }

    @Test
    public void naoDeveEncerrarLeiloesQueComecaramMenosDeUmaSemanaAtras() {
        data.add(Calendar.DAY_OF_MONTH, -1);

        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

        encerrador.encerra();

        assertEquals(0, encerrador.getTotalEncerrados());
        assertFalse(leilao1.isEncerrado());
        assertFalse(leilao2.isEncerrado());

        verify(daoFalso, never()).atualiza(leilao1);
        verify(daoFalso, never()).atualiza(leilao2);
    }

    @Test
    public void naoEncerrarLeilaoQueNaoExiste (){
        when(daoFalso.correntes()).thenReturn(new ArrayList<Leilao>());

        encerrador.encerra();

        assertEquals(0, encerrador.getTotalEncerrados());
    }

    @Test
    public void deveAtualizarLeiloesEncerrados (){
        data.set(199, 1, 20);

        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1));

        encerrador.encerra();

        InOrder inOrder = inOrder(daoFalso, carteiro);
        inOrder.verify(daoFalso, times(1)).atualiza(leilao1);
        inOrder.verify(carteiro, times(1)).envia(leilao1);

        verify(daoFalso, times(1)).atualiza(leilao1);
    }

    @Test
    public void deveContinuarAExecucaoMesmoQuandoODaoFalhar (){
        data.set(199, 1, 20);

        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
        doThrow(new RuntimeException()).when(carteiro).envia(leilao1);

        encerrador.encerra();

        verify(daoFalso).atualiza(leilao2);
        verify(carteiro).envia(leilao2);
    }

    @Test
    public void carteiroNuncaDeveSerInvocado (){
        data.set(199, 1, 20);

        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
        doThrow(new RuntimeException()).when(daoFalso).atualiza(any(Leilao.class));
        encerrador.encerra();

        verify(carteiro, never()).envia(any(Leilao.class));
    }
}