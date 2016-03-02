package com.creativejones.andre.recommendations;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.creativejones.andre.recommendations.api.Etsy;
import com.creativejones.andre.recommendations.google.GoogleServicesHelper;
import com.creativejones.andre.recommendations.model.ActiveListings;
import com.creativejones.andre.recommendations.model.Listing;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ListingHolder>
    implements Callback<ActiveListings>, GoogleServicesHelper.GoogleServicesListener {

    private LayoutInflater mInflater;
    private ActiveListings mActiveListings;
    private MainActivity mActivity;
    private boolean isGooglePlayServicesAvailable;

    public ListingAdapter(MainActivity activity){
        mInflater = LayoutInflater.from(activity);
        mActivity = activity;
        isGooglePlayServicesAvailable = false;
    }

    @Override
    public ListingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListingHolder(mInflater.inflate(R.layout.layout_listing, parent, false));
    }

    @Override
    public void onBindViewHolder(ListingHolder holder, int position) {
        final Listing listing = mActiveListings.results[position];

        holder.titleView.setText(listing.title);
        holder.shopNameView.setText(listing.Shop.shop_name);
        holder.priceView.setText(listing.price);

        if(isGooglePlayServicesAvailable){
            //show plus one button
        } else {
            //dont
        }

        Picasso.with(holder.imageView.getContext())
                .load(listing.Images[0].url_570xN)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        if(mActiveListings == null) return 0;
        if(mActiveListings.results == null) return 0;

        return mActiveListings.results.length;
    }

    @Override
    public void success(ActiveListings activeListings, Response response) {
        mActiveListings = activeListings;
        notifyDataSetChanged();
        mActivity.showList();
    }

    @Override
    public void failure(RetrofitError error) {
        mActivity.showError();
    }

    @Override
    public void onConnected() {
        if(getItemCount() == 0){
            Etsy.getActiveListings(this);
        }

        isGooglePlayServicesAvailable = true;

        notifyDataSetChanged();
    }

    @Override
    public void onDisconnected() {
        if(getItemCount() == 0){
            Etsy.getActiveListings(this);
        }

        isGooglePlayServicesAvailable = false;

        notifyDataSetChanged();
    }

    public class ListingHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView titleView;
        public TextView shopNameView;
        public TextView priceView;

        public ListingHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.listing_image);
            titleView = (TextView)itemView.findViewById(R.id.listing_title);
            shopNameView = (TextView)itemView.findViewById(R.id.listing_shop_name);
            priceView = (TextView)itemView.findViewById(R.id.listing_price);
        }
    }

    public ActiveListings getActiveListings(){
        return mActiveListings;
    }
}
