package be.ucll.java.mobile.ucllstorage;

import androidx.appcompat.app.AppCompatActivity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private Button btnSave, btnLoad;
    private RadioButton radPrefs, radFile, radDatabase;
    private RadioGroup radGrStorage;
    private EditText txtInput;
    private TextView txtOutput;
    private static final String TAG = "LOGGINGUCLL";
    SharedPreferences pref;
    myDbAdapter helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        helper = new myDbAdapter(this);
        pref = getApplicationContext().getSharedPreferences("PrefContent", 0);
        setContentView(R.layout.activity_main);
        btnSave = this.findViewById(R.id.btnSave);
        btnLoad = this.findViewById(R.id.btnLoad);
        radPrefs = this.findViewById(R.id.radPrefs);
        radFile = this.findViewById(R.id.radFile);
        radDatabase = this.findViewById(R.id.radDatabase);
        radGrStorage = this.findViewById(R.id.radGrStorage);
        txtInput = this.findViewById(R.id.txtInput);
        txtOutput = this.findViewById(R.id.txtOutput);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    buttonDo(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    buttonDo(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void buttonDo(boolean doSaved) throws IOException {
        Log.d(TAG, "buttonDo: started with "+radGrStorage.getCheckedRadioButtonId());
        if(doSaved && txtInput.getText().toString().equals("")){
            return;
        }
        if(radGrStorage.getCheckedRadioButtonId() == radPrefs.getId()){ //preferences
            Log.d(TAG, "buttonDo: preferences started");
            SharedPreferences.Editor editor = pref.edit();
            if(doSaved){ //saving preferences
                Log.d(TAG, "buttonDo: preferences save started");
                editor.putString("PrefContent",txtInput.getText().toString());
                Log.d(TAG, "buttonDo: \""+txtInput.getText().toString()+"\" saved in PrefContent");
                editor.commit();
            }else{ //loading preferences
                Log.d(TAG, "buttonDo: prefreneces load started");
                txtOutput.setText(pref.getString("PrefContent","Preferences undefined"));
                Log.d(TAG, "buttonDo: \""+pref.getString("PrefContent","Preferences undefined")+"\" loaded");
            }
        }else if(radGrStorage.getCheckedRadioButtonId() == radFile.getId()){
            Log.d(TAG, "buttonDo: File started");
            File directory = getFilesDir();
            File file = new File(directory,"FileContent.txt");
            if(doSaved){
                Log.d(TAG, "buttonDo: File saving started");
                if(!file.exists()){
                    file.createNewFile();
                    Log.d(TAG, "buttonDo: created a new File");
                }
                FileWriter filewrite = new FileWriter(file.getPath());
                filewrite.write(txtInput.getText().toString());
                Log.d(TAG, "buttonDo: wrote \""+txtInput.getText().toString()+"\" in file");
                filewrite.close();
            }else{
                Log.d(TAG, "buttonDo: file save started");
                if(!file.exists()){
                    txtOutput.setText("Bestand FileContent.txt niet gevonden");
                    txtInput.setText("");
                    return;
                }
                Scanner fileScanner = new Scanner(file);
                StringBuilder str_output = new StringBuilder();
                while(fileScanner.hasNextLine()){
                    str_output.append(fileScanner.nextLine());
                }
                fileScanner.close();
                txtOutput.setText(str_output.toString());
                Log.d(TAG, "buttonDo: loaded file \""+str_output.toString()+"\"");

            }
        }else if(radGrStorage.getCheckedRadioButtonId() == radDatabase.getId()){
            Log.d(TAG, "buttonDo: database start");
            if(doSaved){
                Log.d(TAG, "buttonDo: database save start");
                String str_ = txtInput.getText().toString();
                Log.d(TAG, "buttonDo: "+str_+"=> updating to db");
                helper.insertData(str_);
            }else{
                Log.d(TAG, "buttonDo: database load start");
                txtOutput.setText(helper.getData());
            }
        }
        if(doSaved){
            txtInput.setText("");
        }
    }
}