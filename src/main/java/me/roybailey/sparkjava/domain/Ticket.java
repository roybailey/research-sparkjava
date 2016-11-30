package me.roybailey.sparkjava.domain;

import com.google.common.collect.ImmutableSet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Ticket {

    String project;
    String key;
    String type;
    String title;
    Integer size;
    TicketStatus status;
    Set<String> labels;

    public static class TicketBuilder {
        public TicketBuilder withLabels(String... labels) {
            ImmutableSet.Builder<String> builder = new ImmutableSet.Builder<>();
            Arrays.stream(labels).forEach(builder::add);
            Set<String> setLabels = builder.build();
            if(this.labels != null)
                setLabels.addAll(this.labels);
            return this.labels(setLabels);
        }
    }
}
