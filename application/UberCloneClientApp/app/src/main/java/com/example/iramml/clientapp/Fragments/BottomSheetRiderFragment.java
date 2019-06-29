package com.example.iramml.clientapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.iramml.clientapp.Common.Common;
import com.example.iramml.clientapp.R;
import com.example.iramml.clientapp.Retrofit.IGoogleAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheetRiderFragment extends BottomSheetDialogFragment {
    String mLocation;
    boolean isTapOnMap;
    IGoogleAPI mService;
    TextView txtLocation;
    public static BottomSheetRiderFragment newInstance(String location, boolean isTapOnMap){
        BottomSheetRiderFragment fragment=new BottomSheetRiderFragment();
        Bundle args=new Bundle();
        args.putString("location", location);

        args.putBoolean("isTapOnMap", isTapOnMap);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation=getArguments().getString("location");

        isTapOnMap=getArguments().getBoolean("isTapOnMap");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view0=inflater.inflate(R.layout.bottom_sheet_rider, container, false);
        txtLocation=(TextView)view0.findViewById(R.id.txtLocation);



        mService=Common.getGoogleService();
        getPrice(mLocation);

        if(!isTapOnMap){
            //from place fragment
            txtLocation.setText(mLocation);

        }

        return view0;
    }

    private void getPrice(String mLocation) {
        try {
            String requestUrl = "https://maps.googleapis.com/maps/api/directions/json?mode=driving&" +
                    "transit_routing_preference=less_driving&" +
                    "origin=" + mLocation + "&" +
                    "key=" + getResources().getString(R.string.google_direction_api);
            Log.d("LINK_ROUTES", requestUrl);
            mService.getPath(requestUrl).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        JSONArray routes = jsonObject.getJSONArray("routes");

                        JSONObject object = routes.getJSONObject(0);
                        JSONArray legs = object.getJSONArray("legs");

                        JSONObject legsObject = legs.getJSONObject(0);

                        JSONObject distance = legsObject.getJSONObject("distance");
                        String distanceText = distance.getString("text");
                        Double distanceValue = Double.parseDouble(distanceText.replaceAll("[^0-9\\\\.]", ""));



                        if(isTapOnMap) {
                            String startAddress = legsObject.getString("start_address");


                            txtLocation.setText(startAddress);


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.d("ERROR", t.getMessage());
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
