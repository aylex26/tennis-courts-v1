package com.tenniscourts.guests;

import com.tenniscourts.exceptions.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Collections.EMPTY_LIST;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
public class GuestService {

    public static final String GUEST_NOT_FOUND = "Guest not found.";
    private final GuestRepository guestRepository;

    private final GuestMapper guestMapper;

    public GuestDTO addGuest(GuestDTO guestDTO) {
        guestDTO.setId(null);
        return guestMapper.map(guestRepository.save(guestMapper.map(guestDTO)));
    }

    public void updateGuest(GuestDTO guestDTO) {
        guestRepository.findById(guestDTO.getId()).orElseThrow(() -> {
            throw new EntityNotFoundException(GUEST_NOT_FOUND);
        });
        guestRepository.save(guestMapper.map(guestDTO));
    }

    public void deleteGuest(Long guestId) {
        guestRepository.findById(guestId).orElseThrow(() -> {
            throw new EntityNotFoundException(GUEST_NOT_FOUND);
        });
        guestRepository.deleteById(guestId);
    }

    public List<GuestDTO> findAllGuests() {
        return guestRepository.findAll().stream().map(guestMapper::map).collect(toList());
    }

    public List<GuestDTO> findAllGuestsByName(String name) {
        return guestRepository.findAllByName(name).map(guestMapper::map).orElse(EMPTY_LIST);
    }

    public GuestDTO findGuestById(Long id) {
        return guestRepository.findById(id).map(guestMapper::map).orElseThrow(() -> {
            throw new EntityNotFoundException(GUEST_NOT_FOUND);
        });
    }
}
