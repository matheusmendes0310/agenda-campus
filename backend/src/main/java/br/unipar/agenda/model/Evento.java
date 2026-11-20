package br.unipar.agenda.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false, length = 120)
    private String local;

    @Column(nullable = false)
    private Integer vagas;

    // Relacionamento 1:N -> um evento tem muitas inscrições
    @OneToMany(mappedBy = "evento")
    private List<Inscricao> inscricoes = new ArrayList<>();

    public Evento() {
    }

    public Evento(String nome, LocalDate data, String local, Integer vagas) {
        this.nome = nome;
        this.data = data;
        this.local = local;
        this.vagas = vagas;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Integer getVagas() {
        return vagas;
    }

    public void setVagas(Integer vagas) {
        this.vagas = vagas;
    }

    public List<Inscricao> getInscricoes() {
        return inscricoes;
    }
}
