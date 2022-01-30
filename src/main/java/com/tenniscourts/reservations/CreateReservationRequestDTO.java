package com.tenniscourts.reservations;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Data
public class CreateReservationRequestDTO {

    @NotNull
    private Long guestId;

    @NotNull
    private List<Long> scheduleIds;
}
