package be.hicham.v2_nhi_shop.fragment;

import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import be.hicham.v2_nhi_shop.R;
import be.hicham.v2_nhi_shop.models.Article;
import be.hicham.v2_nhi_shop.utilities.Constants;

public class MapFragment extends Fragment {

    private LatLng userLatLong;
    private LocationListener locationListener;
    private LocationManager locationManager;

    private GoogleMap mMap;
    private Article article;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        article = (Article) getArguments().getSerializable(Constants.KEY_ARTICLE);
        System.out.println("adressse out : " + article.getLocalisation());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map,container,false);
        SupportMapFragment supportMapFragment =(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_Map);


        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                System.out.println("adressse  : " + article.getLocalisation());


                // Create a new Geocoder object
                Geocoder geocoder = new Geocoder(getContext());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocationName(article.getLocalisation(), 6);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Get the first address from the list
                Address address = addresses.get(0);

                // Get the latitude and longitude of the address
                double latitude = address.getLatitude();
                double longitude = address.getLongitude();

                // Use the latitude and longitude values as needed
                //GeoPoint geoPoint = getLocationFromAddress(article.getLocalisation());
                //afficher la position sur la map
                userLatLong = new LatLng(latitude, longitude);
                System.out.println("lat and long : " + userLatLong);
                mMap.clear(); // To clear old marker on map
                mMap.addMarker(new MarkerOptions().position(userLatLong).title("Votre localisation"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLong));

            }
        });
        return view;
    }

}