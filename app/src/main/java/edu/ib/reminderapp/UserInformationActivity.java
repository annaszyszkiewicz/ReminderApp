package edu.ib.reminderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * klasa obslugujaca okno obliczajace ilosc wody, ktora powinien pic uzytkownik
 */
public class UserInformationActivity extends AppCompatActivity {

    /**
     * przyslonieta metoda określająca działanie aplikacji od razu po jej uruchomieniu
     * odczytuje z pliku informacje o uzytkowniku, by nie musial on ustawiac ich po kazdym uruchomieniu aplikacji
     *
     * @param savedInstanceState zachowanie danych aplikacji
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_user_information);

        Spinner sex = (Spinner) findViewById(R.id.selectSex);
        Spinner age = (Spinner) findViewById(R.id.selectAge);
        Spinner physicalActivity = (Spinner) findViewById(R.id.selectPhysicalActivity);
        Spinner pregnant = (Spinner) findViewById(R.id.selectPregnant);
        Spinner breastfeeding = (Spinner) findViewById(R.id.selectBreastfeeding);

        TextView txtBreastfeeding = (TextView) findViewById(R.id.txtBreastfeeding);
        TextView txtPregnant = (TextView) findViewById(R.id.txtPregnant);

        TextView dailyAmountOfWater = (TextView) findViewById(R.id.txtDailyAmountOfWater);

        File file = new File(getExternalFilesDir(MainActivity.FOLDERNAME_SAVE), MainActivity.FILENAME_SAVE);

        try (FileInputStream is = new FileInputStream(file)) {
            int n = is.read();
            StringBuilder fileString = new StringBuilder();
            while (n != -1) {
                fileString.append((char) n);
                n = is.read();
            }

            String[] element = fileString.toString().split(",");

            sex.setSelection(Integer.parseInt(element[0]));
            age.setSelection(Integer.parseInt(element[1]));
            physicalActivity.setSelection(Integer.parseInt(element[2]));
            pregnant.setSelection(Integer.parseInt(element[3]));
            breastfeeding.setSelection(Integer.parseInt(element[4]));
            dailyAmountOfWater.setText(getString(R.string.drink) + element[5] + getString(R.string.drink2));

        } catch (IOException e) {
            Log.e(MainActivity.TAG, e.toString());
        }

        //sprawdzanie, co zostało wybrane w spinnerze przy każdym wybieraniu
        //i odpowiednio ukrywanie oraz odkrywanie spinnerów związanych z ciążą i karmieniem dziecka
        sex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (sex.getSelectedItemPosition() == 1) {
                    pregnant.setVisibility(View.INVISIBLE);
                    breastfeeding.setVisibility(View.INVISIBLE);
                    txtBreastfeeding.setVisibility(View.INVISIBLE);
                    txtPregnant.setVisibility(View.INVISIBLE);
                    pregnant.setSelection(0);
                    breastfeeding.setSelection(0);
                } else {
                    pregnant.setVisibility(View.VISIBLE);
                    breastfeeding.setVisibility(View.VISIBLE);
                    txtBreastfeeding.setVisibility(View.VISIBLE);
                    txtPregnant.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    /**
     * metoda wywolywana po kliknieciu przycisku "OBLICZ"
     * oblicz ilosc potrzebnej uzytkownikowi wody i zapisuje wybrane przez uzytkownika informacje do pliku
     *
     * @param view obiekt wywolujacy metode
     */
    public void onClickCalculate(View view) {
        Spinner sex = (Spinner) findViewById(R.id.selectSex);
        Spinner age = (Spinner) findViewById(R.id.selectAge);
        Spinner physicalActivity = (Spinner) findViewById(R.id.selectPhysicalActivity);
        Spinner pregnant = (Spinner) findViewById(R.id.selectPregnant);
        Spinner breastfeeding = (Spinner) findViewById(R.id.selectBreastfeeding);

        TextView dailyAmountOfWater = (TextView) findViewById(R.id.txtDailyAmountOfWater);

        Log.d(MainActivity.TAG, sex.getSelectedItem().toString());
        Log.d(MainActivity.TAG, age.getSelectedItem().toString());
        Log.d(MainActivity.TAG, physicalActivity.getSelectedItem().toString());
        Log.d(MainActivity.TAG, pregnant.getSelectedItem().toString());
        Log.d(MainActivity.TAG, breastfeeding.getSelectedItem().toString());

        int dailyAmount = 2000;

        switch (sex.getSelectedItemPosition()) {
            case 0:
                switch (age.getSelectedItemPosition()) {
                    case 0:
                        dailyAmount = 2000;
                        break;
                    case 1:
                        dailyAmount = 1950;
                        break;
                    case 2:
                        dailyAmount = 1900;
                        break;
                    case 3:
                        dailyAmount = 1750;
                        break;
                    case 4:
                        dailyAmount = 1600;
                        break;
                    case 5:
                        dailyAmount = 1250;
                        break;
                    case 6:
                        dailyAmount = 1000;
                        break;
                }
                if (pregnant.getSelectedItemPosition() == 2) {
                    dailyAmount += 200;
                }
                if (breastfeeding.getSelectedItemPosition() == 2) {
                    dailyAmount += 200;
                }
                break;

            case 1:
                switch (age.getSelectedItemPosition()) {
                    case 0:
                        dailyAmount = 2500;
                        break;
                    case 1:
                        dailyAmount = 2350;
                        break;
                    case 2:
                        dailyAmount = 2100;
                        break;
                    case 3:
                        dailyAmount = 1750;
                        break;
                    case 4:
                        dailyAmount = 1600;
                        break;
                    case 5:
                        dailyAmount = 1250;
                        break;
                    case 6:
                        dailyAmount = 1000;
                        break;
                }
                break;
        }

        if (physicalActivity.getSelectedItemPosition() == 2) {
            dailyAmount += 500;
        }

        dailyAmountOfWater.setText(getString(R.string.drink) + dailyAmount + getString(R.string.drink2));

        String information = sex.getSelectedItemPosition() + "," + age.getSelectedItemPosition() + "," + physicalActivity.getSelectedItemPosition() +
                "," + pregnant.getSelectedItemPosition() + "," + breastfeeding.getSelectedItemPosition() + "," + dailyAmount;

        Log.d(MainActivity.TAG, information);

        File myExternalFile = new File(getExternalFilesDir(MainActivity.FOLDERNAME_SAVE), MainActivity.FILENAME_SAVE);

        try (FileOutputStream os = new FileOutputStream(myExternalFile)) {
            os.write(information.getBytes());
            os.close();
        } catch (IOException e) {
            Log.e(MainActivity.TAG, e.toString());
        }

    }


}