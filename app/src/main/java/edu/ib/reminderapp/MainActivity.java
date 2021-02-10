package edu.ib.reminderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;

/**
 * klasa obslugujaca aktywnosc glownego okna aplikacji
 */
public class MainActivity extends AppCompatActivity {

    public static final String FOLDERNAME_SAVE = "ReminderApp";
    public static final String FILENAME_SAVE = "UserInfo.txt";
    public static final String FILENAME_SAVE_BUTTON = "ButtonStan.txt";
    public static final String TAG = "EDUIB";


    /**
     * przyslonieta metoda określająca działanie aplikacji od razu po jej uruchomieniu
     * odczytuje informacje zapisane w pliku i na ich podstawie ustawia informacje na przycisku
     * przy każdym uruchomieniu losuje przydatna ciekawostke
     *
     * @param savedInstanceState zachowanie danych aplikacji
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        Button btnReminder = (Button) findViewById(R.id.btnDrinkWaterReminder);
        TextView txtInfo = (TextView) findViewById(R.id.txtInfo);

        File buttonFile = new File(getExternalFilesDir(MainActivity.FOLDERNAME_SAVE), MainActivity.FILENAME_SAVE_BUTTON);
        try (FileInputStream is = new FileInputStream(buttonFile)) {
            int n = is.read();
            StringBuilder fileString = new StringBuilder();
            while (n != -1) {
                fileString.append((char) n);
                n = is.read();
            }
            if (fileString.toString().equals("1"))
                btnReminder.setText(getString(R.string.btnDrinkWaterReminderOFF));
            else
                btnReminder.setText(getString(R.string.btnDrinkWaterReminder));

        } catch (IOException e) {
            Log.e(MainActivity.TAG, e.toString());
        }

        Random random = new Random();
        int randomFile = random.nextInt(4);

        switch (randomFile) {
            case 0:
                txtInfo.setText(getString(R.string.txtDehydrationInfo));
                break;
            case 1:
                txtInfo.setText(getString(R.string.txtDietInfo));
                break;
            case 2:
                txtInfo.setText(getString(R.string.txtTasteInfo));
                break;
            case 3:
                txtInfo.setText(getString(R.string.txtClimateInfo));
                break;
        }

    }

    /**
     * metoda wywolywana po kliknieciu przycisku "O TOBIE"
     * przenosi użytkownika do okna aplikacji obliczajacej potrzebna ilosc wody
     *
     * @param view obiekt wywolujacy metode
     */
    public void onClickAboutYou(View view) {
        Intent intent = new Intent(this, UserInformationActivity.class);
        startActivity(intent);
    }

    /**
     * metoda wywoływana po kliknieciu przycisku "WLACZ PRZYPOMNIENIE O PICIU WODY" lub "WYLACZ PRZYPOMNIENIE O PICIU WODY"
     * wlacza lub wylacza przypomnienia o piciu wody i ustawia, by pojawialy sie co jakis czas
     * zapisuje w pliku, czy powiadomienia o piciu wody zostaly wlaczone, czy wylaczone
     * informuje o tym, czy zostaly wlaczone lub wylaczone powiadomienia
     *
     * @param view obiekt wywolujacy metode
     */
    public void onClickReminder(View view) {
        Button btnReminder = (Button) findViewById(R.id.btnDrinkWaterReminder);
        String buttonText = "";

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 20);

        Intent intent = new Intent(getApplicationContext(), Alert.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (btnReminder.getText().toString().equals(getString(R.string.btnDrinkWaterReminder))) {
            Toast.makeText(this, "Włączono przypomnienia", Toast.LENGTH_LONG).show();
            btnReminder.setText(getString(R.string.btnDrinkWaterReminderOFF));

            Calendar time = Calendar.getInstance();
            int millis = 0;
            if (time.get(Calendar.HOUR_OF_DAY) > 22 || time.get(Calendar.HOUR_OF_DAY) < 7)
                millis = 1000 * 60 * 60 * 10;
            else
                millis = 1000 * 60 * 30;

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), millis, pendingIntent);

            buttonText = "1";

        } else {
            Toast.makeText(this, "Wyłączono przypomnienia", Toast.LENGTH_LONG).show();
            btnReminder.setText(getString(R.string.btnDrinkWaterReminder));
            alarmManager.cancel(pendingIntent);
            buttonText = "2";
        }

        File buttonFile = new File(getExternalFilesDir(MainActivity.FOLDERNAME_SAVE), MainActivity.FILENAME_SAVE_BUTTON);
        if (!buttonFile.exists()) {
            try {
                buttonFile.createNewFile();
            } catch (IOException e) {
                Log.e(MainActivity.TAG, e.toString());
            }
        }

        try (FileOutputStream os = new FileOutputStream(buttonFile)) {
            os.write(buttonText.getBytes());
            os.close();
        } catch (IOException e) {
            Log.e(MainActivity.TAG, e.toString());
        }

    }

}