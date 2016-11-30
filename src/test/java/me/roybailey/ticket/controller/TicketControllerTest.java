package me.roybailey.ticket.controller;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableList;
import feign.Feign;
import feign.codec.Decoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import me.roybailey.sparkjava.TicketApplication;
import me.roybailey.sparkjava.domain.Ticket;
import me.roybailey.sparkjava.domain.TicketApi;
import me.roybailey.sparkjava.domain.TicketStatus;
import org.junit.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class TicketControllerTest {

    TicketApi api;

    static Thread server = new Thread(()->TicketApplication.main(new String[]{}));

    @BeforeClass
    public static void startServer() throws InterruptedException {
        server.start();
        // give it time to load and start listening..
        Thread.sleep(TimeUnit.SECONDS.toMillis(10));
    }

    @AfterClass
    public static void stopServer() {
        server.interrupt();
    }

    @Before
    public void connect() {
        ObjectMapper mapper = new ObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.api = Feign.builder()
                .encoder(new JacksonEncoder(mapper))
                .decoder(new JacksonDecoder(mapper))
                .logger(new feign.Logger.ErrorLogger())
                .logLevel(feign.Logger.Level.BASIC)
                .target(TicketApi.class, "http://localhost:4567/api/v1");

        List<Ticket> tickets = new ImmutableList.Builder<Ticket>()
                .add(Ticket.builder()
                        .project("TODO")
                        .title("Shopping")
                        .status(TicketStatus.TODO)
                        .size(3)
                        .withLabels("housework")
                        .build())
                .add(Ticket.builder()
                        .project("TODO")
                        .title("Decorating Bedroom")
                        .status(TicketStatus.INPROGRESS)
                        .size(5)
                        .withLabels("home", "diy")
                        .build())
                .add(Ticket.builder()
                        .project("TODO")
                        .title("Ironing")
                        .status(TicketStatus.TODO)
                        .size(7)
                        .withLabels("housework")
                        .build())
                .add(Ticket.builder()
                        .project("WORK")
                        .title("Find new job")
                        .status(TicketStatus.INPROGRESS)
                        .size(9)
                        .withLabels("lifestyle")
                        .build())
                .build();
        this.api.addTickets(tickets);
    }

    @After
    public void teardown() {
        api.deleteTickets();
    }

    @Test
    public void testTicketController() {
        List<Ticket> tickets = api.tickets();
        tickets.forEach(System.out::println);
        assertThat(tickets).hasSize(4);
    }

    @Test
    public void testTicketControllerByProject() {
        List<Ticket> tickets = api.tickets("TODO");
        tickets.forEach(System.out::println);
        assertThat(tickets).hasSize(3);
        assertThat(tickets.get(0).getStatus()).isEqualTo(TicketStatus.TODO);
        assertThat(tickets.get(1).getStatus()).isEqualTo(TicketStatus.TODO);
        assertThat(tickets.get(2).getStatus()).isEqualTo(TicketStatus.INPROGRESS);
    }

    @Test
    public void testTicketControllerByProjectSortedBySize() {
        List<Ticket> tickets = api.tickets("TODO", "size");
        tickets.forEach(System.out::println);
        assertThat(tickets).hasSize(3);
        assertThat(tickets.get(0).getSize()).isGreaterThan(tickets.get(1).getSize());
        assertThat(tickets.get(1).getSize()).isGreaterThan(tickets.get(2).getSize());
    }

    @Test
    public void testTicketControllerByLabels() {
        List<Ticket> tickets = api.getTicketsByLabels("TODO", "size", "housework");
        tickets.forEach(System.out::println);
        assertThat(tickets).hasSize(2);
    }

}
