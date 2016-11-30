package me.roybailey.sparkjava.domain;

import feign.Headers;
import feign.Param;
import feign.QueryMap;
import feign.RequestLine;

import java.util.List;

@Headers("Accept: application/json")
public interface TicketApi {

    @RequestLine("GET /tickets")
    List<Ticket> tickets();

    @RequestLine("GET /tickets/{project}?sort={sort}")
    List<Ticket> tickets(@Param("project") String project, @Param("sort") String sort);

    @RequestLine("GET /tickets/{project}?sort={sort}&labels={labels}")
    List<Ticket> getTicketsByLabels(
            @Param("project") String project,
            @Param("sort") String sort,
            @Param("labels") String labels);

    default List<Ticket> tickets(@Param("project") String project) {
        return tickets(project, "status");
    }

    @RequestLine("POST /tickets")
    void addTickets(List<Ticket> tickets);

    @RequestLine("DELETE /tickets")
    void deleteTickets();
}
