package com.sathi4shopping.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sathi4shopping.Activity.WebViewActivity;
import com.sathi4shopping.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EcommerceFragment extends Fragment {


    public EcommerceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ecommerce, container, false);
        LinearLayout layout1 = view.findViewById(R.id.flipkart_phone);
        LinearLayout layout2 = view.findViewById(R.id.flipkart_tv);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", "https://www.flipkart.com/mobile-phones-store?otracker=nmenu_sub_Electronics_0_Mobiles&affid=flipkartecho&affExtParam1=UP_GB_9213333639"));
            }
        });
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", "https://www.flipkart.com/tvsandappliancesclp-store?otracker=nmenu_sub_Electronics_0_Mobiles&affid=flipkartecho&affExtParam1=UP_GB_9213333639"));
            }
        });
        return view;
    }
}
