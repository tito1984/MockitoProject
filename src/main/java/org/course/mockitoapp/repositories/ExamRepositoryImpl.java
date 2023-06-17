package org.course.mockitoapp.repositories;

import org.course.mockitoapp.models.Data;
import org.course.mockitoapp.models.Exam;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamRepositoryOther implements ExamRepository {
    @Override
    public List<Exam> findAll() {
        try {
            System.out.println("Other Exam Repository");
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Data.EXAMS;
    }

    @Override
    public Exam save(Exam exam) {
        return Data.EXAM;
    }
}
