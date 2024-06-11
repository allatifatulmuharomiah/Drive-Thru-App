package android.example.food;

import android.example.food.model.MenuItem;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Menu extends AppCompatActivity {

    private List<MenuItem> menuItems;
    private RecyclerView recyclerView;
    private MenuAdapter menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        menuItems = new ArrayList<>();
        menuAdapter = new MenuAdapter(menuItems);
        recyclerView.setAdapter(menuAdapter);

        new FetchMenuTask().execute("http://192.168.120.74//appdrive/getMenu.php");
    }

    private class FetchMenuTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response += line;
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject menuItem = jsonArray.getJSONObject(i);

                    int id = menuItem.getInt("menu_id");
                    String name = menuItem.getString("menu_name");
                    String description = menuItem.getString("description");
                    double price = menuItem.getDouble("price");

                    menuItems.add(new MenuItem(id, name, description, price));
                }
                // Update UI with menuItems list
                displayMenuItems();
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(Menu.this, "Error parsing data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayMenuItems() {
        menuAdapter.notifyDataSetChanged();
    }
}