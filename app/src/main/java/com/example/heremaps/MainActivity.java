package com.example.heremaps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.BitmapFactory;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.heremaps.Adapters.CompleteTextAdapter;
import com.example.heremaps.Common.Common;
import com.example.heremaps.Model.Coordinates;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.offroute.OffRouteListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;


public class MainActivity extends AppCompatActivity implements MapboxMap.OnMapClickListener, MapboxMap.OnMarkerClickListener, PermissionsListener, OffRouteListener {

    boolean onClicked = false;

    private MapView mapView;
    private static MapboxMap mapbox;
    public static LatLng currentPostion = Common.city_info.getCenter();
    static String name_of_place;
    String address;
    static LatLng destination;
    LatLng center = Common.city_info.getCenter();

    MarkerOptions destination_marker = new MarkerOptions();

    AutoCompleteTextView search;

    protected LocationManager locationManager;

    List<String> list = new ArrayList<>();
    List<Coordinates> coordinates_arr = new ArrayList<>();

    CompleteTextAdapter adapter;

    private BuildingPlugin buildingPlugin;

    int count = 0;

    private PermissionsManager permissionsManager;

    private LocationComponent locationComponent;

    String url = "https://nominatim.openstreetmap.org/search?q=[pharmacy]&format=json&limit=50&viewbox=76.7382789,%2043.0332628,%2077.1672819&bounded=1";
    String url_map_click = "https://nominatim.openstreetmap.org/reverse?=&format=json&lat=43.2415338533319&lon=76.94535453478898";

    BottomNavigationView botNav;

    ImageButton down_arrow;

    TextView name, address_text_view;

    private DirectionsRoute currentRoute;

    private NavigationMapRoute navigationMapRoute;
    MapboxNavigation navigation;

    TextView distance;

    private BottomSheetBehavior mBottomSheetBehaviour;

    String search_text = "";

    Button button;

    ImageButton my_positon;


    private OfflineManager offlineManager;
    private OfflineRegion offlineRegion;

    Button driving,cycling,wakling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        Mapbox.setAccessToken(getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_main);
        search = findViewById(R.id.search);
        name = findViewById(R.id.name);
        distance = findViewById(R.id.distance);
        button = findViewById(R.id.go);
        address_text_view = findViewById(R.id.address);
        ImageButton button = findViewById(R.id.btn_srch);
        down_arrow = findViewById(R.id.down_arrow);
        botNav = findViewById(R.id.bottom_nav);
        navigation = new MapboxNavigation(MainActivity.this, getString(R.string.mapbox_access_token));
        botNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.search:
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.from:
                        currentPostion = destination;
                        Log.d("current_pos", currentPostion + "");
                        Log.d("current_pos", destination + "");
                        break;
                    case R.id.here:
                        getRoute(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC);
                        break;
                }
                return false;
            }
        });
        offlineManager = OfflineManager.getInstance(MainActivity.this);
        Button down = findViewById(R.id.download);
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOnline()) {
                    buildAlertMessageDownload();
                }else{
                    Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (search.getText().toString().equals("")) {
                    mapbox.clear();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent intent = new Intent(MainActivity.this, SearchActivity.class);
               // startActivity(intent);
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLngBounds bounds = mapbox.getProjection().getVisibleRegion().latLngBounds;
                search_text = search.getText().toString();
                url = "https://nominatim.openstreetmap.org/search?q=" +
                        search.getText().toString() + "&format=json&limit=50&viewbox=" + bounds.getNorthEast().getLongitude() + "," + bounds.getNorthEast().getLatitude()
                        + "," + bounds.getSouthWest().getLongitude() + "," + bounds.getSouthWest().getLatitude() + "&bounded=1&namedetails=1&addressdetails=1";
                new JsonTask()
                        .execute(url);
            }
        });
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        locationManager = (LocationManager) getSystemService(MainActivity.LOCATION_SERVICE);
        my_positon = findViewById(R.id.my_pos);
        my_positon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    enableLocationComponent(mapbox.getStyle());
                    if(locationComponent.getLastKnownLocation()!=null) {
                        currentPostion = new LatLng(locationComponent.getLastKnownLocation().getLatitude(), locationComponent.getLastKnownLocation().getLongitude());
                        CameraPosition position = new CameraPosition.Builder()
                                .target(currentPostion)
                                .zoom(14)
                                .tilt(20)
                                .build();
                        mapbox.setCameraPosition(position);
                    }
                } else {
                    buildAlertMessageNoGps();
                }
            }
        });
        checkMap();
       // openMap();
        View nestedScrollView = findViewById(R.id.nestedScrollView);
        mBottomSheetBehaviour = BottomSheetBehavior.from(nestedScrollView);
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        botNav.setVisibility(View.GONE);
        down_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String message = bundle.getString("message");
            if (message != null) {
                search_text = message;
                Log.d("message",message);
                LatLngBounds bounds = mapbox.getProjection().getVisibleRegion().latLngBounds;
                url = "https://nominatim.openstreetmap.org/search?q=" +
                        message + "&format=json&limit=50&viewbox=" + bounds.getNorthEast().getLongitude() + "," + bounds.getNorthEast().getLatitude()
                        + "," + bounds.getSouthWest().getLongitude() + "," + bounds.getSouthWest().getLatitude() + "&bounded=1&namedetails=1&addressdetails=1";
                new JsonTask()
                        .execute(url);
            }
        }


        driving = findViewById(R.id.driving);
        cycling = findViewById(R.id.cycling);
        wakling = findViewById(R.id.walking);

    }
    public static boolean performClick(View view) {
        return view.isEnabled() && view.isClickable() && view.performClick();
    }

    public void OnClick(View v){
        switch (v.getId()){
            case R.id.driving:
                getRoute(DirectionsCriteria.PROFILE_DRIVING);
                break;
            case R.id.cycling:
                getRoute(DirectionsCriteria.PROFILE_CYCLING);
                break;
            case R.id.walking:
                getRoute(DirectionsCriteria.PROFILE_WALKING);
                break;
        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        enableLocationComponent(mapbox.getStyle());
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    ProgressDialog progressDialog;

    private void buildAlertMessageDownload() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Download Map?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        if(isOnline()) {
                            progressDialog = new ProgressDialog(MainActivity.this);
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                            getOfflineMap();
                        }else{
                            Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        finish();
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
        } else {
            url_map_click = "https://nominatim.openstreetmap.org/reverse?q=1&lat=" + point.getLatitude() + "&lon=" + point.getLongitude() + "&format=json&email=almasnurlanov16@gmail.com&namedetails=1&addressdetails=1&extratags=1";
            new JsonTask()
                    .execute(url_map_click);
            distance.setText((int) point.distanceTo(currentPostion) + "m");
            mapbox.clear();
            // enableLocationComponent(mapbox.getStyle());
            destination = point;
            //getRoute();
            //navigationMapRoute.removeRoute();

            onClicked = true;
            button.setEnabled(true);
            button.setBackgroundResource(R.color.mapboxBlue);

            //name.setText(name_of_place);
            //botNav.inflateMenu(R.menu.menu_on_click_marker);
            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            botNav.setVisibility(View.VISIBLE);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.title(name.getText().toString());
            markerOptions.position(point);
            mapbox.addMarker(markerOptions);
        }
        return true;
    }

    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        String s = "You";
        if (!marker.getTitle().equals(s)) {
            destination = new LatLng(marker.getPosition().getLatitude(), marker.getPosition().getLongitude());
            //getRoute();
            String[] separated = marker.getTitle().split(",");
            destination_marker.setTitle(separated[0]);
            destination_marker.position(destination);
            mapbox.addMarker(destination_marker);
            if (separated.length == 2) {
                name.setText(separated[0]);
                address_text_view.setText(separated[1]);
            } else {
                name.setText(separated[0]);
                address_text_view.setText("");
            }
            botNav.setVisibility(View.VISIBLE);
            mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
            distance.setText((int) destination.distanceTo(currentPostion) + "m");
            button.setEnabled(true);
            button.setBackgroundResource(R.color.mapboxBlue);
        }
        return false;
    }
    Boolean check;
    public void checkMap(){
        offlineManager.listOfflineRegions(new OfflineManager.ListOfflineRegionsCallback() {
            @Override
            public void onList(OfflineRegion[] offlineRegions) {
                if (offlineRegions == null || offlineRegions.length == 0) {
                    buildAlertMessageDownload();
                    return;
                }else{
                    for(int i =0;i<offlineRegions.length;i++){
                        if(getRegionName(offlineRegions[i]).equals(Common.name)){
                            check = true;
                            break;
                        }else {
                            check = false;
                        }
                    }
                    if(!check){
                        buildAlertMessageDownload();
                    }else{
                        openMap();
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }
    @SuppressLint("StringFormatInvalid")
    private String getRegionName(OfflineRegion offlineRegion) {
        // Get the region name from the offline region metadata
        String regionName;

        try {
            byte[] metadata = offlineRegion.getMetadata();
            String json = new String(metadata, JSON_CHARSET);
            JSONObject jsonObject = new JSONObject(json);
            regionName = jsonObject.getString(JSON_FIELD_REGION_NAME);
        } catch (Exception exception) {

            regionName = String.format("asdasd", offlineRegion.getID());
        }
        return regionName;
    }
    public void getOfflineMap() {
        String styleUrl = "mapbox://styles/mapbox/traffic-day-v2";
//        Log.d("style",mapbox.getStyle()+" "+styleUrl);
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(Common.city_info.getFirst_point())
                .include(Common.city_info.getSecond_point())
                .build();
        double minZoom = 1;
        double maxZoom = 18;
        float pixelRatio = this.getResources().getDisplayMetrics().density;
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
                styleUrl, bounds, minZoom, maxZoom, pixelRatio);
        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(JSON_FIELD_REGION_NAME, Common.name);
            String json = jsonObject.toString();
            metadata = json.getBytes(JSON_CHARSET);
        } catch (Exception exception) {

            metadata = null;
        }
        offlineManager.createOfflineRegion(definition, metadata, new OfflineManager.CreateOfflineRegionCallback() {
            @Override
            public void onCreate(OfflineRegion offlineRegion) {

                MainActivity.this.offlineRegion = offlineRegion;
                launchDownload();
            }

            @Override
            public void onError(String error) {


            }
        });
    }

    private void launchDownload() {
// Set up an observer to handle download progress and
// notify the user when the region is finished downloading
        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
            @Override
            public void onStatusChanged(OfflineRegionStatus status) {
// Compute a percentage
                double percentage = status.getRequiredResourceCount() >= 0
                        ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                        0.0;
                Log.d("status", "" + percentage);
                if ((int)percentage != 0) {
                    progressDialog.setProgress((int)percentage);
                }
                if (status.isComplete()) {
// Download complete
                    openMap();
                    Log.d("status", "complete");
                    progressDialog.dismiss();
                    return;
                } else if (status.isRequiredResourceCountPrecise()) {
// Switch to determinate state
                    // Log.d("status","isRequi");
                }

// Log what is being currently downloaded

            }

            @Override
            public void onError(OfflineRegionError error) {
                Log.d("errorPost", error.getMessage());
            }

            @Override
            public void mapboxTileCountLimitExceeded(long limit) {
                Log.d("Limit", limit + "");
            }
        });

// Change the region state
        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
    }
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    private void getRoute(String type) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(Point.fromLngLat(currentPostion.getLongitude(), currentPostion.getLatitude()))
                .destination(Point.fromLngLat(destination.getLongitude(), destination.getLatitude()))
                .alternatives(true)
                .profile(type)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null) {

                            return;
                        } else if (response.body().routes().size() < 1) {

                            return;
                        }
                        currentRoute = response.body().routes().get(0);

                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(navigation, mapView, mapbox, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addProgressChangeListener(navigation);
                        navigationMapRoute.showAlternativeRoutes(true);
                        navigationMapRoute.addRoute(currentRoute);

                        distance.setText(currentRoute.distance().intValue() + "m");

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                    }
                });
        navigation.addOffRouteListener(this);
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            locationComponent = mapbox.getLocationComponent();
            locationComponent.activateLocationComponent(this, loadedMapStyle);
            locationComponent.setCameraMode(CameraMode.TRACKING);
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationComponent.setLocationComponentEnabled(true);
                currentPostion = new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                        locationComponent.getLastKnownLocation().getLongitude());
            }
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, "Need permission", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            if (mapbox.getStyle() != null) {
                enableLocationComponent(mapbox.getStyle());
            }
        } else {
            Toast.makeText(this, "No permission", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void userOffRoute(Location location) {
        Toast.makeText(this, "off-route called", Toast.LENGTH_LONG).show();
    }

    @SuppressLint("StaticFieldLeak")
    private class JsonTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
//                int status = connection.getResponseCode();
  //              Log.d("asd",status+"");
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {

                if (s.charAt(0) != '[') {
                    s = "[" + s + "]";
                }
                coordinates_arr.clear();
                list.clear();
                JSONArray jsonResponse = null;
                JSONObject getSelected = null;
                String names = null;
                address = "";
                double lon = 0;
                double lat = 0;
                try {
                    jsonResponse = new JSONArray(s);

                    for (int i = 0; i < jsonResponse.length(); i++) {
                        getSelected = (JSONObject) jsonResponse.get(i);
                        Log.d("qwert", getSelected+"");
                        names = (String) getSelected.get("display_name");
                        lon = getSelected.getDouble("lon");
                        lat = getSelected.getDouble("lat");
                        Coordinates cor = new Coordinates();
                        if (getSelected.has("address")) {

                            Iterator<?> keys = getSelected.getJSONObject("address").keys();
                            List<String> ar = new ArrayList<>();
                            while (keys.hasNext()) {
                                ar.add((String) keys.next());
                            }
                            if (getSelected.getJSONObject("namedetails").length() != 0 & !getSelected.getJSONObject("namedetails").isNull("name")) {
                                if (ar.contains("road")) {
                                    address = getSelected.getJSONObject("address").getString("road");
                                    cor.setAddress(getSelected.getJSONObject("address").getString("road"));
                                } else if (ar.contains("suburb")) {
                                    address = getSelected.getJSONObject("address").getString("suburb");
                                    cor.setAddress(getSelected.getJSONObject("address").getString("suburb"));
                                } else if (ar.contains("neighbourhood")) {
                                    address = getSelected.getJSONObject("address").getString("neighbourhood");
                                    cor.setAddress(getSelected.getJSONObject("address").getString("neighbourhood"));
                                }
                                name_of_place = getSelected.getJSONObject("namedetails").getString("name");
                                cor.setName(getSelected.getJSONObject("namedetails").getString("name"));
                            } else {
                                if (ar.contains("road")) {
                                    name_of_place = getSelected.getJSONObject("address").getString("road");
                                    cor.setName(getSelected.getJSONObject("address").getString("road"));
                                } else if (ar.contains("suburb")) {
                                    name_of_place = getSelected.getJSONObject("address").getString("suburb");
                                    cor.setName(getSelected.getJSONObject("address").getString("suburb"));
                                } else if (ar.contains("neighbourhood")) {
                                    name_of_place = getSelected.getJSONObject("address").getString("neighbourhood");
                                    cor.setName(getSelected.getJSONObject("address").getString("neighbourhood"));
                                }
                                address = "";
                                cor.setAddress("");
                            }
                            cor.setLat(lat);
                            cor.setLon(lon);
                        }
                        list.add(names);
                        if (cor.getName() != null) {
                            coordinates_arr.add(new Coordinates(lon, lat, name_of_place, address));
                        }
                        if (onClicked) {
                            name.setText(name_of_place);
                            address_text_view.setText(address);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (!onClicked) {
                initData();
            }
            onClicked = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehaviour.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            if (mapbox.getMarkers().size() != 0) {
                mapbox.clear();
                search.setText("");
                search_text = "";
                if (navigationMapRoute != null) {
                    navigationMapRoute.removeRoute();
                }
                currentPostion = new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
                        locationComponent.getLastKnownLocation().getLongitude());
            } else {
                super.onBackPressed();
            }

        }
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        botNav.setVisibility(View.GONE);
    }

    private void initData() {
        mapbox.clear();
        Log.d("size",coordinates_arr.size()+"");
        if (coordinates_arr.size() != 0) {
            for (int i = 0; i < coordinates_arr.size(); i++) {
                if (!coordinates_arr.get(i).getAddress().equals("")) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title(coordinates_arr.get(i).getName() + "," + coordinates_arr.get(i).getAddress());
                    markerOptions.position(new LatLng(coordinates_arr.get(i).getLat(), coordinates_arr.get(i).getLon()));
                    mapbox.addMarker(markerOptions);
                } else {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title(coordinates_arr.get(i).getName());
                    markerOptions.position(new LatLng(coordinates_arr.get(i).getLat(), coordinates_arr.get(i).getLon()));
                    mapbox.addMarker(markerOptions);
                }
            }
            adapter = new CompleteTextAdapter(list, MainActivity.this, R.layout.item);
            search.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private void addDestinationIconSymbolLayer(@NonNull Style loadedMapStyle) {
        loadedMapStyle.addImage("destination-icon-id",
                BitmapFactory.decodeResource(this.getResources(), R.drawable.mapbox_marker_icon_default));
        GeoJsonSource geoJsonSource = new GeoJsonSource("destination-source-id");
        loadedMapStyle.addSource(geoJsonSource);
        SymbolLayer destinationSymbolLayer = new SymbolLayer("destination-symbol-layer-id", "destination-source-id");
        destinationSymbolLayer.withProperties(
                iconImage("destination-icon-id"),
                iconAllowOverlap(true),
                iconIgnorePlacement(true)
        );
        loadedMapStyle.addLayer(destinationSymbolLayer);
    }

    private void openMap() {
        Log.d("asd","Openmap");
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                mapbox = mapboxMap;
                mapboxMap.setStyle(Style.TRAFFIC_DAY, new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        enableLocationComponent(style);
                        mapbox.getUiSettings().setAttributionEnabled(false);
                        mapbox.getUiSettings().setLogoEnabled(false);
                        mapboxMap.addOnMapClickListener(MainActivity.this);
                        mapboxMap.setOnMarkerClickListener(MainActivity.this);
                        if (count == 0) {
                            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                                currentPostion = new LatLng(locationComponent.getLastKnownLocation().getLatitude(),
//                                        locationComponent.getLastKnownLocation().getLongitude());
                                CameraPosition position = new CameraPosition.Builder()
                                        .target(currentPostion)
                                        .zoom(14)
                                        .tilt(20)
                                        .build();
                                mapboxMap.setCameraPosition(position);
                            } else {
                                CameraPosition position = new CameraPosition.Builder()
                                        .target(center)
                                        .zoom(12)
                                        .tilt(20)
                                        .build();
                                mapboxMap.setCameraPosition(position);
                            }
                            buildingPlugin = new BuildingPlugin(mapView, mapboxMap, style);
                            buildingPlugin.setVisibility(true);
                            count++;

                        }
                    }
                });
               // checkMap();
                LatLngBounds latLngBounds = new LatLngBounds.Builder()
                        .include(Common.city_info.getFirst_point())
                        .include(Common.city_info.getSecond_point())
                        .build();
                if(!isOnline()){
                    mapboxMap.setLatLngBoundsForCameraTarget(latLngBounds);
                    mapboxMap.setMaxZoomPreference(14);
                }
                mapbox.addOnCameraIdleListener(new MapboxMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        if (search_text.length() != 0) {
                            LatLngBounds bounds = mapbox.getProjection().getVisibleRegion().latLngBounds;
                            String url = "https://nominatim.openstreetmap.org/search?q=" +
                                    search_text + "&format=json&limit=50&viewbox=" + bounds.getNorthEast().getLongitude() + "," + bounds.getNorthEast().getLatitude()
                                    + "," + bounds.getSouthWest().getLongitude() + "," + bounds.getSouthWest().getLatitude() + "&bounded=1&namedetails=1&addressdetails=1";
                            new JsonTask()
                                    .execute(url);
                            Log.d("saddsasds", url);
                        }
                    }
                });
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                .directionsRoute(currentRoute)
                                .shouldSimulateRoute(false)
                                .build();
// Call this method with Context from within an Activity
                        NavigationLauncher.startNavigation(MainActivity.this, options);
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}
