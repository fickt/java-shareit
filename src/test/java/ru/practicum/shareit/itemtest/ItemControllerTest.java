package ru.practicum.shareit.itemtest;

import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImplRepos;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    MockMvc mockMvc;

    Gson gson = new Gson();

    @MockBean
    ItemServiceImplRepos itemService;

    ItemDto itemDto;

    @BeforeEach
    void createItem() {
        itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("just an item")
                .isAvailable(true)
                .ownerId(1L)
                .build();
    }

    @Test
    void shouldCreateItem() throws Exception {
        when(itemService.addItem(anyLong(), Mockito.any()))
                .thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(gson.toJson(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.ownerId").value(itemDto.getOwnerId()));
    }

    @Test
    void shouldReturnExistingItem() throws Exception {
        when(itemService.getItem(anyLong(), Mockito.any()))
                .thenReturn(itemDto);
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.ownerId").value(itemDto.getOwnerId()));
    }

    @Test
    void shouldEditAndReturnItem() throws Exception {
        when(itemService.editItem(anyLong(), anyLong() , Mockito.any()))
                .thenReturn(itemDto);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(gson.toJson(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.ownerId").value(itemDto.getOwnerId()));
    }

    @Test
    void shouldReturnListOfItems() throws Exception {
        when(itemService.getListOfItems(anyLong(), anyLong(), anyLong()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items?from=1&size=1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$.[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$.[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.[0].ownerId").value(itemDto.getOwnerId()));
    }

    @Test
    void shouldReturnListOfItemsBySearch() throws Exception {
        when(itemService.getListOfItemsBySearch(anyString(), any(), any()))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search?text=item&from=1&size=1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$.[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$.[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.[0].ownerId").value(itemDto.getOwnerId()));
    }

    @Test
    void shouldAddCommentToItemAndReturnItemWithComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorId(1L)
                .authorName("Nick")
                .itemId(1L)
                .text("Somebody once told me the world is gonna roll me")
                .build();
        when(itemService.addCommentToItem(anyLong(),anyLong(), any()))
                .thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(gson.toJson(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.itemId").value(commentDto.getItemId()))
                .andExpect(jsonPath("$.itemId").value(commentDto.getItemId()))
                .andExpect(jsonPath("$.authorId").value(commentDto.getAuthorId()));
    }
}
