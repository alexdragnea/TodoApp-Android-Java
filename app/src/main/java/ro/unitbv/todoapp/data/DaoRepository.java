package ro.unitbv.todoapp.data;

import android.app.Application;
import androidx.lifecycle.LiveData;
import java.util.List;
import ro.unitbv.todoapp.model.Task;
import ro.unitbv.todoapp.utils.TaskRoomDatabase;

public class DaoRepository {
  private final LiveData<List<Task>> getAllTaskListRep;

  private final TaskDao taskDaoRepo;

  public DaoRepository(Application application) {
    TaskRoomDatabase database = TaskRoomDatabase.getDatabase(application);
    taskDaoRepo = database.taskDao();
    getAllTaskListRep = taskDaoRepo.getAllTask();
  }

  public void insertRepo(Task task) {
    TaskRoomDatabase.databaseWriterExecutor.execute(
        () -> {
          taskDaoRepo.insert(task);
        });
  }

  public void updateRepo(Task task) {
    TaskRoomDatabase.databaseWriterExecutor.execute(
        () -> {
          taskDaoRepo.update(task);
        });
  }

  public void deleteAllRepo() {
    taskDaoRepo.deleteAll();
  }

  public void deleteRepo(Task task) {
    TaskRoomDatabase.databaseWriterExecutor.execute(
        () -> {
          taskDaoRepo.delete(task);
        });
  }

  public LiveData<List<Task>> fetchAllTaskRepo() {
    return getAllTaskListRep;
  }

  public LiveData<Task> fetchSingleTask(long id) {
    return taskDaoRepo.getOneTask(id);
  }
}
