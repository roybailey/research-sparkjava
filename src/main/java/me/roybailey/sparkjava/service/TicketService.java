package me.roybailey.sparkjava.service;

import me.roybailey.sparkjava.domain.Ticket;
import me.roybailey.sparkjava.domain.TicketMapper;
import spark.utils.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TicketService {

    private Map<String, Ticket> allTickets = new ConcurrentHashMap<>();

    public List<Ticket> getAllTickets() {
        return new ArrayList<Ticket>(this.allTickets.values());
    }

    public List<Ticket> getAllTickets(String project, String labels, String sort) {
        return getAllTickets().stream()
                .filter((ticket) -> project.equalsIgnoreCase(ticket.getProject()))
                .filter((ticket) -> StringUtils.isEmpty(labels) || ticket.getLabels().contains(labels))
                .sorted((t1,t2)->{
                    if("size".equalsIgnoreCase(sort)) {
                        return t2.getSize() - t1.getSize();
                    }
                    return t1.getStatus().ordinal() - t2.getStatus().ordinal();
                })
                .collect(Collectors.toList());
    }

    public Ticket getTicket(String key) {
        return this.allTickets.get(key);
    }

    public Ticket addTicket(Ticket ticket) {
        assert ticket.getKey() == null;
        Ticket newTicket = TicketMapper.copyTicket(ticket);
        newTicket.setKey(UUID.randomUUID().toString());
        this.allTickets.put(newTicket.getKey(), newTicket);
        return newTicket;
    }

    public void addTickets(List<Ticket> tickets) {
        tickets.forEach(this::addTicket);
    }

    public void deleteAll() {
        allTickets.clear();
    }
}