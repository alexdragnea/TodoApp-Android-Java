package ro.unitbv.todoapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import ro.unitbv.todoapp.adapter.OnToDoClickListener;
import ro.unitbv.todoapp.adapter.RecyclerViewAdapter;
import ro.unitbv.todoapp.model.SharedViewModel;
import ro.unitbv.todoapp.model.Task;
import ro.unitbv.todoapp.model.TaskViewModel;

public class MainActivity extends AppCompatActivity implements OnToDoClickListener {
  BottomSheetFragment bottomSheetFragment;
  private TaskViewModel taskViewModel;
  private SharedViewModel sharedViewModel;
  private ImageView floatingButton;
  ImageView mCreateRem;
  private RecyclerView recyclerView;
  private RecyclerViewAdapter recyclerViewAdapter;
  private EditText todo_edit_text;
  private ConstraintLayout todoRowLayout;

  private AlertDialog.Builder deleteDialog;

  public static final String NOTIFICATION_CHANNEL_ID = "10001";
  private static final String default_notification_channel_id = "default";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    floatingButton = findViewById(R.id.fab_button);
    todo_edit_text = findViewById(R.id.todo_et);

    mCreateRem =
        findViewById(R.id.notification_button); // Floating action button to change activity
    mCreateRem.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), ReminderActivity.class);
            startActivity(intent); // Starts the new activity to add Reminders
          }
        });
    LottieAnimationView lottieAnimationView = findViewById(R.id.lottie_animation);

    // animation

    // setting the toolbar
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    int white = Color.parseColor("#ffffff");
    int yellow = Color.parseColor("#EEE311");
    toolbar.setTitleTextColor(white);
    toolbar.setSubtitleTextColor(yellow);

    taskViewModel =
        new ViewModelProvider.AndroidViewModelFactory(MainActivity.this.getApplication())
            .create(TaskViewModel.class);

    // setting recyclerview
    recyclerView = findViewById(R.id.recycler_view);
    recyclerView.setHasFixedSize(true);
    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

    taskViewModel
        .fetchAllTaskViewModel()
        .observe(
            this,
            taskList -> {
              recyclerViewAdapter = new RecyclerViewAdapter(taskList, this);
              recyclerView.setAdapter(recyclerViewAdapter);

              if (recyclerViewAdapter.getItemCount() == 0) {
                if (lottieAnimationView.getVisibility() == View.GONE) {
                  lottieAnimationView.setVisibility(View.VISIBLE);
                  lottieAnimationView.playAnimation();
                }
                Toast.makeText(MainActivity.this, "Add Your Task,Please", Toast.LENGTH_LONG).show();
              } else {
                if (lottieAnimationView.getVisibility() == View.VISIBLE) {
                  lottieAnimationView.setVisibility(View.GONE);
                  lottieAnimationView.cancelAnimation();
                }
              }
            });

    // setting the sharedviewmodel-bridge btw fragment and activity
    sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

    // extracting from the bottom sheet
    bottomSheetFragment = new BottomSheetFragment();
    ConstraintLayout constraintLayout = findViewById(R.id.bottomSheet);
    BottomSheetBehavior<ConstraintLayout> bottomSheetBehavior =
        BottomSheetBehavior.from(constraintLayout);
    bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.STATE_HIDDEN);

    // hitting the floating button
    floatingButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            todo_edit_text.getText().clear();
            Log.d("tag", "onClick: clicked!");
            showBottomSheetDialog();
          }
        });
  }

  public void showBottomSheetDialog() {
    bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
  }

  // sets the options menu.
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {

    getMenuInflater().inflate(R.menu.main_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void toDoOnclick(int position, Task task) {
    Log.d("onCreate", "" + position);

    sharedViewModel.setSelectItem(task);
    sharedViewModel.setIsEdit(true);
    showBottomSheetDialog();
  }

  @Override
  public void radioOnClick(Task task) {
    Log.d("onCreate", task.getTask());

    // setting the dialog box
    deleteDialog = new AlertDialog.Builder(this);

    deleteDialog.setTitle("Delete !");
    deleteDialog.setMessage("Do you want to delete the current Task ?");
    deleteDialog.setIcon(R.drawable.ic_baseline_delete_24);

    deleteDialog.setPositiveButton(
        "Yes",
        (dialogInterface, i) -> {
          TaskViewModel.deleteViewModel(task);

          recyclerViewAdapter.notifyDataSetChanged();

          Snackbar.make(floatingButton, "Deleted Successfully", Snackbar.LENGTH_SHORT).show();
        });

    deleteDialog.setNegativeButton(
        "No",
        (dialogInterface, i) -> {
          ((RadioButton) findViewById(R.id.rowxml_radio_button)).setChecked(false);
          findViewById(R.id.rowxml_radio_button).setSelected(false);

          Snackbar.make(floatingButton, "Left Intact", Snackbar.LENGTH_SHORT).show();
        });

    deleteDialog.show();
  }

  @Override
  public void chipOnClick(Task task) {
    Snackbar.make(floatingButton, task.getTask(), Snackbar.LENGTH_SHORT).show();
  }

  //  private void scheduleNotification(Notification notification, int delay) {
  //    Intent notificationIntent = new Intent(this, NotificationPublisher.class);
  //    notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
  //    notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
  //    PendingIntent pendingIntent =
  //        PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);
  //    long futureInMillis = SystemClock.elapsedRealtime() + delay;
  //    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
  //    assert alarmManager != null;
  //    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
  //  }
  //
  //  private Notification getNotification(String content) {
  //    NotificationCompat.Builder builder =
  //        new NotificationCompat.Builder(this, default_notification_channel_id);
  //    builder.setContentTitle("Scheduled Notification");
  //    builder.setContentText(content);
  //    builder.setSmallIcon(R.drawable.ic_launcher_foreground);
  //    builder.setAutoCancel(true);
  //    builder.setChannelId(NOTIFICATION_CHANNEL_ID);
  //    return builder.build();
  //  }
}
