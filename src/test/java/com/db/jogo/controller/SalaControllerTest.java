package com.db.jogo.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.db.jogo.model.Baralho;
import com.db.jogo.model.CartaDoJogo;
import com.db.jogo.model.CartaInicio;
import com.db.jogo.model.CartaObjetivo;
import com.db.jogo.model.Jogador;
import com.db.jogo.model.Sala;
import com.db.jogo.service.SalaService;
import com.fasterxml.jackson.databind.ObjectMapper;


@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Sala Controller Teste")
class SalaControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @MockBean
    private SalaService salaService;

    CartaInicio cartaInicio = new CartaInicio();
    Baralho baralho = new Baralho();
    CartaDoJogo carta = new CartaDoJogo();
    CartaObjetivo cartaObjetivo = new CartaObjetivo();
    Jogador jogador = new Jogador();
    Sala sala = new Sala();

    @BeforeEach
    public void init(){
        cartaInicio.setId(UUID.randomUUID());
        cartaInicio.setNome("Teste");
        cartaInicio.setDescricao("Descricao");

        carta.setId(UUID.randomUUID());
        carta.setPontos(2);
        carta.setBonus(true);
        carta.setCategoria("Visual");
        carta.setTexto("Deficiencia visual");
        carta.setFonte("Wikipedia");
        carta.setValorCorGrande(0);
        carta.setValorCorPequeno(0);
        carta.setTipo("Ação");

        cartaObjetivo.setId(UUID.randomUUID());
        cartaObjetivo.setDescricao("Texto da carta");
        cartaObjetivo.setPontos(0);
        cartaObjetivo.setClassificacao("Ganhe pontos");
        cartaObjetivo.setCategoria("Física");

        baralho.setId(UUID.randomUUID());
        baralho.setCodigo("LILA");
        baralho.setTitulo("Teste");
        baralho.setDescricao("Exemplo");
        baralho.setCartaInicio(new ArrayList<>());
        baralho.adicionarCartaDoInicio(cartaInicio);
        baralho.setCartasDoJogo(new ArrayList<>());
        baralho.adicionarCartadoJogo(carta);
        baralho.setCartasObjetivo(new ArrayList<>());
        baralho.adicionarCartaDoInicio(cartaInicio);

        jogador.setId(UUID.randomUUID());
        jogador.setNome("Felipe");
        jogador.setPontos(0);
        jogador.setBonusCoracaoGra(0);
        jogador.setBonusCoracaoPeq(0);
        jogador.setCoracaoGra(0);
        jogador.setCoracaoPeq(0);
        jogador.setCartasDoJogo(new ArrayList());
        jogador.adicionaCarta(carta);
        jogador.adicionaObjetivo(cartaObjetivo);

        sala.setId(UUID.randomUUID());
        sala.setBaralho(baralho);
        sala.setHash("hashpraentrar");
        sala.setStatus(Sala.StatusEnum.NOVO);
        sala.setJogadores(new ArrayList<>());
        sala.adicionarJogador(jogador);
    }

    @Test
    @DisplayName("Teste de Salvar/Criar uma sala do Controller de Sala")
    void criarSala() throws Exception {

        given(salaService.saveSala(sala)).willReturn(sala);

        ObjectMapper mapper = new ObjectMapper();
        String newSalaAsJSON = mapper.writeValueAsString(sala);
        this.mockMvc.perform(post("/sala")
                .content(newSalaAsJSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Teste de encontrar sala por hash do Controller de Sala")
    void encontrarSalaPorHash() throws Exception{

        given(salaService.findSalaByHash(sala.getHash())).willReturn(Optional.of(sala));

        ObjectMapper mapper = new ObjectMapper();
        String encontrarSalaAsJSON = mapper.writeValueAsString(sala);
        this.mockMvc.perform(get("/sala/" + sala.getHash())
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().json(encontrarSalaAsJSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Teste de Salvar/Criar uma sala do Controller de Sala com erro")
    void criarSalaComErro() throws Exception {
        Sala sala = new Sala();

        given(salaService.saveSala(sala)).willReturn(sala);

        ObjectMapper mapper = new ObjectMapper();
        String newSalaAsJSON = mapper.writeValueAsString(sala);
        this.mockMvc.perform(post("/sala")
                .content(newSalaAsJSON)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Teste de encontrar sala por hash do Controller de Sala com erro")
    void encontrarSalaPorHashComErro() throws Exception{
        Sala sala = new Sala();
        sala.setId(UUID.randomUUID());
        sala.setBaralho(baralho);
        sala.setHash("hashpraentrar");
        sala.setStatus(Sala.StatusEnum.NOVO);
        sala.setJogadores(new ArrayList<>());
        sala.adicionarJogador(jogador);

        given(salaService.findSalaByHash("hashpraentrarerrado")).willReturn(Optional.of(sala));

        ObjectMapper mapper = new ObjectMapper();
        String encontrarSalaAsJSON = mapper.writeValueAsString(sala);
        this.mockMvc.perform(get("/sala/" + sala.getHash())
                        .content(encontrarSalaAsJSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }
}