package ro.unitbv.todoapp.utils;

import androidx.room.TypeConverter;
import java.util.Date;
import ro.unitbv.todoapp.model.Priority;

public class Converter {

  @TypeConverter
  public static Date dateFromTimeStamp(Long value) {
    return value == null ? null : new Date(value);
  }

  @TypeConverter
  public static Long dateToTimestamp(Date date) {
    return date == null ? null : date.getTime();
  }

  @TypeConverter
  public static String fromPriority(Priority priority) {
    return priority == null ? null : priority.name();
  }

  @TypeConverter
  public static Priority toPriority(String priority) {
    return priority == null ? null : Priority.valueOf(priority);
  }
}
