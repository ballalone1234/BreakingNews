package com.example.breakingnews;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayAdapter<News> aa;
    ArrayList<News> news = new ArrayList<>();
    private static final String TAG = "EARTHQUAKE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new
                StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        listView = (ListView)this.findViewById(R.id.list);
        int layoutID = android.R.layout.simple_list_item_1;
        aa = new ArrayAdapter<>(this, layoutID , news);
        listView.setAdapter(aa);
        refreshEarthquakes();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                AlertDialog.Builder adb = new AlertDialog.Builder(
                        MainActivity.this);
                adb.setTitle(parent.getItemAtPosition(position).toString());
                adb.setMessage(news.get(position).getDetails());
                adb.setPositiveButton("Ok", null);
                adb.show();
            }
        });

    }
    private void refreshEarthquakes() {
        // Get the XML
        URL url;
        try {
            String quakeFeed = "https://mgronline.com/store/sitemap/sitemap-global.xml";
            url = new URL(quakeFeed);
            URLConnection connection;
            connection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream in = httpConnection.getInputStream();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                // Parse the earthquake feed.
                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();
                // Clear the old earthquakes
                news.clear();
                // Get a list of each earthquake entry.
                NodeList nl = docEle.getElementsByTagName("url");
                if (nl != null && nl.getLength() > 0) {
                    for (int i = 0 ; i < nl.getLength(); i++) {
                        Element entry = (Element)nl.item(i);
                        Element im = (Element)entry.getElementsByTagName("image:image").item(0);

                        String title = im.getElementsByTagName("image:title").item(0).getFirstChild().getNodeValue();
                        String details = im.getElementsByTagName("image:caption").item(0).getFirstChild().getNodeValue();

                        News quake = new News(title, details);
                        addNews(quake);
                    }
                }
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, "MalformedURLException", e);
        } catch (IOException e) {
            Log.d(TAG, "IOException", e);
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            Log.d(TAG, "Parser Configuration Exception", e);
        } catch (SAXException e) {
            Log.d(TAG, "SAX Exception", e);
        }
        finally {

        }
    }

    private void addNews(News _quake) {
        news.add(_quake); // Add the new quake to our list of earthquakes.
        aa.notifyDataSetChanged(); // Notify the array adapter of a change.
    }
}

