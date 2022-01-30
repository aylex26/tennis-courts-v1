package com.tenniscourts.tenniscourts;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.schedules.ScheduleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TennisCourtService {

    public static final String COURT_NOT_FOUND = "Court not found.";
    private final TennisCourtRepository tennisCourtRepository;
    private final ScheduleService scheduleService;
    private final TennisCourtMapper tennisCourtMapper;

    public TennisCourtDTO addTennisCourt(TennisCourtDTO court) {
        return tennisCourtMapper.map(tennisCourtRepository.save(tennisCourtMapper.map(court)));
    }

    public TennisCourtDTO findTennisCourtById(Long id, boolean withSchedule) {
        TennisCourtDTO tennisCourtDTO = findTennisCourtById(id);
        if (withSchedule) {
            tennisCourtDTO.setTennisCourtSchedules(scheduleService.findSchedulesByTennisCourtId(id));
        }
        return tennisCourtDTO;
    }

    private TennisCourtDTO findTennisCourtById(Long id) {
        return tennisCourtRepository.findById(id).map(tennisCourtMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException(COURT_NOT_FOUND);
        });
    }
}
