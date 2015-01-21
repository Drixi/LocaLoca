package com.example.group3.localoca;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MapFragment extends Fragment {

    ImageView img_first_floor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_blueprint, container, false);

        img_first_floor = (ImageView) rootView.findViewById(R.id.imageEnhance);
        new PhotoViewAttacher(img_first_floor);

        return rootView;
    }
}

