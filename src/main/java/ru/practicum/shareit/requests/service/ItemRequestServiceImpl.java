package ru.practicum.shareit.requests.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.requests.converter.RequestConverter;
import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.requests.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;

    private final UserRepository userRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto requestDto, Long requestorId) {
        userRepository
                .findById(requestorId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID: %s has not been found!", requestorId)));
        requestDto.setCreated(LocalDateTime.now());
        requestDto.setRequestorId(requestorId);
        ItemRequest request = RequestConverter.convertDtoToRequest(requestDto);
        return RequestConverter.convertRequestToDto(requestRepository.save(request));
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long requestorId) {
        userRepository
                .findById(requestorId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID: %s has not been found!", requestorId)));
       return RequestConverter
               .convertRequestListToDto(requestRepository.findAllByRequestorIdOrderByCreatedDesc(requestorId));
    }

    @Override
    public List<ItemRequestDto> getRangeOfRequests(Long from, Long size, Long userId) {
        if(from == null || size == null) {
            return RequestConverter.
                    convertRequestListToDto(requestRepository
                            .findAllByIdNotNull())
                    .stream()
                    .filter(o -> !o.getItems().isEmpty() && !o.getRequestorId().equals(userId))
                    .collect(Collectors.toList());
        }
        return RequestConverter.
                convertRequestListToDto(requestRepository
                        .findAllByIdNotNull(PageRequest.of(from.intValue(), size.intValue(), Sort.by("created").descending())))
                .stream()
                .filter(o -> !o.getItems().isEmpty() && !o.getRequestorId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId, Long userId) {
        userRepository
                .findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with ID: %s has not been found!", userId)));

        ItemRequest itemRequest = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with ID: %s has not been found", requestId)));
        return RequestConverter.convertRequestToDto(itemRequest);
    }
}
