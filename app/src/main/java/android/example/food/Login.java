package android.example.food;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Login extends AppCompatActivity {

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;
    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.username);
        etPassword = findViewById(R.id.password);
    }

    public void checkLogin(View arg0) {
        final String email = etEmail.getText().toString();
        final String password = etPassword.getText().toString();
        new AsyncLogin().execute(email, password);
    }

    private class AsyncLogin extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(Login.this);
        HttpURLConnection conn;
        URL url = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                url = new URL("http://192.168.120.74/appdrive/login.php");
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "exception";
            }

            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("username", params[0])
                        .appendQueryParameter("password", params[1]);
                String query = builder.build().getEncodedQuery();

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

            } catch (IOException e1) {
                e1.printStackTrace();
                return "exception";
            }

            try {
                int response_code = conn.getResponseCode();
                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    return result.toString();
                } else {
                    return "unsuccessful";
                }

            } catch (IOException e) {
                e.printStackTrace();
                return "exception";

            } finally {
                conn.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            pdLoading.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");

                if (status.equalsIgnoreCase("true")) {
                    int role = jsonObject.getInt("role");
                    if (role == 1) {
                        Intent intent = new Intent(Login.this, admin.class);
                        startActivity(intent);
                    } else if (role == 2) {
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                    }
                    Login.this.finish();
                } else if (status.equalsIgnoreCase("false")) {
                    Toast.makeText(Login.this, "Email atau Password tidak valid", Toast.LENGTH_LONG).show();
                } else if (status.equalsIgnoreCase("exception") || status.equalsIgnoreCase("unsuccessful")) {
                    Toast.makeText(Login.this, "Oops! Sepertinya koneksi sedang bermasalah.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(Login.this, "JSON parsing error", Toast.LENGTH_LONG).show();
            }
        }
    }
}
