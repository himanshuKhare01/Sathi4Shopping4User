package com.sathi4shopping.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.sathi4shopping.Activity.WebViewActivity;
import com.sathi4shopping.R;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.RichLinkListener;
import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;

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
        final RichLinkView view1 = view.findViewById(R.id.richLinkView1);
        final RichLinkView view2 = view.findViewById(R.id.richLinkView2);
        addLink(view1, "https://banner2.kisspng.com/20180804/srv/kisspng-4k-resolution-smart-tv-philips-led-backlit-lcd-ele-clearance-sale-tvs-and-appliances-home-kitchen-5b66337db06ae7.4017029315334245097226.jpg", "https://www.flipkart.com/tvsandappliancesclp-store?otracker=nmenu_sub_Electronics_0_Mobiles&affid=flipkartecho&affExtParam1=UP_GB_9213333639", view);
        addLink(view2, "https://www.pngkey.com/png/detail/377-3778414_android-one-latest-mobile-phones-png.png", "https://www.flipkart.com/mobile-phones-store?otracker=nmenu_sub_Electronics_0_Mobiles&affid=flipkartecho&affExtParam1=UP_GB_9213333639", view);
        return view;
    }

    private void addLink(final RichLinkView richlinkview, final String img, final String address, final View view) {
        try {
            URL url = new URL(address);
            richlinkview.setLink(String.valueOf(url), new ViewListener() {
                        @Override
                        public void onSuccess(boolean status) {
                            view.findViewById(R.id.eshopping_progressbar).setVisibility(View.GONE);
                            view.findViewById(R.id.eshopping_progressbar2).setVisibility(View.GONE);
                            richlinkview.getMetaData().setImageurl(img);
                        }

                        @Override
                        public void onError(final Exception e) {
                            Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "Network Problem ", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "error catch", Toast.LENGTH_SHORT).show();
        }
        richlinkview.setDefaultClickListener(false);
        richlinkview.setClickListener(new RichLinkListener() {
            @Override
            public void onClicked(View view, MetaData meta) {
                startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", address));
            }
        });
    }
}
