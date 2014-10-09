package com.example.hkharton.testcanvas.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.hkharton.testcanvas.activities.LibraryActivity;
import com.example.hkharton.testcanvas.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class WoodFragment extends Fragment implements OnClickListener {
    // Surface filter button
    private Button woodSmooth = null;
    private Button woodRough = null;
    private Button woodSuperRough = null;

    Map<String, Boolean> statusMap = new HashMap<String, Boolean>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wood, container, false);

        woodSmooth = (Button) rootView.findViewById(R.id.woodSmooth);
        woodRough = (Button) rootView.findViewById(R.id.woodRough);
        woodSuperRough = (Button) rootView.findViewById(R.id.woodSuperRough);

        statusMap.put("woodSmooth", false);
        statusMap.put("woodRough", false);
        statusMap.put("woodSuperRough", false);

        woodSmooth.setOnClickListener(this);
        woodRough.setOnClickListener(this);
        woodSuperRough.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        ArrayList<String> requestedCharacteristic = new ArrayList<String>();

        if(woodSmooth.getId() == v.getId()){
            toggleStatus("woodSmooth", v);
        }else if(woodRough.getId() == v.getId()) {
            toggleStatus("woodRough", v);
        }else if(woodSuperRough.getId() == v.getId()){
            toggleStatus("woodSuperRough", v);
        }

        // update the display
        Iterator statusIterator = statusMap.entrySet().iterator();
        while (statusIterator.hasNext()) {
            Map.Entry<String, Boolean> pairs = (Map.Entry) statusIterator.next();

            if(pairs.getValue() == true){
                requestedCharacteristic.add(pairs.getKey());
                Log.e("TEST", pairs.getKey());
            }
        }
        LibraryActivity.updateTileImageList(LibraryActivity.WOOD_TYPE, requestedCharacteristic);
    }

    private void toggleStatus(String status, View view){
        boolean currentStatus = statusMap.get(status);
        if(currentStatus == false){
            statusMap.put(status, true);
            view.setBackground(getResources().getDrawable(R.drawable.button_border_selected));
            Log.e("TEST", status + " TO -> true");
        }else{
            statusMap.put(status, false);
            view.setBackground(getResources().getDrawable(R.drawable.button_border_unselected));
            Log.e("TEST", status + " TO -> false");
        }
    }
}