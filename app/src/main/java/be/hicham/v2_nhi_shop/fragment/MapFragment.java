package be.hicham.v2_nhi_shop.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.HashMap;

import be.hicham.v2_nhi_shop.R;
import be.hicham.v2_nhi_shop.databinding.ActivityMapsBinding;
import be.hicham.v2_nhi_shop.models.Localisation;
import be.hicham.v2_nhi_shop.utilities.PreferenceManager;

public class MapFragment extends Fragment {

    LatLng userLatLong;
    LocationListener locationListener;
    LocationManager locationManager;

    private GoogleMap mMap;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map,container,false);
        SupportMapFragment supportMapFragment =(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_Map);

        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                        // sauvegarder la position das la DB
                        Localisation locationUser =  new Localisation(
                                location.getLatitude(),
                                location.getLongitude()
                        );
                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        HashMap<String, Object> user = new HashMap<>();
                        user.put("latitude", locationUser.getLatitude());
                        user.put("longitude", locationUser.getLongitude());

                        database.collection("localisation Actuel")
                                .add(user)
                                .addOnSuccessListener(documentReference -> {
                                })
                                .addOnFailureListener(exception -> {
                                });

                        // afficher la position sur la map
                        userLatLong = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.clear(); // To clear old marker on map
                        mMap.addMarker(new MarkerOptions().position(userLatLong).title("Votre localisation"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(userLatLong));
                    }
                };

                // demande de permission d'avoir leur localisation
                askLocalisationPermission();
            }
            private void askLocalisationPermission() {
                Dexter.withActivity(getActivity()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1000, locationListener);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
            }
        });
        return view;
    }
}