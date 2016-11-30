package me.roybailey.sparkjava;

import me.roybailey.sparkjava.controller.TicketController;
import me.roybailey.sparkjava.domain.Ticket;
import me.roybailey.sparkjava.domain.TicketStatus;
import me.roybailey.sparkjava.service.TicketService;

import static spark.Spark.get;
import static spark.Spark.post;

public class TicketApplication {

    public static void main(String[] args) {
        TicketService service = new TicketService();
        TicketController controller = new TicketController(service);
        controller.init();
    }
}