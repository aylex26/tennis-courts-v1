package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.schedules.CreateScheduleRequestDTO;
import com.tenniscourts.schedules.ScheduleMapper;
import com.tenniscourts.schedules.ScheduleRepository;
import com.tenniscourts.schedules.ScheduleService;
import com.tenniscourts.tenniscourts.TennisCourtRepository;
import org.junit.Before;
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

import java.time.LocalDateTime;
import java.util.Optional;

@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = ScheduleService.class)
public class ScheduleServiceTest {

    public static final long TENNIS_COURT_ID = 1L;
    public static final long ID = 20L;
    @InjectMocks
    ScheduleService scheduleService;
    @Mock
    ScheduleRepository scheduleRepository;
    @Mock
    TennisCourtRepository tennisCourtRepository;
    @Mock
    ScheduleMapper scheduleMapper;

    private CreateScheduleRequestDTO createScheduleRequestDTO;

    @Before
    public void init() {
        createScheduleRequestDTO = CreateScheduleRequestDTO
                .builder()
                .tennisCourtId(TENNIS_COURT_ID)
                .startDateTime(LocalDateTime.now())
                .build();
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionWhenCourtNotFound() {
        Mockito.lenient().when(tennisCourtRepository.findById(ID)).thenReturn(Optional.empty());
        scheduleService.addSchedule(createScheduleRequestDTO);
    }

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionWhenScheduleNotFound() {
        Mockito.lenient().when(scheduleRepository.findById(ID)).thenReturn(Optional.empty());
        scheduleService.findScheduleById(ID);
    }

}
