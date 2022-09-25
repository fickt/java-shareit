package ru.practicum.shareit.requesttest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.requests.converter.RequestConverter;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.requests.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.rowmapper.UserDtoRowMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ItemRequestServiceTest {

    @Autowired
    ItemRequestServiceImpl requestService;

    @MockBean
    ItemRequestRepository requestRepository;

    @MockBean
    UserRepository userRepository;

    ItemRequestDto requestDto;

    UserDto userDto;

    @BeforeEach
    void createRequest() {
        requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .requestorId(1L)
                .build();
    }

    @BeforeEach
    void createUser() {
        userDto = UserDto.builder()
                .id(1L)
                .name("John")
                .email("garrys2machinima@gmail.com")
                .build();
    }



    @Test
    void shouldCreateRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(requestRepository.save(any()))
                .thenReturn(RequestConverter.convertDtoToRequest(requestDto));

        requestService.createItemRequest(requestDto, 1L);
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).save(any());
    }

    @Test
    void shouldReturnListOfRequestsOfRequestor() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(requestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn((List.of(RequestConverter.convertDtoToRequest(requestDto))));

        requestService.getAllRequests(1L);

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findAllByRequestorIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void shouldReturnRangeOfRequestsOfRequestorWithFromAndSizeEqualNull() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(requestRepository.findAllByIdNotNull())
                .thenReturn((List.of(RequestConverter.convertDtoToRequest(requestDto))));

        requestService.getRangeOfRequests(null, null, 1L);

        verify(requestRepository, times(1)).findAllByIdNotNull();
    }

    @Test
    void shouldReturnRangeOfRequestsOfRequestorWithFromAndSizeEqualOneAndTwo() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(requestRepository.findAllByIdNotNull(any()))
                .thenReturn((List.of(RequestConverter.convertDtoToRequest(requestDto))));

        requestService.getRangeOfRequests(1L, 2L, 1L);

        verify(requestRepository, times(1)).findAllByIdNotNull(any());
    }

    @Test
    void shouldGetItemRequestById() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(UserDtoRowMapper.convertDtoToUser(userDto)));

        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(RequestConverter.convertDtoToRequest(requestDto)));

        requestService.getItemRequestById(1L, 1L);

        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
    }
}
