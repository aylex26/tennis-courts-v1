package com.tenniscourts.reservations;

import com.tenniscourts.exceptions.EntityNotFoundException;
import com.tenniscourts.guests.Guest;
import com.tenniscourts.guests.GuestRepository;
import com.tenniscourts.schedules.Schedule;
import com.tenniscourts.schedules.ScheduleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ReservationService {

    public static final String GUEST_NOT_FOUND = "Guest not found.";
    public static final String INVALID_SCHEDULE = "Invalid schedule.";
    public static final String RESERVATION_NOT_FOUND = "Reservation not found.";
    public static final String CANNOT_RESCHEDULE_TO_THE_SAME_SLOT = "Cannot reschedule to the same slot.";
    private final GuestRepository guestRepository;
    private final ScheduleRepository scheduleRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;

    public List<ReservationDTO> bookReservation(CreateReservationRequestDTO createReservationRequestDTO) {
        Guest bookingGuest = guestRepository.findById(createReservationRequestDTO.getGuestId()).orElseThrow(() -> {
            throw new EntityNotFoundException(GUEST_NOT_FOUND);
        });

        List<Schedule> bookingSchedules = scheduleRepository.findAllById(createReservationRequestDTO.getScheduleIds());
        if (bookingSchedules.isEmpty() || bookingSchedules.size() != createReservationRequestDTO.getScheduleIds().size()) {
            throw new IllegalArgumentException(INVALID_SCHEDULE);
        }

        List<Reservation> bookings = new ArrayList<>();
        bookingSchedules.forEach(s -> {
            Reservation reservation = new Reservation();
            reservation.setReservationStatus(ReservationStatus.READY_TO_PLAY);
            reservation.setGuest(bookingGuest);
            reservation.setSchedule(s);
            reservation.setValue(BigDecimal.TEN);
            bookings.add(reservation);
        });
        return reservationMapper.map(reservationRepository.saveAll(bookings));
    }

    public ReservationDTO findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).map(reservationMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException(RESERVATION_NOT_FOUND);
        });
    }

    public ReservationDTO cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> {
            throw new EntityNotFoundException(RESERVATION_NOT_FOUND);
        });
        return reservationMapper.map(cancel(reservation));
    }

    private Reservation cancel(Reservation reservation) {
        validateCancellation(reservation);
        BigDecimal refundValue = getRefundValue(reservation);
        return updateReservation(reservation, refundValue, ReservationStatus.CANCELLED);
    }

    private Reservation updateReservation(Reservation reservation, BigDecimal refundValue, ReservationStatus status) {
        reservation.setReservationStatus(status);
        reservation.setValue(reservation.getValue().subtract(refundValue));
        reservation.setRefundValue(refundValue);

        return reservationRepository.save(reservation);
    }

    private void validateCancellation(Reservation reservation) {
        if (!ReservationStatus.READY_TO_PLAY.equals(reservation.getReservationStatus())) {
            throw new IllegalArgumentException("Cannot cancel/reschedule because it's not in ready to play status.");
        }

        if (reservation.getSchedule().getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Can cancel/reschedule only future dates.");
        }
    }

    public BigDecimal getRefundValue(Reservation reservation) {
        long passingHours = ChronoUnit.HOURS.between(LocalDateTime.now(), reservation.getSchedule().getStartDateTime());

        if (passingHours >= 24) {
            return reservation.getValue();
        } else if (passingHours >= 12) {
            return reservation.getValue().divide(BigDecimal.valueOf(4), 2, RoundingMode.CEILING).multiply(BigDecimal.valueOf(3));
        } else if (passingHours >= 2) {
            return reservation.getValue().divide(BigDecimal.valueOf(2), 2, RoundingMode.CEILING);
        } else {
            return reservation.getValue().divide(BigDecimal.valueOf(4), 2, RoundingMode.CEILING);
        }
    }

    public ReservationDTO rescheduleReservation(Long previousReservationId, Long scheduleId) {
        Reservation previousReservation = reservationRepository.findById(previousReservationId).orElseThrow(() -> {
            throw new EntityNotFoundException(RESERVATION_NOT_FOUND);
        });

        if (scheduleId.equals(previousReservation.getSchedule().getId())) {
            throw new IllegalArgumentException(CANNOT_RESCHEDULE_TO_THE_SAME_SLOT);
        }
        previousReservation = cancel(previousReservation);

        previousReservation.setReservationStatus(ReservationStatus.RESCHEDULED);
        reservationRepository.save(previousReservation);

        CreateReservationRequestDTO newReservationRequestDTO = new CreateReservationRequestDTO();
        List<Long> scheduleIds = new ArrayList<>();
        scheduleIds.add(scheduleId);
        newReservationRequestDTO.setGuestId(previousReservation
                .getGuest()
                .getId());
        newReservationRequestDTO.setScheduleIds(scheduleIds);
        ReservationDTO newReservation = bookReservation(newReservationRequestDTO).get(0);

        newReservation.setPreviousReservation(reservationMapper.map(previousReservation));
        return newReservation;
    }
}
