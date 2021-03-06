package com.game.wargame.Views.Views;

import android.support.v4.app.FragmentActivity;

import com.game.wargame.AppConstant;
import com.game.wargame.Model.Entities.Entity;
import com.game.wargame.Model.Entities.Players.Player;
import com.game.wargame.Model.Entities.VirtualMap.CellTypeEnum;
import com.game.wargame.Model.Entities.VirtualMap.RealCell;
import com.game.wargame.Model.GameContext.GameContext;
import com.game.wargame.R;
import com.game.wargame.Views.Animations.Animation;
import com.game.wargame.Views.Animations.AnimationFactory;
import com.game.wargame.Views.Animations.PlayerAliveAnimation;
import com.game.wargame.Views.Bitmaps.BitmapCache;
import com.game.wargame.Views.Bitmaps.BitmapDescriptorDescriptorFactory;
import com.game.wargame.Views.Bitmaps.IBitmapFactory;
import com.game.wargame.Views.GoogleMap.GoogleMap;
import com.game.wargame.Views.GoogleMap.GoogleMapView;
import com.game.wargame.Views.GoogleMap.IGoogleMapView;
import com.game.wargame.Views.PlayerMarker;
import com.game.wargame.Views.PlayerMarkerFactory;
import com.game.wargame.Views.VirtualMap.Block;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;

public class MapView implements GoogleMapView.OnMapReadyCallback {

    public static final int LOCAL_PLAYER_MARKER_RES_ID = R.mipmap.profile_s;
    public static final int REMOTE_PLAYER_MARKER_RES_ID = R.mipmap.profile_c;

    private FragmentActivity mActivity;
    private GoogleMap mGoogleMap;
    private IGoogleMapView mGoogleMapView;

    private PlayerMarker mPlayerShadowMarker;
    private HashMap<String, PlayerMarker> mPlayerLocations;
    private HashMap<String, Marker> mEntityMarkers;
    private HashMap<String, Block> mBlockMarkers;
    private BitmapCache mBitmapCache;

    private PlayerMarkerFactory mPlayerMarkerFactory;
    private OnMapReadyListener mOnMapReadyListener;


    public MapView(FragmentActivity fragmentActivity, IGoogleMapView googleMapView, BitmapDescriptorDescriptorFactory bitmapDescriptorFactory) {
        mPlayerMarkerFactory = new PlayerMarkerFactory(bitmapDescriptorFactory);
        mBitmapCache = new BitmapCache(fragmentActivity.getResources(), new AnimationFactory(), bitmapDescriptorFactory);

        init(fragmentActivity, googleMapView, new PlayerMarkerFactory(bitmapDescriptorFactory));
    }


    // For test
    public MapView(FragmentActivity fragmentActivity, IGoogleMapView googleMapView, BitmapDescriptorDescriptorFactory bitmapDescriptorFactory, IBitmapFactory bitmapFactory, PlayerMarkerFactory playerMarkerFactory) {
        mBitmapCache = new BitmapCache(fragmentActivity.getResources(), new AnimationFactory(), bitmapDescriptorFactory, bitmapFactory);

        init(fragmentActivity, googleMapView, playerMarkerFactory);
    }

    private void init(FragmentActivity activity, IGoogleMapView googleMapView, PlayerMarkerFactory playerMarkerFactory) {
        mActivity = activity;
        mPlayerLocations = new HashMap<>();
        mBlockMarkers = new HashMap<>();

        mPlayerMarkerFactory = playerMarkerFactory;
        mEntityMarkers = new HashMap<>();

        mGoogleMapView = googleMapView;
        mGoogleMapView.onCreate(null);

        mBitmapCache.loadBitmaps();
    }

    public void startAsync(OnMapReadyListener onMapReadyListener) {
        mOnMapReadyListener = onMapReadyListener;
        mGoogleMapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setZoomControlEnabled(true);
        mPlayerMarkerFactory.setGoogleMap(mGoogleMap);

        if(mOnMapReadyListener != null) {
            mOnMapReadyListener.onMapReady();
        }
        mGoogleMapView.onResume();
    }

    public Projection getMapProjection() {
        return mGoogleMap.getProjection();
    }

    public void addLocalPlayer(final String playerId) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayerMarker playerMarker = mPlayerMarkerFactory.create(LOCAL_PLAYER_MARKER_RES_ID);
                mPlayerLocations.put(playerId, playerMarker);
            }
        });
    }

    public void addRemotePlayer(final String playerId) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayerMarker playerMarker = mPlayerMarkerFactory.create(PlayerAliveAnimation.getResourceIdForNumero(AppConstant.getNumeroFromName(playerId)));
                mPlayerLocations.put(playerId, playerMarker);
            }
        });
    }

    public void movePlayerTo(final String playerId, final LatLng position) {
        if (mGoogleMap == null) return;

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayerMarker marker = mPlayerLocations.get(playerId);
                if (marker != null) {
                    marker.move(position);
                }
            }
        });
    }

    public void addPlayerShadow(final LatLng position) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    mPlayerShadowMarker = mPlayerMarkerFactory.create(mBitmapCache.getBitmap(1000));
                    mPlayerShadowMarker.move(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void movePlayerShadow(final LatLng position) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPlayerShadowMarker.move(position);
            }
        });
    }

    public void removePlayerShadow() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mPlayerShadowMarker.remove();
                mPlayerShadowMarker = null;
            }
        });
    }

    public void display(final Player player) {
        final String playerId = player.getPlayerId();
        final Animation playerAnimation = player.getAnimation();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayerMarker marker = mPlayerLocations.get(playerId);
                if (marker != null && playerAnimation != null && playerAnimation.isDirty()) {
                    try {
                        marker.setIcon(mBitmapCache.getBitmap(playerAnimation.current()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    playerAnimation.clean();
                }
            }
        });
    }

    public void addEntity(Entity e) {
        Animation animation = e.getAnimation();
        Marker marker = null;
        try {
            marker = mGoogleMap.addMarker(new MarkerOptions()
                    .position(e.getPosition())
                    .rotation((float) e.getDirection())
                    .anchor(0.5f, 0.5f)
                    .icon(mBitmapCache.getBitmap(animation.current())));
            mEntityMarkers.put(e.getUUID(), marker);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void updateEntity(Entity e) {
        Marker marker = mEntityMarkers.get(e.getUUID());
        if (marker != null) {
            marker.setPosition(e.getPosition());
            Animation animation = e.getAnimation();
            if (animation.isDirty())
                try {
                    marker.setIcon(mBitmapCache.getBitmap(animation.current()));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            animation.clean();
        }
    }

    public void removeEntity(Entity e) {
        Marker marker = mEntityMarkers.get(e.getUUID());
        if (marker != null)
            marker.remove();
        mEntityMarkers.remove(e.getUUID());
    }

    public void display(GameContext gameContext) {

    }

    private void addBlock(RealCell realCell, float rotation, int resId) {
        BitmapDescriptor scaledBlockDescriptor = null;
        try {
            scaledBlockDescriptor = mBitmapCache.getBitmap(resId);
            Block b = mGoogleMap.addBlock(new GroundOverlayOptions()
                    .position(realCell.position(), realCell.width(), realCell.height())
                    .anchor(0.5f, 0.5f)
                    .zIndex(-100)
                    .bearing(rotation)
                    .image(scaledBlockDescriptor));

            mBlockMarkers.put(realCell.getUUID(), b);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addBlock(RealCell realCell, float rotation) {

        CellTypeEnum cellType = realCell.cell().type();
        if (cellType == CellTypeEnum.BREAKABLE_BLOCK) {
            addBlock(realCell, rotation, R.mipmap.woodbox);
        }
        else if(cellType == CellTypeEnum.UNBREAKABLE_BLOCK) {
            addBlock(realCell, rotation, R.mipmap.wall);
        }
    }

    public void removeBlock(RealCell realCell) {
        Block b = mBlockMarkers.get(realCell.getUUID());
        if(b != null) {
            b.remove();
            mBlockMarkers.remove(realCell.getUUID());
        }
    }

    public void removePlayer(final String playerId) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                PlayerMarker marker = mPlayerLocations.get(playerId);
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

    public void setOnMapClickListener(com.google.android.gms.maps.GoogleMap.OnMapClickListener onMapClickListener) {
        mGoogleMap.setOnMapClickListener(onMapClickListener);
    }

    public void setOnMapLongClickListener(com.google.android.gms.maps.GoogleMap.OnMapLongClickListener onMapLongClickListener) {
        mGoogleMap.setOnMapLongClickListener(onMapLongClickListener);
    }

    public interface OnMapReadyListener {
        public void onMapReady();
    }
}
