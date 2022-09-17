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

import java.util.List;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Override
    public ItemRequestDto createItemRequest(ItemRequestDto requestDto) {
        ItemRequest request = RequestConverter.convertDtoToRequest(requestDto);
        return RequestConverter.convertRequestToDto(requestRepository.save(request));
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long requestorId) {
       return RequestConverter
               .convertRequestListToDto(requestRepository.findAllByRequestorIdOrderByCreatedDesc(requestorId));
    }

    @Override
    public List<ItemRequestDto> getRangeOfRequests(Long from, Long size) {
       return RequestConverter.
               convertRequestListToDto(requestRepository
                       .findAllFromIdToId(PageRequest.of(from.intValue(), size.intValue(), Sort.by("created").descending())));
    }

    @Override
    public ItemRequestDto getItemRequestById(Long requestId) {
        ItemRequest itemRequest = requestRepository
                .findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with ID: %s has not been found", requestId)));
        return RequestConverter.convertRequestToDto(itemRequest);
    }

}
