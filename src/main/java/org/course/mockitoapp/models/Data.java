package org.course.mockitoapp.services;

import org.course.mockitoapp.models.Exam;

import java.util.Arrays;
import java.util.List;

public class Data {
    public final static List<Exam> EXAMS = Arrays.asList(
            new Exam(5L, "Maths"),
            new Exam(7L, "Lenguaje"),
            new Exam(6L, "History"));
    public final static List<Exam> NULL_ID_EXAMS = Arrays.asList(
            new Exam(null, "Maths"),
            new Exam(null, "Lenguaje"),
            new Exam(null, "History"));
    public final static List<Exam> NEG_ID_EXAMS = Arrays.asList(
            new Exam(-5L, "Maths"),
            new Exam(-7L, "Lenguaje"),
            new Exam(-6L, "History"));

    public final static List<String> QUESTIONS = Arrays.asList(
            "arithmetics",
            "integrals",
            "geometry",
            "trigonometry");

    public final static Exam EXAM = new Exam(null, "Biology");
}
