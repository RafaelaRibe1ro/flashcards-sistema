package com.rafaelaribeiro.categoria_service;

import com.rafaelaribeiro.categoria_service.messaging.CategoriaEvent;
import com.rafaelaribeiro.categoria_service.messaging.CategoriaEventPublisher;
import com.rafaelaribeiro.categoria_service.model.Categoria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoriaEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private CategoriaEventPublisher publisher;

    @Test
    void devePublicarEventoComExchangeERoutingKeyCorretos() {
        Categoria categoria = new Categoria("Java", "Linguagem de programação");
        categoria.setId(1L);
        categoria.setCriadoEm(LocalDateTime.now());

        publisher.publicar(categoria, "criada");

        ArgumentCaptor<CategoriaEvent> captor = ArgumentCaptor.forClass(CategoriaEvent.class);
        verify(rabbitTemplate).convertAndSend(eq("categoria.events"), eq("categoria.criada"), captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(1L);
        assertThat(captor.getValue().nome()).isEqualTo("Java");
        assertThat(captor.getValue().tipoEvento()).isEqualTo("criada");
    }

    @Test
    void naoDeveLancarExcecaoQuandoBrokerIndisponivel() {
        Categoria categoria = new Categoria("Java", "Linguagem de programação");
        categoria.setId(1L);
        doThrow(new AmqpException("broker indisponível")).when(rabbitTemplate)
                .convertAndSend(any(String.class), any(String.class), any(Object.class));

        publisher.publicar(categoria, "criada");
    }
}
