package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.inOrder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.mockito.InOrder;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;

public class EncerradorDeLeilaoTest {

	
	@Test
	public void deveEncerrarleiloesQueComecaramUmaSemanaAntes() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Celular").naData(antiga).constroi();
		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);
		
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		when(daoFalso.correntes()).thenReturn(leiloesAntigos);

		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();
		
		assertEquals(2, encerrador.getTotalEncerrados());
		assertTrue(leilao1.isEncerrado());
		assertTrue(leilao2.isEncerrado());
		
	}
	
	@Test
	public void naoDeveEncerrarLeiloesQueComecaramOntem() {

		Calendar ontem = Calendar.getInstance();
		ontem.add(Calendar.DAY_OF_MONTH, -1);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV").naData(ontem).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Celular").naData(ontem).constroi();
		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);
		
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		when(daoFalso.correntes()).thenReturn(leiloesAntigos);
		
		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();
		
		assertEquals(0, encerrador.getTotalEncerrados());
		assertFalse(leilao1.isEncerrado());
		assertFalse(leilao2.isEncerrado());
		
	}

	@Test
	public void naoDeveEncerrarLeiloesQueComecaramMenosDeUmaSemana() {
		
		Calendar ontem = Calendar.getInstance();
		ontem.add(Calendar.DAY_OF_MONTH, -1);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV").naData(ontem).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Celular").naData(ontem).constroi();
		List<Leilao> leiloesAntigos = Arrays.asList(leilao1, leilao2);
		
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		when(daoFalso.correntes()).thenReturn(leiloesAntigos);
		
		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();
		
		assertEquals(0, encerrador.getTotalEncerrados());
		assertFalse(leilao1.isEncerrado());
		assertFalse(leilao2.isEncerrado());
		
		verify(daoFalso, never()).atualiza(leilao1);
		verify(daoFalso, never()).atualiza(leilao2);
		
	}
	
	@Test
	public void naoDeveEncerrarLeiloesSeListaVazia() {
		
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		when(daoFalso.correntes()).thenReturn(new ArrayList<Leilao>());
		
		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();
		
		assertEquals(0, encerrador.getTotalEncerrados());
		
	}

	@Test
	public void deveAtualizarLeiloesEncerrados() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);
		
		Leilao leilao1 = new CriadorDeLeilao().para("Tv de Plasma").naData(antiga).constroi();
		
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1));
		
		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();
		
		verify(daoFalso, times(1)).atualiza(leilao1);
		
		
	}

	@Test
	public void deveAtualizarLeiloesEncerradosEEnvialosPorEmail() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);
		
		Leilao leilao1 = new CriadorDeLeilao().para("Tv de Plasma").naData(antiga).constroi();
		
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1));
		
		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();
		
        // passamos os mocks que serao verificados
        InOrder inOrder = inOrder(daoFalso, carteiroFalso);
        // a primeira invoca��o
        inOrder.verify(daoFalso, times(1)).atualiza(leilao1);    
        // a segunra invoca��o
        inOrder.verify(carteiroFalso, times(1)).envia(leilao1);		
		
	}
	
}
