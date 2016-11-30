package me.roybailey.ticket.domain;

import com.google.common.collect.ImmutableSet;
import me.roybailey.sparkjava.domain.Ticket;
import me.roybailey.sparkjava.domain.TicketMapper;
import me.roybailey.sparkjava.domain.TicketStatus;
import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class TicketTest {

    @Test
    public void testTicketDomain() {
        String[] labels = new String[] {"one","two"};

        Ticket record = Ticket.builder()
                .key("KEY-001")
                .type("Story")
                .title("Test Ticket")
                .status(TicketStatus.INPROGRESS)
                .size(5)
                .withLabels(labels)
                .build();

        assertThat(record.getKey()).isEqualTo("KEY-001");
        assertThat(record.getType()).isEqualTo("Story");
        assertThat(record.getTitle()).isEqualTo("Test Ticket");
        assertThat(record.getStatus()).isEqualTo(TicketStatus.INPROGRESS);
        assertThat(record.getSize()).isEqualTo(5);
        assertThat(record.getLabels()).containsExactly(labels);

        Ticket copyTicket = TicketMapper.copyTicket(record);

        assertThat(copyTicket).isEqualToComparingFieldByField(record);
    }
}
