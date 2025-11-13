package com.intern.backendettaba.designpattern.revenuestrategy;
import com.intern.backendettaba.entities.Event;
import com.intern.backendettaba.interfaces.RevenueStrategy;

public class EventRevenue implements RevenueStrategy {

    private final Event event;
    public EventRevenue(Event event) {
        this.event = event;
    }
    @Override
    public float calculerRevenu() {
        System.out.println("*************numbre de ticket totale**************"+event.getNumberTickets());
        System.out.println("*************numbre de ticket totale**************"+event.getNumberTickets());

        System.out.println("*************numbre de ticket avalable**************"+event.getNumberAvailableTickets());
        System.out.println("Revenue pour ettaba :::::::::::::!!!!!!"+(float)(event.getNumberTickets()-event.getNumberAvailableTickets())*event.getPrice());
        return (float) (event.getNumberTickets()-event.getNumberAvailableTickets())*event.getPrice();


    }
}
