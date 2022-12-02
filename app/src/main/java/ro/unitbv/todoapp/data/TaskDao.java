package ro.unitbv.todoapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;
import ro.unitbv.todoapp.model.Task;

@Dao
public interface TaskDao {

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  void insert(Task task);

  @Update
  void update(Task task);

  @Query("DELETE FROM toDoList_database")
  void deleteAll();

  @Query("SELECT * FROM toDoList_database")
  LiveData<List<Task>> getAllTask();

  @Delete
  void delete(Task task);

  @Query("SELECT * FROM toDoList_database WHERE toDoList_database.taskId==:id")
  LiveData<Task> getOneTask(long id);
}
