package com.intern.backendettaba.services;

import com.intern.backendettaba.designpattern.revenuestrategy.EttabaRevenue;
import com.intern.backendettaba.designpattern.revenuestrategy.EventRevenue;
import com.intern.backendettaba.designpattern.revenuestrategy.RevenueContext;
import com.intern.backendettaba.entities.Ettaba;
import com.intern.backendettaba.entities.Event;
import com.intern.backendettaba.interfaces.RevenueStrategy;
import com.intern.backendettaba.repositories.EventRepository;
import com.intern.backendettaba.repositories.FarmRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventService {

    private final FarmRepository farmRepository;
    private final EventRepository eventRepository;



    public ResponseEntity<Event> saveEvent(Event event){
        //to be modified according to user action
        event.setBoughtDate(LocalDate.now());
        //
        event.setCreationDate(LocalDate.now());
        return new ResponseEntity<>(eventRepository.save(event), HttpStatus.CREATED);
    }

    public ResponseEntity<Event> getEventByID(Long id){
        Event event=eventRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        event.setNumberSoldTickets(event.getNumberTickets()- event.getNumberAvailableTickets());

        return new ResponseEntity<>(event, HttpStatus.OK);
    }

    public ResponseEntity<List<Event>> getAllEvents(){
        return ResponseEntity.ok(eventRepository.findAll());
    }


    public ResponseEntity<Float> getEventRevenues(){
        float total=0f;
        List<Event> event = eventRepository.findAll();
        for (Event event1 : event) {

            RevenueStrategy strategy = new EventRevenue(event1);
            RevenueContext context = new RevenueContext(strategy);
            float revenu = context.calculer();
            total+=revenu;
        }
         System.out.println("*************total*****************"+total);

        return new ResponseEntity<>(total,HttpStatus.OK);

    }

    public ResponseEntity<Event> updateEvent(Event newEvent,Long id){
        Event dbEvent=eventRepository.findById(id).orElseThrow(()-> new EntityNotFoundException(String.valueOf(id)));
        if(Objects.nonNull(newEvent.getName()) && !Objects.equals(newEvent.getName(),dbEvent.getName())){
            dbEvent.setName(newEvent.getName());
        }
        if(Objects.nonNull(newEvent.getDescription()) && !Objects.equals(newEvent.getDescription(),dbEvent.getDescription())){
            dbEvent.setDescription(newEvent.getDescription());
        }
        if(Objects.nonNull(newEvent.getPrice()) && !Objects.equals(newEvent.getPrice(),dbEvent.getPrice())){
            dbEvent.setPrice(newEvent.getPrice());
        }
        if(Objects.nonNull(newEvent.getStartDate()) && !Objects.equals(newEvent.getStartDate(),dbEvent.getStartDate())){
            dbEvent.setStartDate(newEvent.getStartDate());
        }
        if(Objects.nonNull(newEvent.getEndDate()) && !Objects.equals(newEvent.getEndDate(),dbEvent.getEndDate())){
            dbEvent.setEndDate(newEvent.getEndDate());
        }
        if(Objects.nonNull(newEvent.getNumberTickets()) && !Objects.equals(newEvent.getNumberTickets(),dbEvent.getNumberTickets())){
            dbEvent.setNumberTickets(newEvent.getNumberTickets());
        }
        if(Objects.nonNull(newEvent.getNumberAvailableTickets()) && !Objects.equals(newEvent.getNumberAvailableTickets(),dbEvent.getNumberAvailableTickets())){
            dbEvent.setNumberAvailableTickets(newEvent.getNumberAvailableTickets());
        }
        return new ResponseEntity<>(eventRepository.save(dbEvent),HttpStatus.OK);
    }

    public ResponseEntity<Event> deleteEvent(Long id){
        Optional<Event> event=eventRepository.findById(id);
        if(event.isPresent()) {
            eventRepository.deleteById(id);
            ResponseEntity.ok(event);
        }
        return ResponseEntity.notFound().build();
    }

   /* public ResponseEntity<Event> addEventToFarmById(Long id, Event newEvent) {
        Event event=farmRepository.findById(id).map(farm -> {
            newEvent.setFarm(farm);
            return eventRepository.save(newEvent);
        }).orElseThrow(()-> new ResourceNotFoundException("Not found farm id = "+id));

        return new ResponseEntity<>(event,HttpStatus.CREATED);
    }*/

    public ResponseEntity<Event> addEventToFarmById(Long id, Event eventRequest) {
        Event event = farmRepository.findById(id).map(farm -> {
            // Utilisation du pattern Creator avec les images
            List<Event> ListeventsByfarm = eventRepository.findByFarmId(farm.getId());
            System.out.println("listaaaaaaaaaaaaaaaaaaaa"+ ListeventsByfarm.size());


            Event newEvent = farm.createEvent(
                    eventRequest.getName(),
                    eventRequest.getPrice(),
                    eventRequest.getDescription(),
                    eventRequest.getStartDate(),
                    eventRequest.getEndDate(),
                    eventRequest.getNumberTickets(),
                    eventRequest.getImages() ,
                    ListeventsByfarm // Passage des images
            );
            newEvent.setFarm(farm);
            return eventRepository.save(newEvent);
        }).orElseThrow(() -> new ResourceNotFoundException("Not found farm id = " + id));

        return new ResponseEntity<>(event, HttpStatus.CREATED);
    }

    public ResponseEntity<List<Event>> getAllEventsByFarmId(Long farmId){
        if(!farmRepository.existsById(farmId)){
            throw new ResourceNotFoundException("Not found farm id : "+farmId);
        }

        return new ResponseEntity<>(eventRepository.findByFarmId(farmId), HttpStatus.OK);
    }

    public ResponseEntity<List<Event>> deleteAllEventsFromFarmById(Long id){
        if(!farmRepository.existsById(id)){
            throw new ResourceNotFoundException("Not found");
        }

        eventRepository.deleteByFarmId(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
