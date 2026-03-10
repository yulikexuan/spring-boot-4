//: spring.boot.sfg7.rest.mvc.web.controller.FlashcardControllerTest.java

package spring.boot.sfg7.rest.mvc.web.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import spring.boot.sfg7.rest.mvc.domain.ocp.model.Flashcard;
import spring.boot.sfg7.rest.mvc.domain.ocp.service.FlashcardService;
import tools.jackson.databind.ObjectMapper;


@WebMvcTest(FlashcardController.class)
@ExtendWith(MockitoExtension.class)
@DisplayName("Test FlashcardController - ")
@DisplayNameGeneration(ReplaceUnderscores.class)
class FlashcardControllerTest {

    static final String FLASHCARD_URI    = "/sfg7/api/v1/flashcard";
    static final String FLASHCARD_ID_PATH = "/{flashcardId}";

    @MockitoBean
    private FlashcardService flashcardService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void able_To_Get_Flashcard_By_Id() throws Exception {
        Long flashcardId = 1L;
        Flashcard card = Flashcard.builder()
                .id(flashcardId)
                .question("Which functional interface converts from int to double?")
                .answer("IntToDoubleFunction")
                .weight(2)
                .build();

        given(flashcardService.getFlashcardById(flashcardId)).willReturn(card);

        URI uri = ServletUriComponentsBuilder.fromUriString(FLASHCARD_URI)
                .path(FLASHCARD_ID_PATH)
                .buildAndExpand(flashcardId)
                .toUri();

        mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(flashcardId))
                .andExpect(jsonPath("$.question").value("Which functional interface converts from int to double?"))
                .andExpect(jsonPath("$.answer").value("IntToDoubleFunction"))
                .andExpect(jsonPath("$.weight").value(2));
    }

    @Test
    void able_To_Get_All_Flashcards_In_List() throws Exception {
        List<Flashcard> cards = List.of(
                Flashcard.builder().id(1L).question("Q1").answer("A1").weight(1).build(),
                Flashcard.builder().id(2L).question("Q2").answer("A2").weight(2).build());

        given(flashcardService.findAllFlashcards()).willReturn(cards);

        URI uri = ServletUriComponentsBuilder.fromUriString(FLASHCARD_URI).build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.get(uri).accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(cards.size()));
    }

    @Test
    void able_To_Save_New_Flashcard() throws Exception {
        Flashcard input = Flashcard.builder()
                .question("What is a compile-time constant variable?")
                .answer("A variable that is marked final and initialized with a literal value when it is declared")
                .weight(1)
                .build();
        Flashcard saved = Flashcard.builder()
                .id(42L)
                .question(input.question())
                .answer(input.answer())
                .weight(input.weight())
                .build();

        given(flashcardService.saveNewFlashcard(any(Flashcard.class))).willReturn(saved);

        URI uri = ServletUriComponentsBuilder.fromUriString(FLASHCARD_URI).build().toUri();

        mockMvc.perform(MockMvcRequestBuilders.post(uri)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.containsString(String.valueOf(saved.id()))));
    }

    @Test
    void able_To_Update_Flashcard() throws Exception {
        Long flashcardId = 1L;
        Flashcard update = Flashcard.builder()
                .question("Updated question")
                .answer("Updated answer")
                .weight(3)
                .build();

        willDoNothing().given(flashcardService).updateFlashcardById(anyLong(), any(Flashcard.class));

        URI uri = ServletUriComponentsBuilder.fromUriString(FLASHCARD_URI)
                .path(FLASHCARD_ID_PATH)
                .buildAndExpand(flashcardId)
                .toUri();

        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNoContent());
    }

    @Test
    void able_To_Delete_Flashcard() throws Exception {
        Long flashcardId = 1L;

        willDoNothing().given(flashcardService).deleteFlashcardById(anyLong());

        URI uri = ServletUriComponentsBuilder.fromUriString(FLASHCARD_URI)
                .path(FLASHCARD_ID_PATH)
                .buildAndExpand(flashcardId)
                .toUri();

        mockMvc.perform(MockMvcRequestBuilders.delete(uri))
                .andExpect(status().isNoContent());
    }

    @Test
    void able_To_Patch_Flashcard() throws Exception {
        Long flashcardId = 1L;
        Flashcard patch = Flashcard.builder()
                .question("Patched question")
                .answer("")
                .weight(0)
                .build();

        willDoNothing().given(flashcardService).patchFlashcardById(anyLong(), any(Flashcard.class));

        URI uri = ServletUriComponentsBuilder.fromUriString(FLASHCARD_URI)
                .path(FLASHCARD_ID_PATH)
                .buildAndExpand(flashcardId)
                .toUri();

        mockMvc.perform(MockMvcRequestBuilders.patch(uri)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isNoContent());
    }
}
