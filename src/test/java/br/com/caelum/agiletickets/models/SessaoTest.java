package br.com.caelum.agiletickets.models;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SessaoTest {
	
	private Sessao sessao;
	
	@Before
	public void setUp() {
		sessao = new Sessao();
	}
	
	@After
	public void setDown() {
	}
	
	@Test
	public void deveVender2ingressoSeHa2vagas() throws Exception {
		sessao.setTotalIngressos(2);

		Assert.assertTrue(sessao.podeReservar(2));
	}
	
	@Test
	public void naoDeveVender3ingressoSeHa2vagas() throws Exception {
		sessao.setTotalIngressos(2);

		Assert.assertFalse(sessao.podeReservar(3));
	}

	@Test
	public void reservarIngressosDeveDiminuirONumeroDeIngressosDisponiveis() throws Exception {
		sessao.setTotalIngressos(5);

		sessao.reserva(3);
		Assert.assertEquals(2, sessao.getIngressosDisponiveis().intValue());
	}
	
}
