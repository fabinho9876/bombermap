package com.game.wargame.Views.Animations;

import com.game.wargame.R;

/**
 * Created by sergei on 14/03/16.
 */
public class ProjectileAnimation extends Animation {

    protected static final Size SIZE = new Size(64, 64);

    public ProjectileAnimation() {
        mDrawablesId.add(R.mipmap.missile1);
        mDrawablesId.add(R.mipmap.missile2);
        mDrawablesId.add(R.mipmap.missile3);
        mDrawablesId.add(R.mipmap.missile4);
    }

    @Override
    public Size getSize() {
        return SIZE;
    }
}
