package ru.journal.homework.aggregator.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.journal.homework.aggregator.domain.*;
import ru.journal.homework.aggregator.repo.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.*;

@Service
@AllArgsConstructor
public class StudentService {
    private final LessonRepo lessonRepo;
    private final LessonTypeRepo lessonTypeRepo;
    private final PairRepo pairRepo;
    private final GroupRepo groupRepo;
    private final LessonMessageRepo lessonMessageRepo;

    public List<String> getDatesString(Integer shift){
        LocalDate today = LocalDate.now();

        // Находим понедельник текущей недели
        LocalDate monday = today;
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1);
        }

        // Применяем смещение в неделях
        monday = monday.plusWeeks(shift);

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("d ")
                .appendText(ChronoField.MONTH_OF_YEAR, new HashMap<Long, String>() {{
                    put(1L, "января");
                    put(2L, "февраля");
                    put(3L, "марта");
                    put(4L, "апреля");
                    put(5L, "мая");
                    put(6L, "июня");
                    put(7L, "июля");
                    put(8L, "августа");
                    put(9L, "сентября");
                    put(10L, "октября");
                    put(11L, "ноября");
                    put(12L, "декабря");
                }})
                .toFormatter(new Locale("ru"));

        List<String> dates = new ArrayList<>();
        for (int i = 0; i < 6; ++i) {
            LocalDate date = monday.plusDays(i);
            String dayName = date.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("ru"));
            dayName = dayName.substring(0, 1).toUpperCase() + dayName.substring(1);
            String formattedDate = date.format(formatter);
            dates.add(dayName + ", " + formattedDate);
        }

        return dates;
    }

    public List<LocalDate> getDates(Integer shift){
        LocalDate today = LocalDate.now();

        LocalDate monday = today;
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {
            monday = monday.minusDays(1);
        }
        monday = monday.plusWeeks(shift);

        List<LocalDate> dates = new ArrayList<>();
        for (int i = 0; i < 6; ++i) {
            dates.add(monday.plusDays(i));
        }

        return dates;
    }

    public Map<String, Lesson> getWeekLessons(Long groupId, Integer weekShift) {
        List<LocalDate> dates = getDates(weekShift);
        List<Lesson> lessons = lessonRepo.findByGroupIdAndDateIn(groupId, dates);

        Map<String, Lesson> lessonMap = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Lesson lesson : lessons) {
            String key = lesson.getDate().format(formatter) + "_" + lesson.getPair().getId();
            lessonMap.put(key, lesson);
        }

        return lessonMap;
    }

    public List<Pair> getAllPairs() {
        return pairRepo.findAll();
    }

    public Group getStudentGroup(User user) {
        return groupRepo.findGroupByStudentUserId(user.getId());
    }

    public Map<String, LessonMessage> getWeekLessonMessages(Map<String, Lesson> lessons) {
        Map<String, LessonMessage> messagesMap = new HashMap<>();
        
        for (Map.Entry<String, Lesson> entry : lessons.entrySet()) {
            List<LessonMessage> messages = lessonMessageRepo.findByLessonsId(entry.getValue().getId());
            if (!messages.isEmpty()) {
                messagesMap.put(entry.getKey(), messages.get(0));
            }
        }
        
        return messagesMap;
    }
}
