package br.com.caelum.agiletickets.models;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import com.sun.org.omg.CORBA.ExceptionDescription;

@Entity
public class Espetaculo {

	@Id
	@GeneratedValue
	private Long id;

	private String nome;

	private String descricao;

	@Enumerated(EnumType.STRING)
	private TipoDeEspetaculo tipo;

	@OneToMany(mappedBy="espetaculo")
	private List<Sessao> sessoes = newArrayList();

	@ManyToOne
	private Estabelecimento estabelecimento;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public TipoDeEspetaculo getTipo() {
		return tipo;
	}

	public void setTipo(TipoDeEspetaculo tipo) {
		this.tipo = tipo;
	}

	public List<Sessao> getSessoes() {
		return sessoes;
	}

	public void setEstabelecimento(Estabelecimento estabelecimento) {
		this.estabelecimento = estabelecimento;
	}

	public Estabelecimento getEstabelecimento() {
		return estabelecimento;
	}

	public List<Sessao> criaSessoes(LocalDate inicio, LocalDate fim, LocalTime horario, Periodicidade periodicidade) {
		/*if (fim.isBefore(inicio)) {
			LocalDate Aux = fim;
			fim = inicio;
			inicio = Aux;
		}*/
		int diasNoIntervalo = Days.daysBetween(inicio, fim).getDays();
		List<Sessao> sessoes = newArrayList();
		for(int i = 0; i<=diasNoIntervalo; i++) {
			Sessao sessao = new Sessao();
			sessao.setInicio(inicio.plusDays(i).toDateTime(horario));
			//sessao.setDuracaoEmMinutos(duracaoEmMinutos)
			sessoes.add(sessao);
		}
		return sessoes;
	}

}
