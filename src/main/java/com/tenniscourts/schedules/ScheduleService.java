package com.tenniscourts.schedules;

import com.tenniscourts.exceptions.AlreadyExistsEntityException;
import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.tenniscourts.TennisCourt;
import com.tenniscourts.tenniscourts.TennisCourtRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ScheduleService {

    public static final String COURT_NOT_FOUND = "Court not found.";
    public static final String ALREADY_EXISTS = "Already exists.";
    public static final String SCHEDULE_NOT_FOUND = "Schedule not found";
    private final ScheduleRepository scheduleRepository;
    private final TennisCourtRepository tennisCourtRepository;
    private final ScheduleMapper scheduleMapper;


    public ScheduleDTO addSchedule(CreateScheduleRequestDTO scheduleDTO) {
        TennisCourt tennisCourt = tennisCourtRepository.findById(scheduleDTO.getTennisCourtId()).orElseThrow(() -> {
            throw new EntityNotFoundException(COURT_NOT_FOUND);
        });
        LocalDateTime standardizedStartDateTime = scheduleDTO.getStartDateTime().withMinute(0);
        long duplicates = scheduleRepository.countAllByStartDateTimeAndTennisCourt(standardizedStartDateTime, tennisCourt);
        if (duplicates > 0) {
            throw new AlreadyExistsEntityException(ALREADY_EXISTS);
        }

        return scheduleMapper.map(scheduleRepository.saveAndFlush(
                Schedule.builder()
                        .startDateTime(standardizedStartDateTime)
                        .tennisCourt(tennisCourt)
                        .endDateTime(standardizedStartDateTime.plusHours(1))
                        .build()));
    }

    public List<ScheduleDTO> findSchedulesByDates(LocalDateTime startDate, LocalDateTime endDate) {
        return scheduleMapper.map(scheduleRepository.findAllByStartDateTimeAfterAndEndDateTimeBefore(startDate, endDate));
    }

    public ScheduleDTO findScheduleById(Long id) {
        return scheduleRepository.findById(id).map(scheduleMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException(SCHEDULE_NOT_FOUND);
        });
    }

    public List<ScheduleDTO> findSchedulesByTennisCourtId(Long id) {
        return scheduleMapper.map(scheduleRepository.findByTennisCourt_IdOrderByStartDateTime(id));
    }
}
