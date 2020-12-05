package com.watabou.pixeldungeon;

import com.badlogic.gdx.Game;
import com.watabou.input.GameAction;
import com.watabou.utils.PDPlatformSupport;

public class Launcher extends Game {

    PixelDungeon p;

	public Launcher(final PDPlatformSupport<GameAction> platformSupport) {
        p = new PixelDungeon(platformSupport);
    }

	@Override
	public void create() {

		setScreen(p);

	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

}
