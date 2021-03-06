/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * Virtual Pixel Dungeon
 * Copyright (C) 2020-2021 AnsdoShip Studio
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

package com.watabou.noosa;

import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.RenderedText;
import com.watabou.noosa.ui.Component;

import java.util.ArrayList;
import com.watabou.pixeldungeon.ui.Window;
import net.whitegem.pixeldungeon.LanguageFactory;

public class BitmapText extends Component {

	private int maxWidth = Integer.MAX_VALUE;
	public int nLines;

	private static final RenderedText SPACE = new RenderedText();
	private static final RenderedText NEWLINE = new RenderedText();

	protected String text;
	protected String[] tokens = null;
	protected ArrayList<RenderedText> words = new ArrayList<>();
    protected ArrayList<ColorBlock> underlines = new ArrayList<>();
	protected boolean multiline = false;

	private int size;
	private float zoom;

	private int color = -1;
	private int[] highlightColors = new int[] {
        0x000000,
        0x0000AA,
        0x00AA00,
        0x00AAAA,
        0xAA0000,
        0xAA00AA,
        0xFFAA00,
        0xAAAAAA,
        0x555555,
        0x5555FF,
        0x55FF55,
        0x55FFFF,
        0xFF5555,
        0xFF55FF,
        0xFFFF55,
        0xFFFFFF,
        0xDDD605
	};
	private int highlightColor = color;
	private static final String highlightSymbols = "0123456789abcdef";
	private int highlightColorIndex = -1;
	private boolean highlightSymbolDetected = false;
    private boolean rainbow = false;
	private static final int NORMAL = -1;
	private static final int BOLD = 0;
	private static final int ITALIC = 1;
	private static final int DELETELINE = 2;
	private static final int UNDERLINE = 3;
	private static final int RANDOM = 4;
	private static int wordStyle;

	public static final int LEFT_ALIGN = 1;
	public static final int CENTER_ALIGN = 2;
	public static final int RIGHT_ALIGN = 3;
	private int alignment = LEFT_ALIGN;
    private float spacing;

	public BitmapText(int size) {
		this.size = size;
        spacing = 0.5f;
	}

	public BitmapText(String text, int size) {
		this.size = size;
        spacing = 0.5f;
		text(text);
	}

    public BitmapText(String text, int size, float spacing) {
        this.size = size;
        this.spacing = spacing;
        text(text);
	}

	public void text(String text) {
		this.text = LanguageFactory.getTranslation(text);

		if (LanguageFactory.getTranslation(text) != null && !LanguageFactory.getTranslation(text).equals("")) {

			tokens = Game.platform.splitforTextBlock(LanguageFactory.getTranslation(text), multiline);

			build();
		} else {
            tokens = Game.platform.splitforTextBlock("", multiline);

			build();
        }
	}

    public void text(String str1, String str2) {
        text = str1 + " " + LanguageFactory.getTranslation(str2);

        if (text != null && !text.equals("")) {

			tokens = Game.platform.splitforTextBlock(text, multiline);

			build();
		} else {
            tokens = Game.platform.splitforTextBlock("", multiline);

            build();
        }

    }

	public void text(String text, int maxWidth) {
		this.maxWidth = maxWidth;
		multiline = true;
		text(text);
	}

    public void text(String text, float spacing) {
        this.spacing = spacing;
        text(text);
	}

	public String text() {
		return text;
	}

    public float spacing() {return spacing;}

    public void spacing(float spacing) {
        this.spacing = spacing;
        text(text);
    }

	public void maxWidth(int maxWidth) {
		if (this.maxWidth != maxWidth) {
			this.maxWidth = maxWidth;
			multiline = true;
			text(text);
		}
	}

	public int maxWidth() {
		return maxWidth;
	}

	public void size(int size) {
	    this.size = size;
		text(text);
	}

	public int size() {
	    return size;
	}

	private synchronized void build() {
		if (tokens == null) return;

		clear();
		words = new ArrayList<>();
        underlines = new ArrayList<>();

		highlightColorIndex = -1;
		highlightSymbolDetected = false;
        rainbow = false;
        wordStyle = NORMAL;

		for (String str : tokens) {

            if (str.equals("\n")) {
				words.add(NEWLINE);
            } else if (str.equals(" ")) {
				words.add(SPACE);
			} else if (str.equals("§")) {
            	highlightSymbolDetected = true;
			} else {
				if (highlightSymbolDetected) {
					if (highlightSymbols.contains(String.valueOf(str.charAt(0)))) {
						highlightColorIndex = highlightSymbols.indexOf(String.valueOf(str.charAt(0)));
					} else if (String.valueOf(str.charAt(0)).equals("k")) {
						wordStyle = wordStyle | RANDOM;
					} else if (String.valueOf(str.charAt(0)).equals("l")) {
						wordStyle = BOLD;
					} else if (String.valueOf(str.charAt(0)).equals("m")) {
						wordStyle = wordStyle | DELETELINE;
					} else if (String.valueOf(str.charAt(0)).equals("u")) {
						wordStyle = UNDERLINE;
					} else if (String.valueOf(str.charAt(0)).equals("o")) {
						wordStyle = ITALIC;
					} else if (String.valueOf(str.charAt(0)).equals("p")) {
						wordStyle = NORMAL;
					} else if (String.valueOf(str.charAt(0)).equals("r")) {
                        rainbow = true;
					} else if (String.valueOf(str.charAt(0)).equals("[")) {

					}
					str = str.substring(1);
					highlightSymbolDetected = false;
				}
                RenderedText word;

                if (rainbow) {
                    word = new RenderedText(str, size) {
                        private float time = 0;
                        @Override
                        public void update() {
                            super.update();
                            rm = (float)Math.sin(-(time += Game.elapsed) / 1);
                            gm = (float)Math.sin((time += Game.elapsed) / 2);
                            bm = (float)Math.sin((time += Game.elapsed) / 3);
                            //rm = (float)Math.sin(-(time += Game.elapsed/2f));
                        }};} else {
                    word = new RenderedText(str, size);
                }
				//原来的高亮 word.hardlight(颜色值);即可
				if (highlightColorIndex != -1) {
					word.hardlight(highlightColors[highlightColorIndex]);
				} else if (color != -1) {
					word.hardlight(color);
				}
				if (wordStyle == UNDERLINE) {
					word.setStyle(RenderedText.UNDERLINE);
				} else if(wordStyle == BOLD) {
					word.setStyle(RenderedText.BOLD);
				} else if(wordStyle == ITALIC) {
                    word.setStyle(RenderedText.ITALIC);
				} else {
                    word.setStyle(RenderedText.NORMAL);
				}
				word.scale.set(zoom);
				words.add(word);
				add(word);

				if (height < word.height()) height = word.height();
			}
		}

		layout();
	}

	public synchronized void zoom(float zoom) {
		this.zoom = zoom;
		for (RenderedText word : words) {
			if (word != null) word.scale.set(zoom);
		}
		layout();
	}

	public synchronized void hardlight(int color) {
		this.color = color;
		for (RenderedText word : words) {
			if (word != null) word.hardlight(color);
		}
	}

	public synchronized void resetColor() {
		this.color = -1;
		for (RenderedText word : words) {
			if (word != null) word.resetColor();
		}
	}

	public synchronized void alpha(float value) {
		for (RenderedText word : words) {
			if (word != null) word.alpha(value);
		}
	}

	public synchronized void invert() {
		if (words != null) {
			for (RenderedText word : words) {
				if (word != null) {
					word.ra = 0.77f;
					word.ga = 0.73f;
					word.ba = 0.62f;
					word.rm = -0.77f;
					word.gm = -0.73f;
					word.bm = -0.62f;
				}
			}
		}
	}

	public synchronized void align(int align) {
		alignment = align;
		layout();
	}

	@Override
	protected synchronized void layout() {
		super.layout();
		float x = this.x;
		float y = this.y;
		float height = 0;
		nLines = 1;

		ArrayList<ArrayList<RenderedText>> lines = new ArrayList<>();
		ArrayList<RenderedText> curLine = new ArrayList<>();
		lines.add(curLine);

		width = 0;
		for (RenderedText word : words) {
                if (word == SPACE) {
                    x += 1.5f;
                } else if (word == NEWLINE) {
                    //newline
                    y += height + 2f;
                    x = this.x;
                    nLines++;
                    curLine = new ArrayList<>();
                    lines.add(curLine);
                } else {
                    if (word.height() > height) height = word.height();

                    if ((x - this.x) + word.width() > maxWidth && !curLine.isEmpty()) {
                        y += height + 2f;
                        x = this.x;
                        nLines++;
                        curLine = new ArrayList<>();
                        lines.add(curLine);
                    }

                    word.x = x;
                    word.y = y;
                    PixelScene.align(word);
                    x += word.width();
                    curLine.add(word);

                    if ((x - this.x) > width) width = (x - this.x);

                    //TODO spacing currently doesn't factor in halfwidth and fullwidth characters
                    //(e.g. Ideographic full stop)
                    x -= spacing;

                }
            }
            this.height = (y - this.y) + height;

            if (alignment != LEFT_ALIGN) {
                for (ArrayList<RenderedText> line : lines) {
                    if (line.size() == 0) continue;
                    float lineWidth = line.get(line.size() - 1).width() + line.get(line.size() - 1).x - this.x;
                    if (alignment == CENTER_ALIGN) {
                        for (RenderedText text : line) {
                            text.x += (width() - lineWidth) / 2f;
                            PixelScene.align(text);
                        }
                    } else if (alignment == RIGHT_ALIGN) {
                        for (RenderedText text : line) {
                            text.x += width() - lineWidth;
                            PixelScene.align(text);
                        }
                    }
                }
            }
        }
 //   }
    public float baseLine() {
        return height();
    }
}
