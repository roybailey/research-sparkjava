package me.roybailey.sparkjava.domain;


import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.impl.DefaultMapperFactory;

/**
 * Mappers for Ticket objects using http://orika-mapper.github.io/
 */
public class TicketMapper {

    public static DefaultMapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

    static {
        mapperFactory.classMap(Ticket.class, Ticket.class)
                .byDefault()
                .register();
    }

    public static MapperFacade MAPPER = mapperFactory.getMapperFacade();

    public static Ticket copyTicket(Ticket source) {
        return MAPPER.map(source, Ticket.class);
    }
}
