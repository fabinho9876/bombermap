package com.game.wargame.GameEngine;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.game.wargame.Entities.Player;
import com.game.wargame.R;
import com.game.wargame.WeaponControllers.AbstractWeaponControllerView;
import com.game.wargame.WeaponControllers.RocketControllerView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;


public class GameView implements OnMapReadyCallback, AbstractWeaponControllerView.OnActionFinishedListener {

    private Activity mActivity;
    private GoogleMap mMap;

    private RelativeLayout mMapLayout;
    private AbstractWeaponControllerView mWeaponControllerInterface;
    private Button mFireButton;

    private OnWeaponTargetDefinedListener mOnWeaponTargetDefined;

    private Map<String, Marker> mPlayerLocations;

    public GameView(final Activity activity) {
        mActivity = activity;
        mPlayerLocations = new HashMap<>();
        final GameView that = this;

        mFireButton = (Button) mActivity.findViewById(R.id.fire_button);

        mFireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                that.setWeaponController(new RocketControllerView(activity));
            }
        });

        mMapLayout = (RelativeLayout) activity.findViewById(R.id.map_layout);
    }

    public void initialize(OnWeaponTargetDefinedListener onWeaponTargetDefinedListener) {
        mOnWeaponTargetDefined = onWeaponTargetDefinedListener;
    }

    public void movePlayer(final Player player) {
        if(mMap == null) return;

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Marker marker = mPlayerLocations.get(player.getPlayerId());
                if(marker != null) {
                    marker.setPosition(player.getPosition());
                }
                else {
                    Marker playerMarker = mMap.addMarker(new MarkerOptions()
                            .position(player.getPosition())
                            .anchor(0.5f, 0.35f)
                            .flat(true)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.marker)));
                    mPlayerLocations.put(player.getPlayerId(), playerMarker);
                }
            }
        });
    }

    public void animateCamera(CameraUpdate cameraUpdate) {
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings uiSettings = mMap.getUiSettings();

        uiSettings.setZoomControlsEnabled(true);
    }

    public void setWeaponController(AbstractWeaponControllerView weaponController) {
        mWeaponControllerInterface = weaponController;

        if(mWeaponControllerInterface != null) {
            mFireButton.setSelected(true);

            mWeaponControllerInterface.initialize();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

            mMapLayout.addView(mWeaponControllerInterface, params);
            mWeaponControllerInterface.setOnActionFinishedListener(this);
            mWeaponControllerInterface.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {

                        float targetX = event.getX();
                        float targetY = event.getY();

                        if(mOnWeaponTargetDefined != null) {
                            mOnWeaponTargetDefined.onWeaponTargetDefined(targetX, targetY);
                        }
                        onActionFinished();
                    }
                    return true;
                }
            });
            mMapLayout.invalidate();
        }
    }

    @Override
    public void onActionFinished() {
        if(mWeaponControllerInterface != null) {
            mWeaponControllerInterface.finalize();
            mMapLayout.removeView(mWeaponControllerInterface);
            mMapLayout.invalidate();
            mWeaponControllerInterface = null;
            mFireButton.setSelected(false);
        }
    }

    public void triggerWeapon(LatLng source, LatLng destination, double speed) {

    }

    public Projection getMapProjection() {
        return mMap.getProjection();
    }

    public interface OnWeaponTargetDefinedListener {
        public void onWeaponTargetDefined(float x, float y);
    }
}
