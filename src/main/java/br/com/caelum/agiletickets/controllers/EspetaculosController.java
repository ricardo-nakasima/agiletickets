package br.com.caelum.agiletickets.controllers;

import static br.com.caelum.vraptor.view.Results.status;

import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import br.com.caelum.agiletickets.domain.Agenda;
import br.com.caelum.agiletickets.domain.DiretorioDeEstabelecimentos;
import br.com.caelum.agiletickets.models.Espetaculo;
import br.com.caelum.agiletickets.models.Periodicidade;
import br.com.caelum.agiletickets.models.Sessao;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.validator.ValidationMessage;

import com.google.common.base.Strings;

@Resource
public class EspetaculosController {

	private final Agenda agenda;
	private Validator validator;
	private Result result;

	private final DiretorioDeEstabelecimentos estabelecimentos;

	public EspetaculosController(Agenda agenda, DiretorioDeEstabelecimentos estabelecimentos, Validator validator, Result result) {
		this.agenda = agenda;
		this.estabelecimentos = estabelecimentos;
		this.validator = validator;
		this.result = result;
	}

	@Get
	@Path("/espetaculos")
	public List<Espetaculo> lista() {
		result.include("estabelecimentos", estabelecimentos.todos());
		return agenda.espetaculos();
	}

	@Post
	@Path("/espetaculos")
	public void adicionarEspetaculo(Espetaculo espetaculo) {
		validarString(espetaculo.getNome(), "Nome do espetáculo não pode estar em branco");
		validarString(espetaculo.getDescricao(), "Descrição do espetáculo não pode estar em branco");
		validator.onErrorRedirectTo(this).lista();

		agenda.cadastra(espetaculo);
		result.redirectTo(this).lista();
	}

	private void validarCondicao(boolean condicao, String mensagem) {
		if (condicao) {
			validator.add(new ValidationMessage(mensagem, ""));
		}
	}
	
	private void validarString(String textoParaValidar, String mensagem) {
		validarCondicao(Strings.isNullOrEmpty(textoParaValidar), mensagem);
	}

	@Get
	@Path("/sessao/{id}")
	public void sessao(Long id) {
		Sessao sessao = agenda.sessao(id);
		if (sessao == null) {
			result.notFound();
		}

		result.include("sessao", sessao);
	}

	@Post
	@Path("/sessao/{sessaoId}/reserva")
	public void reservar(Long sessaoId, final Integer quantidade) {
		Sessao sessao = agenda.sessao(sessaoId);
		if (sessao == null) {
			result.notFound();
			return;
		}
		validarCondicao(quantidade < 1, "Você deve escolher um lugar ou mais");
		validarCondicao(!sessao.podeReservar(quantidade), "Não existem ingressos disponíveis");
		validator.onErrorRedirectTo(this).sessao(sessao.getId());

		sessao.reserva(quantidade);
		result.include("message", "Sessao reservada com sucesso");
		result.redirectTo(IndexController.class).index();
	}

	@Get
	@Path("/espetaculo/{espetaculoId}/sessoes")
	public void sessoes(Long espetaculoId) {
		Espetaculo espetaculo = buscarEspetaculo(espetaculoId);
		result.include("espetaculo", espetaculo);
	}

	@Post
	@Path("/espetaculo/{espetaculoId}/sessoes")
	public void cadastraSessoes(Long espetaculoId, LocalDate dataInicio, LocalDate dataFinal, LocalTime horario, Periodicidade periodicidade) {
		Espetaculo espetaculo = buscarEspetaculo(espetaculoId);

		List<Sessao> sessoes = espetaculo.criaSessoes(dataInicio, dataFinal, horario, periodicidade);

		agenda.agende(sessoes);

		result.include("message", sessoes.size() + " sessoes criadas com sucesso");
		result.redirectTo(this).lista();
	}

	private Espetaculo buscarEspetaculo(Long espetaculoId) {
		Espetaculo espetaculo = agenda.espetaculo(espetaculoId);
		validarCondicao(espetaculo == null, "");
		validator.onErrorUse(status()).notFound();
		return espetaculo;
	}
}
