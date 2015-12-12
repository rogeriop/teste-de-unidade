package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;

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
		// a primeira invocação
		inOrder.verify(daoFalso, times(1)).atualiza(leilao1);
		// a segunra invocação
		inOrder.verify(carteiroFalso, times(1)).envia(leilao1);

	}

	@Test
	public void deveContinuarAExecucaoMesmoQuandoODaoFalha() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 1);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Notebook").naData(antiga).constroi();
		
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
		doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao1);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();
		
		verify(daoFalso).atualiza(leilao2);
		verify(carteiroFalso).envia(leilao2);
	}

	@Test
	public void deveContinuarAExecucaoMesmoQuandoOEnviadorDeEmailFalha() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 1);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Notebook").naData(antiga).constroi();
		
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
		doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao1);

        doThrow(new RuntimeException()).when(carteiroFalso).envia(leilao1);

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();
		
		verify(daoFalso).atualiza(leilao2);
		verify(carteiroFalso).envia(leilao2);
	}
	
	@Test
	public void carteiroNuncaInvocadoQuandoTodosLeiloesLancamExcecoes() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 1);
		
		Leilao leilao1 = new CriadorDeLeilao().para("TV").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Notebook").naData(antiga).constroi();
		
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

		EnviadorDeEmail carteiroFalso = mock(EnviadorDeEmail.class);
		
		// ESSAS DUAS LINHAS PODEM SER TROCADAS PELA TERCEIRA
//		doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao1);
//      doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao2);

		doThrow(new RuntimeException()).when(daoFalso).atualiza(any(Leilao.class));
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso, carteiroFalso);
		encerrador.encerra();
		
		// ESSAS DUAS LINHAS PODEM SER TROCADAS PELA TERCEIRA
//		verify(carteiroFalso, never()).envia(leilao1);
//		verify(carteiroFalso, never()).envia(leilao2);
		
		verify(carteiroFalso, never()).envia(any(Leilao.class));
	}
	
	
	
	
}
