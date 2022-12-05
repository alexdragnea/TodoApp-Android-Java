package ro.unitbv.todoapp;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import ro.unitbv.todoapp.notification.NotificationPublisher;

public class ReminderActivity extends AppCompatActivity {

  Button mSubmitbtn, mDatebtn, mTimebtn;
  EditText mTitledit;
  String timeTonotify;
  public static final String NOTIFICATION_CHANNEL_ID = "10001";
  private static final String default_notification_channel_id = "default";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_reminder);

    mTitledit = (EditText) findViewById(R.id.editTitle);
    mDatebtn =
        (Button)
            findViewById(R.id.btnDate); // assigned all the material reference to get and set data
    mTimebtn = (Button) findViewById(R.id.btnTime);
    mSubmitbtn = (Button) findViewById(R.id.btnSbumit);

    mTimebtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            selectTime(); // when we click on the choose time button it calls the select time method
          }
        });

    mDatebtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            selectDate();
          } // when we click on the choose date button it calls the select date method
        });

    mSubmitbtn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String title =
                mTitledit.getText().toString().trim(); // access the data form the input field
            String date =
                mDatebtn.getText().toString().trim(); // access the date form the choose date button
            String time =
                mTimebtn.getText().toString().trim(); // access the time form the choose time button

            if (title.isEmpty()) {
              Toast.makeText(getApplicationContext(), "Please Enter text", Toast.LENGTH_SHORT)
                  .show(); // shows the toast if input field is empty
            } else {
              if (time.equals("time")
                  || date.equals("date")) { // shows toast if date and time are not selected
                Toast.makeText(
                        getApplicationContext(), "Please select date and time", Toast.LENGTH_SHORT)
                    .show();
              } else {
                scheduleNotification(getNotification(title), date, time);
                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
              }
            }
          }
        });
  }

  private void selectTime() { // this method performs the time picker task
    Calendar calendar = Calendar.getInstance();
    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    TimePickerDialog timePickerDialog =
        new TimePickerDialog(
            this,
            new TimePickerDialog.OnTimeSetListener() {
              @Override
              public void onTimeSet(TimePicker timePicker, int i, int i1) {
                timeTonotify = i + ":" + i1; // temp variable to store the time to set alarm
                mTimebtn.setText(FormatTime(i, i1)); // sets the button text as selected time
              }
            },
            hour,
            minute,
            false);
    timePickerDialog.show();
  }

  private void selectDate() { // this method performs the date picker task
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    DatePickerDialog datePickerDialog =
        new DatePickerDialog(
            this,
            new DatePickerDialog.OnDateSetListener() {
              @Override
              public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                mDatebtn.setText(
                    day
                        + "-"
                        + (month + 1)
                        + "-"
                        + year); // sets the selected date as test for button
              }
            },
            year,
            month,
            day);
    datePickerDialog.show();
  }

  public String FormatTime(
      int hour, int minute) { // this method converts the time into 12hr farmat and assigns am or pm

    String time;
    time = "";
    String formattedMinute;

    if (minute / 10 == 0) {
      formattedMinute = "0" + minute;
    } else {
      formattedMinute = "" + minute;
    }

    if (hour == 0) {
      time = "12" + ":" + formattedMinute + " AM";
    } else if (hour < 12) {
      time = hour + ":" + formattedMinute + " AM";
    } else if (hour == 12) {
      time = "12" + ":" + formattedMinute + " PM";
    } else {
      int temp = hour - 12;
      time = temp + ":" + formattedMinute + " PM";
    }

    return time;
  }

  private void scheduleNotification(Notification notification, String date, String time) {
    Intent notificationIntent = new Intent(this, NotificationPublisher.class);
    notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
    notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

    Random random = new Random();

    int number = random.nextInt(10000);

    String dateandtime = date + " " + timeTonotify;
    DateFormat formatter = new SimpleDateFormat("d-M-yyyy hh:mm");

    PendingIntent pendingIntent =
        PendingIntent.getBroadcast(this, number, notificationIntent, PendingIntent.FLAG_MUTABLE);
    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    assert alarmManager != null;
    try {
      Date date1 = formatter.parse(dateandtime);
      alarmManager.set(AlarmManager.RTC_WAKEUP, date1.getTime(), pendingIntent);
      Toast.makeText(getApplicationContext(), "Alaram", Toast.LENGTH_SHORT).show();

    } catch (ParseException e) {
      e.printStackTrace();
    }

    Intent intentBack = new Intent(getApplicationContext(), MainActivity.class);
    intentBack.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(intentBack); // navigates from adding reminder activity ot mainactivity
  }

  private Notification getNotification(String content) {
    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(this, default_notification_channel_id);

    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    builder.setContentTitle("Reminder");
    builder.setSmallIcon(R.drawable.alaram);
    builder.setContentText("ToDo: " + content);
    builder.setAutoCancel(true);
    builder.setOngoing(true);
    builder.setAutoCancel(true);
    builder.setPriority(Notification.PRIORITY_HIGH);
    builder.setOnlyAlertOnce(true);
    builder.setSound(alarmSound);
    builder.setAutoCancel(true);
    builder.setChannelId(NOTIFICATION_CHANNEL_ID);
    return builder.build();
  }
}
