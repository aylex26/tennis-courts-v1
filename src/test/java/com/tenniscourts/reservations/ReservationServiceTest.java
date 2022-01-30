package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.GuestRepository;
import com.tenniscourts.schedules.ScheduleRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = ReservationService.class)
public class ReservationServiceTest {
    public static final long ID = 1L;
    @InjectMocks
    ReservationService reservationService;
    @Mock
    ReservationRepository reservationRepository;
    @Mock
    GuestRepository guestRepository;
    @Mock
    ScheduleRepository scheduleRepository;
    @Mock
    ReservationMapper reservationMapper;

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowException() {
        CreateReservationRequestDTO createReservationRequestDTO = CreateReservationRequestDTO.builder().guestId(ID).build();
        when(guestRepository.findById(ID)).thenReturn(Optional.empty());
        reservationService.bookReservation(createReservationRequestDTO);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionWhenReservationNotFound() {
        when(reservationRepository.findById(ID)).thenReturn(Optional.empty());
        reservationService.findReservation(ID);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionWhenCancelReservationNotFound() {
        when(reservationRepository.findById(ID)).thenReturn(Optional.empty());
        reservationService.cancelReservation(ID);
    }
}
