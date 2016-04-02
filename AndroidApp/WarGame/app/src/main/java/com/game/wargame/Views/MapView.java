package com.game.wargame.Views;

import android.support.v4.app.FragmentActivity;

import com.game.wargame.Model.Entities.EntitiesModel;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.PlayerModel;
import com.game.wargame.Model.GameContext;
import com.game.wargame.R;
import com.game.wargame.Views.Animations.Animation;
import com.game.wargame.Views.Animations.AnimationFactory;
import com.game.wargame.Views.Animations.BitmapHolder;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;

public class MapView implements GoogleMapViewWrapper.OnMapReadyCallback, EntityDisplayer {

    public static final int LOCAL_PLAYER_MARKER_RES_ID = R.mipmap.marker_current;
    public static final int REMOTE_PLAYER_MARKER_RES_ID = R.mipmap.marker;

    private FragmentActivity mActivity;
    private GoogleMapWrapper mGoogleMap;
    private GoogleMapViewWrapper mGoogleMapViewWrapper;

    private HashMap<String, Marker> mPlayerLocations;
    private HashMap<String, Marker> mEntityMarkers;
    private BitmapHolder mBitmapHolder;

    private BitmapDescriptorFactory mBitmapDescriptorFactory;
    private OnMapReadyListener mOnMapReadyListener;

    private static final int DEFAULT_ZOOM = 15;


    public MapView(FragmentActivity fragmentActivity, GoogleMapViewWrapper googleMapViewWrapper, com.game.wargame.Views.BitmapDescriptorFactory bitmapDescriptorFactory) {
        init(fragmentActivity, googleMapViewWrapper, bitmapDescriptorFactory);
    }

    private void init(FragmentActivity activity, GoogleMapViewWrapper googleMapViewWrapper, com.game.wargame.Views.BitmapDescriptorFactory bitmapDescriptorFactory) {
        mActivity = activity;
        mBitmapDescriptorFactory = bitmapDescriptorFactory;
        mPlayerLocations = new HashMap<>();
        mEntityMarkers = new HashMap<>();

        mGoogleMapViewWrapper = googleMapViewWrapper;
        googleMapViewWrapper.onCreate(null);
        mBitmapHolder = new BitmapHolder(new AnimationFactory(), bitmapDescriptorFactory);
        mBitmapHolder.loadBitmaps();
    }

    public void startAsync(OnMapReadyListener onMapReadyListener) {
        mOnMapReadyListener = onMapReadyListener;
        mGoogleMapViewWrapper.getMapAsync(this);
    }

    public void onMapReady(GoogleMapWrapper googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setZoomControlEnabled(true);

        if(mOnMapReadyListener != null) {
            mOnMapReadyListener.onMapReady();
        }
        mGoogleMapViewWrapper.onResume();
    }

    public Projection getMapProjection() {
        return mGoogleMap.getProjection();
    }

    public void addLocalPlayer(final String playerId) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BitmapDescriptor bmp = mBitmapDescriptorFactory.load(LOCAL_PLAYER_MARKER_RES_ID);
                Marker playerMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(0, 0))
                        .anchor(0.5f, 0.35f)
                        .flat(true)
                        .icon(bmp));

                mPlayerLocations.put(playerId, playerMarker);
            }
        });
    }

    public void addRemotePlayer(final String playerId) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                BitmapDescriptor bmp = mBitmapDescriptorFactory.load(REMOTE_PLAYER_MARKER_RES_ID);
                Marker playerMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(0, 0))
                        .anchor(0.5f, 0.35f)
                        .flat(true)
                        .icon(bmp));

                mPlayerLocations.put(playerId, playerMarker);
            }
        });
    }

    public void movePlayerTo(final String playerId, final LatLng position) {
        if(mGoogleMap == null) return;

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Marker marker = mPlayerLocations.get(playerId);
                if (marker != null) {
                    marker.setPosition(position);
                }
            }
        });
    }

    public void addEntityMarker(Entity entity)
    {
        Animation animation = entity.getAnimation();
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(entity.getPosition())
                .rotation((float) entity.getDirection())
                .icon(mBitmapHolder.getBitmap(animation.current())));
        mEntityMarkers.put(entity.getUUID(), marker);
    }

    public void display(final Entity entity) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Animation animation = entity.getAnimation();
                Marker marker = mEntityMarkers.get(entity.getUUID());
                if (marker == null && !entity.isToRemove()) {
                    addEntityMarker(entity);
                } else if (marker != null) {
                    if (entity.isToRemove()) {
                        marker.remove();
                        mEntityMarkers.remove(entity.getUUID());
                    } else {
                        marker.setPosition(entity.getPosition());
                        marker.setIcon(mBitmapHolder.getBitmap(animation.current()));
                    }
                }
            }
        });
    }

    public void display(EntitiesModel entitiesModel) {
        ArrayList<Entity> entities = entitiesModel.getEntities();
        for (Entity entity : entities) {
            display(entity);
            if (entity.isToRemove())
                entitiesModel.removeEntity(entity);
        }
    }

    public void display(GameContext gameContext) {

    }

    public void removePlayer(final PlayerModel player) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Marker marker = mPlayerLocations.get(player.getPlayerId());

                if (marker != null) {
                    marker.remove();
                }
            }
        });
    }

    public void moveCameraTo(LatLng position) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(position);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    public void moveCameraTo(LatLng position, float zoom) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(position, zoom);
        mGoogleMap.animateCamera(cameraUpdate);
    }

    public interface OnMapReadyListener {
        public void onMapReady();
    }
}
