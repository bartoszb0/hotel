package com.bd.hotelapp.guest.service.impl;

import com.bd.hotelapp.guest.domain.entity.Guest;
import com.bd.hotelapp.guest.repository.GuestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GuestServiceImplTest {

    private static final String FIRST_NAME = "Jan";
    private static final String LAST_NAME = "Kowalski";
    private static final String EMAIL = "jan@example.com";

    @Mock
    private GuestRepository guestRepository;

    private GuestServiceImpl guestService;

    @BeforeEach
    void setUp() {
        guestService = new GuestServiceImpl(guestRepository);
    }

    // findOrCreate – istniejący gość

    @Test
    void shouldReturnExistingGuestWithoutSaving() {
        Guest existing = guest();
        when(guestRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existing));

        Guest result = guestService.findOrCreate(FIRST_NAME, LAST_NAME, EMAIL);

        assertThat(result).isSameAs(existing);
        verify(guestRepository, never()).save(any());
    }

    @Test
    void shouldFindExistingGuestByNormalizedEmail() {
        Guest existing = guest();
        when(guestRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existing));

        Guest result = guestService.findOrCreate(FIRST_NAME, LAST_NAME, "  JAN@Example.COM ");

        assertThat(result).isSameAs(existing);
        verify(guestRepository, never()).save(any());
    }

    @Test
    void shouldNotOverwriteExistingGuestDataWithNewNames() {
        Guest existing = guest();
        when(guestRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existing));

        Guest result = guestService.findOrCreate("Janusz", "Nowak", EMAIL);

        assertThat(result.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(result.getLastName()).isEqualTo(LAST_NAME);
        verify(guestRepository, never()).save(any());
    }

    // findOrCreate – nowy gość

    @Test
    void shouldCreateGuestWhenEmailNotFound() {
        when(guestRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(guestRepository.save(any(Guest.class))).thenAnswer(inv -> inv.getArgument(0));

        Guest result = guestService.findOrCreate(FIRST_NAME, LAST_NAME, EMAIL);

        assertThat(result.getFirstName()).isEqualTo(FIRST_NAME);
        assertThat(result.getLastName()).isEqualTo(LAST_NAME);
        assertThat(result.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void shouldSaveNewGuestWithNormalizedEmail() {
        when(guestRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(guestRepository.save(any(Guest.class))).thenAnswer(inv -> inv.getArgument(0));

        guestService.findOrCreate(FIRST_NAME, LAST_NAME, "  JAN@Example.COM ");

        ArgumentCaptor<Guest> captor = ArgumentCaptor.forClass(Guest.class);
        verify(guestRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo(EMAIL);
    }

    @Test
    void shouldReturnInstanceReturnedByRepository() {
        Guest saved = guest();
        when(guestRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(guestRepository.save(any(Guest.class))).thenReturn(saved);

        Guest result = guestService.findOrCreate(FIRST_NAME, LAST_NAME, EMAIL);

        assertThat(result).isSameAs(saved);
    }

    // helpers

    private Guest guest() {
        return new Guest(FIRST_NAME, LAST_NAME, EMAIL);
    }
}
