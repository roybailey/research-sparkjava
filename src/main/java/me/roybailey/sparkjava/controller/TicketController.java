package me.roybailey.sparkjava.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import me.roybailey.sparkjava.domain.Ticket;
import me.roybailey.sparkjava.service.TicketService;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.ResponseTransformer;

import java.util.List;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;

@AllArgsConstructor
public class TicketController {

    private static final TypeReference<List<Ticket>> typeListTicket = new TypeReference<List<Ticket>>(){};

    TicketService ticketService;

    public class JsonTransformer implements ResponseTransformer {

        ObjectMapper mapper;

        public JsonTransformer(ObjectMapper mapper) {
            this.mapper = mapper;
        }

        @Override
        public String render(Object model) throws JsonProcessingException {
            return mapper.writeValueAsString(model);
        }

    }

    public void init() {
        ObjectMapper mapper = new ObjectMapper();

        // insert tickets
        post("/api/v1/tickets", (request, response) -> {
            System.out.println(request.body());
            List<Ticket> results = mapper.readValue(request.bodyAsBytes(),typeListTicket);
            ticketService.addTickets(results);
            return results;
        }, new JsonTransformer(mapper));

        // get all tickets
        get("/api/v1/tickets", (request, response) -> {
            response.status(200);
            response.type("application/json");
            List<Ticket> results = ticketService.getAllTickets();
            return results;
        }, new JsonTransformer(mapper));

        // get all tickets for project
        get("/api/v1/tickets/:project", (request, response) -> {
            response.status(200);
            response.type("application/json");
            QueryParamsMap queryMap = request.queryMap();
            List<Ticket> results = ticketService.getAllTickets(request.params("project"), queryMap.value("labels"), queryMap.value("sort"));
            return results;
        }, new JsonTransformer(mapper));

        // delete all tickets
        delete("/api/v1/tickets", (Request request, Response response) -> {
            response.status(200);
            ticketService.deleteAll();
            return "";
        });
    }
}
