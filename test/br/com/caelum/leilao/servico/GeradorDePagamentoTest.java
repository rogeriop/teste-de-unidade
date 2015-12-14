package br.com.caelum.leilao.servico;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamentos;
import br.com.caelum.leilao.infra.relogio.Relogio;
public class GeradorDePagamentoTest {
	
	@Test
	public void deveGerarPagamentoParaLeilaoEncerrado() {
		RepositorioDeLeiloes leiloes = mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = mock(RepositorioDePagamentos.class);
		Avaliador avaliador = mock(Avaliador.class);
		
		Leilao leilao = new CriadorDeLeilao().para("Conjunto de sofá")
				.lance(new Usuario("José"), 1000.0)
				.lance(new Usuario("Maria"), 1500.0)
				.constroi();
		
		when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));
		when(avaliador.getMaiorLance()).thenReturn(1500.0);
		GeradorDePagamento gerador = new GeradorDePagamento(leiloes, avaliador, pagamentos);
		gerador.gera();

		ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
		verify(pagamentos).salva(argumento.capture());
		Pagamento pagamentoGerado = argumento.getValue();
		
		assertEquals(1500.0, pagamentoGerado.getValor(), 0.00001);

	}

	@Test
	public void deveGerarPagamentoParaLeilaoEncerradoSemMocarAvaliador() {
		RepositorioDeLeiloes leiloes = mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = mock(RepositorioDePagamentos.class);
//		Avaliador avaliador = mock(Avaliador.class);
		Avaliador avaliador = new Avaliador();
		
		Leilao leilao = new CriadorDeLeilao().para("Conjunto de sofá")
				.lance(new Usuario("José"), 1000.0)
				.lance(new Usuario("Maria"), 1500.0)
				.constroi();
		
		when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));
//		when(avaliador.getMaiorLance()).thenReturn(1500.0);

		avaliador.avalia(leilao);
		
		
		
		GeradorDePagamento gerador = new GeradorDePagamento(leiloes, avaliador, pagamentos);
		gerador.gera();

		ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
		verify(pagamentos).salva(argumento.capture());
		Pagamento pagamentoGerado = argumento.getValue();
		
		assertEquals(1500.0, pagamentoGerado.getValor(), 0.00001);

	}
	
	@Test
	public void quandoForUmSabadoDeveEmpurrarParaProximoSegunda () {
		RepositorioDeLeiloes leiloes = mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = mock(RepositorioDePagamentos.class);
		Relogio relogio = mock(Relogio.class);
		
		
		Leilao leilao = new CriadorDeLeilao()
				.para("Geladeira")
				.lance(new Usuario("Maria"), 2500)
				.lance(new Usuario("Maira"), 1000)
				.lance(new Usuario("Pedro"), 1500)
				.constroi();

		Calendar sabado = Calendar.getInstance();
		sabado.set(2015, Calendar.DECEMBER, 13);
		
		when(relogio.hoje()).thenReturn(sabado);
		
		when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));
		GeradorDePagamento gerador = new GeradorDePagamento(leiloes, new Avaliador(), pagamentos);
		gerador.gera();
		
		
		ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
		verify(pagamentos).salva(argumento.capture());
		
		Pagamento pagamentoGerado = argumento.getValue();
		assertEquals(Calendar.MONDAY, pagamentoGerado.getData().get(Calendar.DAY_OF_WEEK));
		
		
	}

	@Test
	public void quandoForUmDomingoDeveEmpurrarParaProximoSegunda () {
		RepositorioDeLeiloes leiloes = mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = mock(RepositorioDePagamentos.class);
		Relogio relogio = mock(Relogio.class);
		
		
		Leilao leilao = new CriadorDeLeilao()
				.para("Geladeira")
				.lance(new Usuario("Maria"), 2500)
				.lance(new Usuario("Maira"), 1000)
				.lance(new Usuario("Pedro"), 1500)
				.constroi();

		Calendar sabado = Calendar.getInstance();
		sabado.set(2015, Calendar.DECEMBER, 14);
		
		when(relogio.hoje()).thenReturn(sabado);
		
		when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));
		GeradorDePagamento gerador = new GeradorDePagamento(leiloes, new Avaliador(), pagamentos);
		gerador.gera();
		
		
		ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
		verify(pagamentos).salva(argumento.capture());
		
		Pagamento pagamentoGerado = argumento.getValue();
		assertEquals(Calendar.MONDAY, pagamentoGerado.getData().get(Calendar.DAY_OF_WEEK));
		
		
	}

}
