package org.course.mockitoapp.services;

import org.course.mockitoapp.models.Exam;
import org.course.mockitoapp.repositories.ExamRepository;
import org.course.mockitoapp.repositories.ExamRepositoryImpl;
import org.course.mockitoapp.repositories.QuestionsRepository;
import org.course.mockitoapp.repositories.QuestionsRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceImplTest {

    @Mock
    ExamRepositoryImpl repository;
    @Mock
    QuestionsRepositoryImpl questionsRepository;

    @InjectMocks
    ExamServiceImpl service;

    @Captor
    ArgumentCaptor<Long> captor;

    @BeforeEach
    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        repository = mock(ExamRepositoryImpl.class);
//        questionsRepository = mock(QuestionsRepositoryImpl.class);
//        service = new ExamServiceImpl(repository, questionsRepository);
    }

    @Test
    void findByName() {
        when(repository.findAll()).thenReturn(Data.EXAMS);

        Optional<Exam> exam = service.findByName("Maths");

        assertTrue(exam.isPresent());
        assertEquals(5L, exam.orElseThrow().getId());
        assertEquals("Maths", exam.get().getName());
    }

    @Test
    void findByNameEmptyList() {
        List<Exam> data = Collections.emptyList();
        when(repository.findAll()).thenReturn(data);

        Optional<Exam> exam = service.findByName("Maths");

        assertFalse(exam.isPresent());
    }

    @Test
    void testExamQuestions() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionsRepository.findQuestionsByExamId(5L)).thenReturn(Data.QUESTIONS);

        Exam exam = service.findExamByNameWithQuestions("Maths");

        assertEquals(4, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("geometry"));
    }

    @Test
    void testExamQuestionsVerify() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionsRepository.findQuestionsByExamId(5L)).thenReturn(Data.QUESTIONS);

        Exam exam = service.findExamByNameWithQuestions("Maths");

        verify(repository).findAll();
        verify(questionsRepository).findQuestionsByExamId(5L);
    }

    @Test
    void testExamNotExistVerify() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        Exam exam = service.findExamByNameWithQuestions("Maths");

        assertNull(exam);
        verify(repository).findAll();
        verifyNoInteractions(questionsRepository);
    }

    @Test
    void testSaveExam() {
        Exam newExam = Data.EXAM;
        newExam.setQuestions(Data.QUESTIONS);
        when(repository.save(any(Exam.class))).then(new Answer<Exam>() {
            Long secuence = 8L;
            @Override
            public Exam answer(InvocationOnMock invocationOnMock) throws Throwable {
                Exam exam = invocationOnMock.getArgument(0);
                exam.setId(secuence++);
                return exam;
            }
        });

        Exam exam = service.save(newExam);

        assertNotNull(exam.getId());
        assertEquals(8L, exam.getId());
        assertEquals("Biology", exam.getName());
        verify(repository).save(any(Exam.class));
        verify(questionsRepository).saveQuestions(anyList());
    }

    @Test
    void testExceptionManagement() {
        when(repository.findAll()).thenReturn(Data.NULL_ID_EXAMS);
        when(questionsRepository.findQuestionsByExamId(null)).thenThrow(IllegalArgumentException.class);

        Exception exception =  assertThrows(IllegalArgumentException.class, () -> {
            service.findExamByNameWithQuestions("Maths");
        });
        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(repository).findAll();
        verify(questionsRepository).findQuestionsByExamId(null);
    }

    @Test
    void testArgumentMatchers() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionsRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        service.findExamByNameWithQuestions("Maths");

        verify(repository).findAll();
        //verify(questionsRepository).findQuestionsByExamId(argThat(arg -> arg != null && arg.equals(5L)));
        verify(questionsRepository).findQuestionsByExamId(argThat(arg -> arg != null && arg >= 5L));
    }

    @Test
    void testArgumentMatchersWithClass() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionsRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        service.findExamByNameWithQuestions("Maths");

        verify(repository).findAll();
        verify(questionsRepository).findQuestionsByExamId(argThat(new MiArgsMatchers()));
    }

    @Test
    void testArgumentMatchersLambdaExpression() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        when(questionsRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        service.findExamByNameWithQuestions("Maths");

        verify(repository).findAll();
        verify(questionsRepository).findQuestionsByExamId(argThat((argument) -> argument != null && argument > 0));
    }

    public static class MiArgsMatchers implements ArgumentMatcher<Long> {

        private Long argument;

        @Override
        public boolean matches(Long argument) {
            this.argument = argument;
            return argument != null && argument > 0;
        }

        @Override
        public String toString() {
            return "This is a personalized error message " +
                    "printed by mockito in case test failed" +
                    argument + " must be positive";
        }
    }

    @Test
    void testArgumentCaptor() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
//        when(questionsRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        service.findExamByNameWithQuestions("Maths");

//        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(questionsRepository).findQuestionsByExamId(captor.capture());
        assertEquals(5L, captor.getValue());
    }

    @Test
    void testDoThrow() {
        Exam exam = Data.EXAM;
        exam.setQuestions(Data.QUESTIONS);
        doThrow(IllegalArgumentException.class).when(questionsRepository).saveQuestions(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
            service.save(exam);
        });
    }

    @Test
    void testDoAnswer() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
//        when(questionsRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5L? Data.QUESTIONS: null;
        }).when(questionsRepository).findQuestionsByExamId(anyLong());

        Exam exam = service.findExamByNameWithQuestions("Maths");

        assertEquals(4, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("geometry"));
        assertEquals(5L, exam.getId());
        assertEquals("Maths", exam.getName());
    }

    @Test
    void testDoAnswerSaveExam() {
        Exam newExam = Data.EXAM;
        newExam.setQuestions(Data.QUESTIONS);
        doAnswer(new Answer<Exam>() {
            Long secuence = 8L;
            @Override
            public Exam answer(InvocationOnMock invocationOnMock) throws Throwable {
                Exam exam = invocationOnMock.getArgument(0);
                exam.setId(secuence++);
                return exam;
            }
        }).when(repository).save(any(Exam.class));

        Exam exam = service.save(newExam);

        assertNotNull(exam.getId());
        assertEquals(8L, exam.getId());
        assertEquals("Biology", exam.getName());
        verify(repository).save(any(Exam.class));
        verify(questionsRepository).saveQuestions(anyList());
    }

    @Test
    void testDoCallRealMethod() {
        when(repository.findAll()).thenReturn(Data.EXAMS);
        //when(questionsRepository.findQuestionsByExamId(anyLong())).thenReturn(Data.QUESTIONS);
        doCallRealMethod().when(questionsRepository).findQuestionsByExamId(anyLong());

        Exam exam = service.findExamByNameWithQuestions("Maths");

        assertEquals(5L, exam.getId());
        assertEquals("Maths", exam.getName());
    }

    @Test
    void testSpy() {
        ExamRepository examRepository = spy(ExamRepositoryImpl.class);
        QuestionsRepository questionRepository = spy(QuestionsRepositoryImpl.class);
        ExamService examService = new ExamServiceImpl(examRepository, questionRepository);

        List<String> question = Arrays.asList("Arithmetic");
//        when(questionRepository.findQuestionsByExamId(anyLong())).thenReturn(question);
        doReturn(question).when(questionRepository).findQuestionsByExamId(anyLong());

        Exam exam = examService.findExamByNameWithQuestions("Maths");

        assertEquals(5, exam.getId());
        assertEquals("Maths", exam.getName());
        assertEquals(1, exam.getQuestions().size());
        assertTrue(exam.getQuestions().contains("Arithmetic"));
        verify(examRepository).findAll();
        verify(questionRepository).findQuestionsByExamId(5L);
    }
}