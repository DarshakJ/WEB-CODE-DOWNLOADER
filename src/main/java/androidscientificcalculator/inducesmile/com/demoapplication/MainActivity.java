package androidscientificcalculator.inducesmile.com.demoapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.icu.text.UnicodeSetSpanner;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    Button getCode,create_file;
    EditText link_url,data_content,file_name;
    TextView txt_display;


    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            Log.i("URL",strings[0]);


            HttpURLConnection urlConnection= null;
            try{
                String result="";
                URL url;
                char current;
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in= urlConnection.getInputStream();
                InputStreamReader reader= new InputStreamReader(in);
                int data = reader.read();

                while (data != -1)
                {
                    current=(char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            }
            catch (Exception e) {
                Log.i("Eroor", e.getMessage());
                return "Problem Occured";
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //grant permission
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE )
                != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1000);
        }

        getCode= (Button) findViewById(R.id.get_code);
        create_file = (Button) findViewById(R.id.create_file);
        //Edittext
        link_url = (EditText) findViewById(R.id.wb_link);
        data_content=(EditText) findViewById(R.id.data);
        file_name=(EditText) findViewById(R.id.file_name);
        //Textview
        txt_display=(TextView) findViewById(R.id.confirm_data);

        getCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!link_url.getText().toString().isEmpty())
                {
                    try
                    {
                        DownloadTask task = new DownloadTask();
                        String inp;
                        inp=task.execute(link_url.getText().toString()).get();
                        if(!inp.equals("Problem Occured")) {
                            data_content.setText(inp);
                            file_name.setText("demo1");
                            txt_display.setVisibility(View.VISIBLE);
                            data_content.setVisibility(View.VISIBLE);
                            file_name.setVisibility(View.VISIBLE);
                            create_file.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            txt_display.setText("Problem Occured \nGive Proper URL");
                        }
                        txt_display.setVisibility(View.VISIBLE);
                    }
                    catch (Exception e)
                    {
                        txt_display.setVisibility(View.VISIBLE);
                        Log.i("Error",e.getMessage());
                        Toast.makeText(getApplicationContext(),"Some problem occured",Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Enter link",Toast.LENGTH_SHORT).show();
                }


            }
        });

        create_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String filename =  file_name.getText().toString();
               String content =  data_content.getText().toString();
               if(!filename.isEmpty()) {
                   saveTextAsFile(filename, content);
               }
               else
               {
                   Toast.makeText(getApplicationContext(),"Enter file name",Toast.LENGTH_SHORT).show();
               }
            }
        });

    }

    private void saveTextAsFile(String filename,String content)
    {
        filename += ".html";
        File file =new File(Environment.getExternalStorageDirectory().getAbsolutePath(),filename);
        try {

            FileOutputStream fileOutputStream= new FileOutputStream(file);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.close();
            Toast.makeText(getApplicationContext(),"Code Saved",Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Log.i("Error",e.getMessage());
            Toast.makeText(getApplicationContext(),"Error Occured",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1000:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(this,"Permission granted",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(this,"Permission notgranted",Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
}
