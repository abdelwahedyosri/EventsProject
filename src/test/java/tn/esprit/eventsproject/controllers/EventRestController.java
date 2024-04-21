package tn.esprit.eventsproject.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.services.IEventServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
class EventRestControllerTest {

    @Mock
    private IEventServices eventServices;

    @InjectMocks
    private EventRestController eventRestController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(eventRestController).build();
    }

    @Test
    void testAddParticipant() throws Exception {
        Participant participant = new Participant();
        participant.setIdPart(1);
        participant.setNom("John");
        participant.setPrenom("Doe");
        participant.setTache(Tache.ORGANISATEUR); // Corrected the task to ORGANISATEUR
        participant.setEvents(new HashSet<>());

        when(eventServices.addParticipant(any(Participant.class))).thenReturn(participant);

        mockMvc.perform(post("/event/addPart")
                        .content("{ \"idPart\": 1, \"nom\": \"John\", \"prenom\": \"Doe\", \"tache\": \"ORGANISATEUR\", \"events\": [] }") // Corrected the task to ORGANISATEUR
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPart").value(participant.getIdPart()))
                .andExpect(jsonPath("$.nom").value(participant.getNom()))
                .andExpect(jsonPath("$.prenom").value(participant.getPrenom()))
                .andExpect(jsonPath("$.tache").value(participant.getTache().name()));
    }

    @Test
    void testAddEventPart() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        Event event = new Event();
        event.setIdEvent(1);
        event.setDescription("Event 1");
        event.setDateDebut(LocalDate.parse("2024-04-21")); // Corrected the dateDebut
        event.setDateFin(LocalDate.parse("2024-04-22")); // Corrected the dateFin
        event.setCout(100);
        event.setParticipants(new HashSet<>());
        event.setLogistics(new HashSet<>());

        Participant participant = new Participant();
        participant.setIdPart(1);
        participant.setNom("John");
        participant.setPrenom("Doe");
        participant.setTache(Tache.ORGANISATEUR);
        participant.setEvents(new HashSet<>());

        when(eventServices.addAffectEvenParticipant(any(Event.class), anyInt())).thenReturn(event);

        mockMvc.perform(post("/event/addEvent/1")
                        .content(objectMapper.writeValueAsString(event)) // Serialize event to JSON
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEvent").value(event.getIdEvent()))
                .andExpect(jsonPath("$.description").value(event.getDescription()))
                .andExpect(jsonPath("$.dateDebut[0]").value(2024)) // Assert the year of dateDebut
                .andExpect(jsonPath("$.dateDebut[1]").value(4))    // Assert the month of dateDebut
                .andExpect(jsonPath("$.dateDebut[2]").value(21))   // Assert the day of dateDebut
                .andExpect(jsonPath("$.dateFin[0]").value(2024))   // Assert the year of dateFin
                .andExpect(jsonPath("$.dateFin[1]").value(4))      // Assert the month of dateFin
                .andExpect(jsonPath("$.dateFin[2]").value(22))     // Assert the day of dateFin
                .andExpect(jsonPath("$.cout").value(event.getCout()));
    }

    @Test
    void testAddEvent() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()); // Moved objectMapper declaration outside the method
        Event event = new Event();
        event.setIdEvent(1);
        event.setDescription("Event 1");
        event.setDateDebut(LocalDate.parse("2024-04-21")); // Corrected the dateDebut
        event.setDateFin(LocalDate.parse("2024-04-22")); // Corrected the dateFin
        event.setCout(100);
        event.setParticipants(new HashSet<>());
        event.setLogistics(new HashSet<>());

        when(eventServices.addAffectEvenParticipant(any(Event.class))).thenReturn(event);

        mockMvc.perform(post("/event/addEvent")
                        .content(objectMapper.writeValueAsString(event)) // Serialize event to JSON
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEvent").value(event.getIdEvent()))
                .andExpect(jsonPath("$.description").value(event.getDescription()))
                .andExpect(jsonPath("$.dateDebut[0]").value(2024)) // Assert the year of dateDebut
                .andExpect(jsonPath("$.dateDebut[1]").value(4))    // Assert the month of dateDebut
                .andExpect(jsonPath("$.dateDebut[2]").value(21))   // Assert the day of dateDebut
                .andExpect(jsonPath("$.dateFin[0]").value(2024))   // Assert the year of dateFin
                .andExpect(jsonPath("$.dateFin[1]").value(4))      // Assert the month of dateFin
                .andExpect(jsonPath("$.dateFin[2]").value(22))     // Assert the day of dateFin
                .andExpect(jsonPath("$.cout").value(event.getCout()));
    }
    @Test
    void testAddAffectLog() throws Exception {
        Logistics logistics = new Logistics();
        logistics.setIdLog(1);
        logistics.setDescription("Logistics for Event 1");
        logistics.setReserve(true);
        logistics.setPrixUnit(50);
        logistics.setQuantite(10);

        when(eventServices.addAffectLog(any(Logistics.class), anyString())).thenReturn(logistics);

        mockMvc.perform(put("/event/addAffectLog/Event 1")
                        .content("{ \"idLog\": 1, \"description\": \"Logistics for Event 1\", \"reserve\": true, \"prixUnit\": 50, \"quantite\": 10 }")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idLog").value(logistics.getIdLog()))
                .andExpect(jsonPath("$.description").value(logistics.getDescription()))
                .andExpect(jsonPath("$.reserve").value(logistics.isReserve()))
                .andExpect(jsonPath("$.prixUnit").value(logistics.getPrixUnit()))
                .andExpect(jsonPath("$.quantite").value(logistics.getQuantite()));
    }

   /* @Test
    void testGetLogistiquesDates() throws Exception {
        LocalDate d1 = LocalDate.now().minusDays(2);
        LocalDate d2 = LocalDate.now();

        Logistics logistics1 = new Logistics();
        logistics1.setIdLog(1);
        logistics1.setDescription("Logistics for Event 1");
        logistics1.setReserve(true);
        logistics1.setPrixUnit(50);
        logistics1.setQuantite(10);

        Logistics logistics2 = new Logistics();
        logistics2.setIdLog(2);
        logistics2.setDescription("Logistics for Event 2");
        logistics2.setReserve(false);
        logistics2.setPrixUnit(30);
        logistics2.setQuantite(5);

        List<Logistics> logisticsList = new ArrayList<>();
        logisticsList.add(logistics1);
        logisticsList.add(logistics2);

        when(eventServices.getLogisticsDates(any(LocalDate.class), any(LocalDate.class))).thenReturn(logisticsList);

        mockMvc.perform(get("/event/getLogs/{d1}/{d2}",
                        d1.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        d2.format(DateTimeFormatter.ISO_LOCAL_DATE))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idLog").value(logistics1.getIdLog()))
                .andExpect(jsonPath("$[0].description").value(logistics1.getDescription()))
                .andExpect(jsonPath("$[0].reserve").value(logistics1.isReserve()))
                .andExpect(jsonPath("$[0].prixUnit").value(logistics1.getPrixUnit()))
                .andExpect(jsonPath("$[0].quantite").value(logistics1.getQuantite()))
                .andExpect(jsonPath("$[1].idLog").value(logistics2.getIdLog()))
                .andExpect(jsonPath("$[1].description").value(logistics2.getDescription()))
                .andExpect(jsonPath("$[1].reserve").value(logistics2.isReserve()))
                .andExpect(jsonPath("$[1].prixUnit").value(logistics2.getPrixUnit()))
                .andExpect(jsonPath("$[1].quantite").value(logistics2.getQuantite()));
    }*/
}
