package rocks.magical.camunda.helper;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import rocks.magical.camunda.helper.data.LocationContainer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LocationService {
    public static LocationContainer getLocationName(Double lat, Double lng) {
        Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();
        try {
            URL url = new URL("https://nominatim.openstreetmap.org/reverse?lat=" + lat + "&lon=" + lng + "&format=json");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setInstanceFollowRedirects(true);
            int status = con.getResponseCode();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();

            return gson.fromJson(content.toString(), LocationContainer.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
