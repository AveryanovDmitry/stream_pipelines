package com.efimchick.ifmo;

import com.efimchick.ifmo.util.CourseResult;
import com.efimchick.ifmo.util.Person;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Collecting {
    private static final byte CHECK_ODD = 2;
    private static final byte MIN_SCORE = 0;
    private static final byte MAX_SCORE = 100;
    private static final byte A_MARK = 90;
    private static final byte B_MARK = 83;
    private static final byte C_MARK = 75;
    private static final byte D_MARK = 68;
    private static final byte E_MARK = 60;

    public int sum(IntStream intStream) {
        return intStream.sum();
    }

    public int production(IntStream intStream) {
        return intStream.reduce((accumulate, element) -> accumulate * element).orElse(0);
    }

    public int oddSum(IntStream intStream) {
        return intStream.filter(i -> i % CHECK_ODD != 0).sum();
    }

    public Map<Integer, Integer> sumByRemainder(int devisor, IntStream intStream) {
        return intStream.boxed().collect(Collectors.groupingBy(i -> i % devisor, Collectors.summingInt(i -> i)));
    }

    public Map<Person, Double> totalScores(Stream<CourseResult> courseResultStream) {
        List<CourseResult> results = courseResultStream.collect(Collectors.toList());
        long countCourses = results.stream()
                .map(s -> s.getTaskResults().keySet())
                .flatMap(Collection::stream)
                .distinct()
                .count();
        return results.stream().collect(Collectors.groupingBy(
                CourseResult::getPerson,
                Collectors.summingDouble(courseResult -> (double) courseResult
                        .getTaskResults()
                        .values()
                        .stream()
                        .reduce(Integer::sum)
                        .orElse(0) / countCourses)
        ));
    }

    public double averageTotalScore(Stream<CourseResult> courseResultStream) {
        Map<Person, Double> totalScores = totalScores(courseResultStream);
        return totalScores.values().stream().reduce(0.0, Double::sum) / totalScores.size();
    }

    public Map<String, Double> averageScoresPerTask(Stream<CourseResult> courseResultStream) {
        List<CourseResult> resultsList = courseResultStream.collect(Collectors.toList());
        long countPerson = resultsList.stream()
                .map(CourseResult::getPerson)
                .distinct()
                .count();

        return resultsList.stream()
                .flatMap(courseResult -> courseResult.getTaskResults().entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.summingDouble(pair -> pair.getValue() / (double) countPerson)));
    }

    public Map<Person, String> defineMarks(Stream<CourseResult> courseResultStream) {
        Map<Person, Double> totalScoresMap = totalScores(courseResultStream);
        return totalScoresMap
                .entrySet()
                .stream()
                .collect(
                        Collectors.toMap(Map.Entry::getKey, v -> getMark(v.getValue()))
                );
    }

    private String getMark(Double score) {
        String mark;

        if (score > MAX_SCORE || score < MIN_SCORE) {
            mark = "Error";
        } else if (score > A_MARK) {
            mark = "A";
        } else if (score >= B_MARK) {
            mark = "B";
        } else if (score >= C_MARK) {
            mark = "C";
        } else if (score >= D_MARK) {
            mark = "D";
        } else if (score >= E_MARK) {
            mark = "E";
        } else {
            mark = "F";
        }
        return mark;
    }

    public String easiestTask(Stream<CourseResult> courseResultStream) {
        Map<String, Double> averageScorePerTask = averageScoresPerTask(courseResultStream);
        return Collections.max(averageScorePerTask.entrySet(), Map.Entry.comparingByValue()).getKey();
    }
}