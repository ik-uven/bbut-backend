package org.ikuven.bbut.tracking.participant;

import org.ikuven.bbut.tracking.repository.ParticipantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceTest {

    private static final LocalDateTime FINISH_TIME = LocalDateTime.parse("2020-08-08T10:47:00");
    public static final long ID_10 = 10L;
    public static final long ID_23 = 23L;
    public static final long ID_40 = 40L;
    public static final long ID_50 = 50L;

    @Mock
    private ParticipantRepository repository;

    @InjectMocks
    private ParticipantService participantService;

    @Test
    @DisplayName("should return correctly filtered participants having state ACTIVE or RESIGNED")
    void getAllQualifiedParticipants() {

        List<Participant> participants = List.of(
                createParticipant(1, ParticipantState.ACTIVE),
                createParticipant(2, ParticipantState.NO_SHOW),
                createParticipant(3, ParticipantState.REGISTERED),
                createParticipant(4, ParticipantState.RESIGNED)
                );

        when(repository.findAll(any(Sort.class))).thenReturn(participants);

        List<Participant> qualifiedParticipants = participantService.getActivatedParticipants();

        assertThat(qualifiedParticipants)
                .isNotNull()
                .extracting(Participant::getId)
                .containsExactly(1L, 4L);
    }

    private Participant createParticipant(long id, ParticipantState state) {
        return Participant.of(id, null, null, null, null, null, null, state);
    }

    /*    @Test
    @DisplayName("should correctly sort when all have completed")
    void getAllParticipants1() {
        List<Lap> laps = createLaps(COMPLETED, COMPLETED, COMPLETED);

        Participant participant10 = createParticipant(laps, ID_10, "Abe", "Ason");
        Participant participant23 = createParticipant(laps, ID_23, "Beorn", "Brown");
        Participant participant40 = createParticipant(laps, ID_40, "Cessy", "Clib");
        Participant participant50 = createParticipant(laps, ID_50, "David", "Dobbs");

        List<Participant> participants = sort(List.of(participant10, participant23, participant40, participant50));
        when(repository.findAll(any(Sort.class))).thenReturn(participants);

        val allParticipants = participantService.getAllParticipants();

        assertThat(allParticipants)
                .extracting(Participant::getId)
                .containsExactly(ID_10, ID_23, ID_40, ID_50);
    }

    @Test
    @DisplayName("should correctly sort when Abe overdue")
    void getAllParticipants2() {
        List<Lap> laps = createLaps(COMPLETED, COMPLETED, COMPLETED);
        List<Lap> lapsLastOverdue = createLaps(COMPLETED, COMPLETED, OVERDUE);

        Participant participant10 = createParticipant(lapsLastOverdue, ID_10, "Abe", "Ason");
        Participant participant23 = createParticipant(laps, ID_23, "Beorn", "Brown");
        Participant participant40 = createParticipant(laps, ID_40, "Cessy", "Clib");
        Participant participant50 = createParticipant(laps, ID_50, "David", "Dobbs");

        List<Participant> participants = sort(List.of(participant10, participant23, participant40, participant50));
        when(repository.findAll(any(Sort.class))).thenReturn(participants);

        val allParticipants = participantService.getAllParticipants();

        assertThat(allParticipants)
                .extracting(Participant::getId)
                .containsExactly(ID_23, ID_40, ID_50, ID_10);
    }

    @Test
    @DisplayName("should correctly sort when all have completed and Abe resigned")
    void getAllParticipants3() {
        List<Lap> laps = createLaps(COMPLETED, COMPLETED, COMPLETED);

        Participant participant10 = createResignedParticipant(laps, ID_10, "Abe", "Ason");
        Participant participant23 = createParticipant(laps, ID_23, "Beorn", "Brown");
        Participant participant40 = createParticipant(laps, ID_40, "Cessy", "Clib");
        Participant participant50 = createParticipant(laps, ID_50, "David", "Dobbs");

        List<Participant> participants = sort(List.of(participant10, participant23, participant40, participant50));
        when(repository.findAll(any(Sort.class))).thenReturn(participants);

        val allParticipants = participantService.getAllParticipants();

        assertThat(allParticipants)
                .extracting(Participant::getId)
                .containsExactly(ID_23, ID_40, ID_50, ID_10);
    }

    @Test
    @DisplayName("should correctly sort when all have completed and Abe resigned")
    void getAllParticipants4() {
        List<Lap> laps3 = createLaps(COMPLETED, COMPLETED, COMPLETED);
        List<Lap> laps2 = createLaps(COMPLETED, COMPLETED);

        Participant participant10 = createParticipant(laps2, ID_10, "Abe", "Ason");
        Participant participant23 = createParticipant(laps2, ID_23, "Beorn", "Brown");
        Participant participant40 = createParticipant(laps3, ID_40, "Cessy", "Clib");
        Participant participant50 = createParticipant(laps3, ID_50, "David", "Dobbs");

        List<Participant> participants = sort(List.of(participant10, participant23, participant40, participant50));
        when(repository.findAll(any(Sort.class))).thenReturn(participants);

        val allParticipants = participantService.getAllParticipants();

        assertThat(allParticipants)
                .extracting(Participant::getId)
                .containsExactly(ID_40, ID_50, ID_10, ID_23);
    }

    @Test
    @DisplayName("should correctly sort when two person have same number of laps but one is overdue")
    void getAllParticipants5() {
        List<Lap> laps3 = createLaps(COMPLETED, COMPLETED, COMPLETED);
        List<Lap> laps2 = createLaps(COMPLETED, COMPLETED);
        List<Lap> laps2x = createLaps(COMPLETED, OVERDUE);

        Participant participant10 = createParticipant(laps2, ID_10, "Abe", "Ason");
        Participant participant23 = createParticipant(laps3, ID_23, "Beorn", "Brown");
        Participant participant40 = createParticipant(laps2x, ID_40, "Cessy", "Clib");
        Participant participant50 = createParticipant(laps2, ID_50, "David", "Dobbs");

        List<Participant> participants = sort(List.of(participant10, participant23, participant40, participant50));
        when(repository.findAll(any(Sort.class))).thenReturn(participants);

        val allParticipants = participantService.getAllParticipants();

        allParticipants
                .forEach(System.out::println);

        assertThat(allParticipants)
                .extracting(Participant::getId, Participant::getParticipantState)
                .containsExactly(
                        tuple(ID_23, ParticipantState.ACTIVE),
                        tuple(ID_10, ParticipantState.ACTIVE),
                        tuple(ID_50, ParticipantState.ACTIVE),
                        tuple(ID_40, ParticipantState.RESIGNED)
                );
    }

    @Test
    @DisplayName("should correctly sort when xxxxxxxxxxxxxxxxx")
    void getAllParticipants6() {
        List<Lap> laps1 = createLaps(COMPLETED);
        List<Lap> laps2 = createLaps(COMPLETED, COMPLETED);
        List<Lap> laps2x = createLaps(COMPLETED, OVERDUE);

        Participant participant10 = createParticipant(laps2, 1L, "Tom", "Eldow", ParticipantState.ACTIVE);
        Participant participant50 = createParticipant(laps1, 30L, "Filip", "Niessen", ParticipantState.RESIGNED);
        Participant participant40 = createParticipant(laps2x, 5L, "Caleb", "Johnson", ParticipantState.RESIGNED);
        Participant participant23 = createParticipant(Collections.emptyList(), 6L, "Fred", "Soneborn", ParticipantState.RESIGNED);

        List<Participant> participants = sort(List.of(participant10, participant23, participant50, participant40));
        when(repository.findAll(any(Sort.class))).thenReturn(participants);

        val allParticipants = participantService.getAllParticipants();

        allParticipants
                .forEach(System.out::println);

        assertThat(allParticipants)
                .extracting(Participant::getId, Participant::getParticipantState)
                .containsExactly(
                        tuple(1L, ParticipantState.ACTIVE),
                        tuple(5L, ParticipantState.RESIGNED),
                        tuple(30L, ParticipantState.RESIGNED),
                        tuple(6L, ParticipantState.RESIGNED)
                );
    }

    private List<Participant> sort(List<Participant> participants) {
        return participants.stream()
                .sorted(Comparator.comparing(Participant::getParticipantState).thenComparing(Participant::getFirstName))
                .collect(Collectors.toList());
    }

    private Participant createResignedParticipant(List<Lap> laps, long id, String firstName, String lastName) {
        return Participant.of(id, firstName, lastName, "", "", Gender.FEMALE, 1974, ParticipantState.RESIGNED, laps);
    }

    private Participant createParticipant(List<Lap> laps, long id, String firstName, String lastName) {
        boolean hasOverdueLap = laps.stream()
                .anyMatch(lap -> lap.state.equals(LapState.OVERDUE));

        ParticipantState active = !hasOverdueLap ? ParticipantState.ACTIVE: ParticipantState.RESIGNED;

        return Participant.of(id, firstName, lastName, "", "", Gender.MALE, 1981, active, laps);
    }

    private Participant createParticipant(List<Lap> laps, long id, String firstName, String lastName, ParticipantState participantState) {

        return Participant.of(id, firstName, lastName, "", "", Gender.MALE, 1981, participantState, laps);
    }

    private List<Lap> createLaps(LapState... lapStates) {

        return IntStream.range(0, lapStates.length)
                .mapToObj(i -> Lap.of(i + 1, i + 1 == 1 ? FINISH_TIME : FINISH_TIME.plusHours(1), lapStates[i]))
                .collect(Collectors.toList());
    }*/
}
