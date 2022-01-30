package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.GuestDTO;
import com.tenniscourts.guests.GuestMapper;
import com.tenniscourts.guests.GuestRepository;
import com.tenniscourts.guests.GuestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = GuestService.class)
public class GuestServiceTest {

    public static final long GUEST_ID = 1L;
    public static final long ID = 20L;
    @InjectMocks
    GuestService guestService;
    @Mock
    GuestRepository guestRepository;
    @Mock
    GuestMapper guestMapper;

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowException() {
        GuestDTO guestDTO = GuestDTO
                .builder()
                .id(GUEST_ID)
                .build();
        when(guestRepository.findById(GUEST_ID)).thenReturn(Optional.empty());
        guestService.updateGuest(guestDTO);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionWhenNotFound() {

        when(guestRepository.findById(ID)).thenReturn(Optional.empty());
        guestService.deleteGuest(ID);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionWhenEntityNotFound() {

        Mockito.lenient().when(guestRepository.findById(ID)).thenReturn(Optional.empty());
        guestService.findGuestById(ID);
    }
}
