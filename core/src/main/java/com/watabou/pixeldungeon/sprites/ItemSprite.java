/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.watabou.pixeldungeon.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.TextureData;
import com.watabou.gdx.GdxTexture;
import com.watabou.gltextures.TextureCache;
import com.watabou.noosa.Game;
import com.watabou.noosa.MovieClip;
import com.watabou.noosa.TextureFilm;
import com.watabou.noosa.audio.Sample;
import com.watabou.pixeldungeon.Assets;
import com.watabou.pixeldungeon.Dungeon;
import com.watabou.pixeldungeon.DungeonTilemap;
import com.watabou.pixeldungeon.effects.CellEmitter;
import com.watabou.pixeldungeon.effects.Speck;
import com.watabou.pixeldungeon.items.Gold;
import com.watabou.pixeldungeon.items.Heap;
import com.watabou.pixeldungeon.items.Item;
import com.watabou.pixeldungeon.levels.Level;
import com.watabou.pixeldungeon.levels.Terrain;
import com.watabou.pixeldungeon.scenes.GameScene;
import com.watabou.utils.PointF;
import com.watabou.utils.Random;
import com.watabou.glwrap.Matrix;
import com.watabou.glwrap.Vertexbuffer;
import com.watabou.noosa.NoosaScript;

public class ItemSprite extends MovieClip {

	public static int SIZE	= 16;

	private static final float DROP_INTERVAL = 0.4f;

	protected TextureFilm film;

	public Heap heap;

	private Glowing glowing;
	private float phase;
	private boolean glowUp;

	private float dropInterval;

    protected boolean renderShadow  = false;
    protected float shadowWidth     = 1f;
    protected float shadowHeight    = 0.25f;
	protected float shadowOffset    = 0.1f;

	public ItemSprite() {
		this(Item.defaultAtlas, ItemSpriteSheet.SMTH, SIZE, null);
	}

	public ItemSprite(Item item) {
		this(item.Atlas, item.image(), item.image_size, item.glowing());
	}
    
    public ItemSprite(int image, Glowing glowing) {
        this(Item.defaultAtlas, image, 16, glowing);
	}
    
    public ItemSprite(Heap heap) {
        this(Item.defaultAtlas, heap.image(), 16, heap.glowing());
	}
    
//    public ItemSprite(int image, Glowing glowing) {
//        this(Item.defaultAtlas, image, SIZE, glowing);
//    }
    
    public ItemSprite(Item item, Glowing glowing) {
        this(item.Atlas, item.image(), item.image_size, glowing);
    }

	public ItemSprite(Object Atlas, int image, int size, Glowing glowing) {
		super(Atlas);
        SIZE = size;
		film = new TextureFilm(texture, SIZE, SIZE);
		view(image, glowing);
	}

	public void originToCenter() {
		origin.set(SIZE / 2);
	}

	public void link() {
		link(heap);
	}

	public void link(Heap heap) {
		this.heap = heap;
		view(heap.image(), heap.glowing());
        renderShadow = true;
		place(heap.pos);
	}

	@Override
	public void revive() {
		super.revive();

		speed.set(0);
		acc.set(0);
		dropInterval = 0;

		heap = null;
	}

	public PointF worldToCamera(int cell) {
		final int csize = DungeonTilemap.SIZE;

		return new PointF(
            cell % Level.WIDTH * csize + (csize - SIZE) * 0.5f,
            cell / Level.WIDTH * csize + (csize - SIZE) * 0.5f
		);
	}

	public void place(int p) {
		point(worldToCamera(p));
        shadowOffset = 0.1f;
	}

	public void drop() {

		if (heap.isEmpty()) {
			return;
		}

		dropInterval = DROP_INTERVAL;

		speed.set(0, -100);
		acc.set(0, -speed.y / DROP_INTERVAL * 2);

		if (visible && heap != null && heap.peek() instanceof Gold) {
			CellEmitter.center(heap.pos).burst(Speck.factory(Speck.COIN), 5);
			Sample.INSTANCE.play(Assets.SND_GOLD, 1, 1, Random.Float(0.9f, 1.1f));
		}
	}

	public void drop(int from) {

		if (heap.pos == from) {
			drop();
		} else {

			float px = x;
			float py = y;
			drop();

			place(from);

			speed.offset((px - x) / DROP_INTERVAL, (py - y) / DROP_INTERVAL);
		}
	}

	public ItemSprite view(int image, Glowing glowing) {
		frame(film.get(image));
		if ((this.glowing = glowing) == null) {
			resetColor();
		}
		return this;
	}

    private float[] shadowMatrix = new float[16];

    @Override
    protected void updateMatrix() {
        super.updateMatrix();
        Matrix.copy(matrix, shadowMatrix);
        Matrix.translate(shadowMatrix,
                         (width() * (1f - shadowWidth)) / 2f,
                         (height() * (1f - shadowHeight)) + shadowOffset);
        Matrix.scale(shadowMatrix, shadowWidth, shadowHeight);
    }

    @Override
    public void draw() {
        if (texture == null || (!dirty && buffer == null))
            return;

        if (renderShadow) {
            if (dirty) {
                verticesBuffer.position(0);
                verticesBuffer.put(vertices);
                if (buffer == null)
                    buffer = new Vertexbuffer(verticesBuffer);
                else
                    buffer.updateVertices(verticesBuffer);
                dirty = false;
            }

            NoosaScript script = script();

            texture.bind();

            script.camera(camera());

            updateMatrix();

            script.uModel.valueM4(shadowMatrix);
            script.lighting(
                0, 0, 0, am * .6f,
                0, 0, 0, aa * .6f);

            script.drawQuad(buffer);
        }

        super.draw();

	}

	@Override
	public void update() {
		super.update();

		visible = (heap == null || Dungeon.visible[heap.pos]);

		if (dropInterval > 0) {
            shadowOffset -= speed.y * Game.elapsed * 0.8f;

            if ((dropInterval -= Game.elapsed) <= 0) {

                speed.set(0);
                acc.set(0);
                shadowOffset = 0.1f;
                place(heap.pos);

                if (visible) {
                    boolean water = Level.water[heap.pos];

                    if (water) {
                        GameScene.ripple(heap.pos);
                    } else {
                        int cell = Dungeon.level.map[heap.pos];
                        water = (cell == Terrain.WELL || cell == Terrain.ALCHEMY);
                    }

                    if (!(heap.peek() instanceof Gold)) {
                        Sample.INSTANCE.play(water ? Assets.SND_WATER : Assets.SND_STEP, 0.8f, 0.8f, 1.2f);
                    }
                }
            }
        }

		if (visible && glowing != null) {
			if (glowUp && (phase += Game.elapsed) > glowing.period) {

				glowUp = false;
				phase = glowing.period;

			} else if (!glowUp && (phase -= Game.elapsed) < 0) {

				glowUp = true;
				phase = 0;

			}

			float value = phase / glowing.period * 0.6f;

			rm = gm = bm = 1 - value;
			ra = glowing.red * value;
			ga = glowing.green * value;
			ba = glowing.blue * value;
		}
	}

    public static int pick(Item item, int x, int y) {
        GdxTexture bmp = TextureCache.get(item.Atlas).bitmap;
        int rows = bmp.getWidth() / item.image_size;
        int row = item.image() / rows;
        int col = item.image() % rows;
        // FIXME: I'm assuming this is super slow?
        final TextureData td = bmp.getTextureData();
        if (!td.isPrepared()) {
            td.prepare();
        }
        final Pixmap pixmap = td.consumePixmap();
        int pixel = pixmap.getPixel(col * item.image_size + x, row * item.image_size + y);
        pixmap.dispose();
        return pixel;
	}

	public static class Glowing {

		public static final Glowing WHITE = new Glowing(0xFFFFFF, 0.6f);

		public int color;
		public float red;
		public float green;
		public float blue;
		public float period;

		public Glowing(int color) {
			this(color, 1f);
		}

		public Glowing(int color, float period) {

			this.color = color;

			red = (color >> 16) / 255f;
			green = ((color >> 8) & 0xFF) / 255f;
			blue = (color & 0xFF) / 255f;

			this.period = period;
		}
	}
}
