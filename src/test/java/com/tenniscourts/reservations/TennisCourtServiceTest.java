package com.tenniscourts.reservations;


import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.schedules.ScheduleService;
import com.tenniscourts.tenniscourts.TennisCourtMapper;
import com.tenniscourts.tenniscourts.TennisCourtRepository;
import com.tenniscourts.tenniscourts.TennisCourtService;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.mockito.Mockito.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = TennisCourtService.class)
public class TennisCourtServiceTest {

    public static final long ID = 10L;
    @InjectMocks
    TennisCourtService tennisCourtService;
    @Mock
    TennisCourtRepository tennisCourtRepository;
    @Mock
    ScheduleService scheduleService;
    @Mock
    TennisCourtMapper tennisCourtMapper;

    @Test(expected = EntityNotFoundException.class)
    public void shouldThrowExceptionWhenCourtNotFound() {
        when(tennisCourtRepository.findById(ID)).thenReturn(Optional.empty());
        tennisCourtService.findTennisCourtById(ID, false);
    }

}
