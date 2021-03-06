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

package com.watabou.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PixmapPacker;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.watabou.pixeldungeon.scenes.PixelScene;
import com.watabou.noosa.Game;
import com.watabou.utils.Callback;
import com.watabou.utils.PlatformSupport;

import java.util.HashMap;
import java.util.regex.Pattern;
import com.watabou.utils.PlatformSupport.TextCallback;
import com.watabou.pixeldungeon.PixelDungeon;

public class AndroidPlatformSupport extends PlatformSupport {

    @Override
    public void updateDisplaySize() {
    }

    @Override
    public void updateSystemUI() {
    }

    @Override
    public boolean connectedToUnmeteredNetwork() {
        return false;
    }

    @Override
    public void promptTextInput(String title, String hintText, int maxLen, boolean multiLine, String posTxt, String negTxt, PlatformSupport.TextCallback callback) {
    }

	/* FONT SUPPORT */

	private int pageSize;
	private PixmapPacker packer;
	private boolean systemfont;

	//droid sans / roboto, or a custom pixel font, for use with Latin and Cyrillic languages
	private static FreeTypeFontGenerator basicFontGenerator;
	private static HashMap<Integer, BitmapFont> basicFonts = new HashMap<>();

	//droid sans / nanum gothic / noto sans, for use with Korean
	private static FreeTypeFontGenerator KRFontGenerator;
	private static HashMap<Integer, BitmapFont> KRFonts = new HashMap<>();

	//droid sans / noto sans, for use with Simplified Chinese
	private static FreeTypeFontGenerator SCFontGenerator;
	private static HashMap<Integer, BitmapFont> SCFonts = new HashMap<>();

	//droid sans / noto sans, for use with Japanese
	private static FreeTypeFontGenerator JPFontGenerator;
	private static HashMap<Integer, BitmapFont> JPFonts = new HashMap<>();

	private static FreeTypeFontGenerator BOLDFontGenerator;
	private static HashMap<Integer, BitmapFont> BOLDFonts = new HashMap<>();

	private static FreeTypeFontGenerator ITALICFontGenerator;
	private static HashMap<Integer, BitmapFont> ITALICFonts = new HashMap<>();

	private static FreeTypeFontGenerator BOLDITALICFontGenerator;
	private static HashMap<Integer, BitmapFont> BOLDITALICFonts = new HashMap<>();

	private static HashMap<FreeTypeFontGenerator, HashMap<Integer, BitmapFont>> fonts;

	//special logic for handling korean android 6.0 font oddities
	private static boolean koreanAndroid6OTF = false;

	@Override
	public void setupFontGenerators(int pageSize, boolean systemfont) {
		//don't bother doing anything if nothing has changed
		if (fonts != null && this.pageSize == pageSize && this.systemfont == systemfont) {
			return;
		}
		this.pageSize = pageSize;
		this.systemfont = systemfont;

		if (fonts != null) {
			for (FreeTypeFontGenerator generator : fonts.keySet()) {
				for (BitmapFont f : fonts.get(generator).values()) {
					f.dispose();
				}
				fonts.get(generator).clear();
				generator.dispose();
			}
			fonts.clear();
			if (packer != null) {
				for (PixmapPacker.Page p : packer.getPages()) {
					p.getTexture().dispose();
				}
				packer.dispose();
			}
		}
		fonts = new HashMap<>();
		basicFontGenerator = KRFontGenerator = SCFontGenerator = JPFontGenerator = null;

		if (systemfont && Gdx.files.absolute("/system/fonts/Roboto-Regular.ttf").exists()) {
			basicFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/Roboto-Regular.ttf"));
		} else if (systemfont && Gdx.files.absolute("/system/fonts/DroidSans.ttf").exists()) {
			basicFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/DroidSans.ttf"));
		} else {
			basicFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pixel_font.ttf"));
		}

		//android 7.0+. all asian fonts are nicely contained in one spot
        // if (systemfont){
		if (Gdx.files.absolute("/system/fonts/NotoSansCJK-Regular.ttc").exists()) {
			//typefaces are 0-JP, 1-KR, 2-SC, 3-TC.
			int typeFace;
			switch (PixelDungeon.language()) {
				case "ja":
					typeFace = 0;
					break;
				case "ko":
					typeFace = 1;
					break;
				case "zh":
					typeFace = 2;
                    break;
                case "tc":
                default:
                    typeFace = 3;
			}
			KRFontGenerator = SCFontGenerator = JPFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/NotoSansCJK-Regular.ttc"), typeFace);

            //otherwise we have to go over a few possibilities.
		} else {

			//Korean font generators
			if (Gdx.files.absolute("/system/fonts/NanumGothic.ttf").exists()) {
				KRFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/NanumGothic.ttf"));
			} else if (Gdx.files.absolute("/system/fonts/NotoSansKR-Regular.otf").exists()) {
				KRFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/NotoSansKR-Regular.otf"));
				koreanAndroid6OTF = true;
			}

			//Chinese font generators
			if (Gdx.files.absolute("/system/fonts/NotoSansSC-Regular.otf").exists()) {
				SCFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/NotoSansSC-Regular.otf"));
			} else if (Gdx.files.absolute("/system/fonts/NotoSansHans-Regular.otf").exists()) {
				SCFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/NotoSansHans-Regular.otf"));
			}

			//Japaneses font generators
			if (Gdx.files.absolute("/system/fonts/NotoSansJP-Regular.otf").exists()) {
				JPFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/NotoSansJP-Regular.otf"));
			}

			//set up a fallback generator for any remaining fonts
			FreeTypeFontGenerator fallbackGenerator;
			if (Gdx.files.absolute("/system/fonts/DroidSansFallback.ttf").exists()) {
				fallbackGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/DroidSansFallback.ttf"));
			} else {
				//no fallback font, just set to null =/
				fallbackGenerator = null;
			}

			if (KRFontGenerator == null) KRFontGenerator = fallbackGenerator;
			if (SCFontGenerator == null) SCFontGenerator = fallbackGenerator;
			if (JPFontGenerator == null) JPFontGenerator = fallbackGenerator;

		}
        // }else{KRFontGenerator = SCFontGenerator = JPFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/pixel_font.ttf"),2);}


        if (Gdx.files.absolute("/system/fonts/DroidSans-Bold.ttf").exists()) {
            BOLDFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/DroidSans-Bold.ttf"));
        } else if (Gdx.files.absolute("/system/fonts/Roboto-Bold.ttf").exists()) {
            BOLDFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/Roboto-Bold.ttf"));
        }

        if (Gdx.files.absolute("/system/fonts/NotoSerif-Italic.ttf").exists()) {
            ITALICFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/NotoSerif-Italic.ttf"));
        } else if (Gdx.files.absolute("/system/fonts/Roboto-Italic.ttf").exists()) {
            ITALICFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/Roboto-Italic.ttf"));
        } 

        if (Gdx.files.absolute("/system/fonts/NotoSerif-BoldItalic.ttf").exists()) {
            BOLDITALICFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/NotoSerif-BoldItalic.ttf"));
        } else if (Gdx.files.absolute("/system/fonts/Roboto-BoldItalic.ttf").exists()) {
            BOLDITALICFontGenerator = new FreeTypeFontGenerator(Gdx.files.absolute("/system/fonts/Roboto-BoldItalic.ttf"));
        } 

		if (basicFontGenerator != null) fonts.put(basicFontGenerator, basicFonts);
		if (KRFontGenerator != null) fonts.put(KRFontGenerator, KRFonts);
		if (SCFontGenerator != null) fonts.put(SCFontGenerator, SCFonts);
		if (JPFontGenerator != null) fonts.put(JPFontGenerator, JPFonts);
		if (BOLDFontGenerator != null) fonts.put(BOLDFontGenerator, BOLDFonts);
		if (ITALICFontGenerator != null) fonts.put(ITALICFontGenerator, ITALICFonts);
		if (BOLDITALICFontGenerator != null) fonts.put(BOLDITALICFontGenerator, BOLDITALICFonts);

		//would be nice to use RGBA4444 to save memory, but this causes problems on some gpus =S
		packer = new PixmapPacker(pageSize, pageSize, Pixmap.Format.RGBA8888, 1, false);
	}

	@Override
	public void resetGenerators() {
		if (fonts != null) {
			for (FreeTypeFontGenerator generator : fonts.keySet()) {
				for (BitmapFont f : fonts.get(generator).values()) {
					f.dispose();
				}
				fonts.get(generator).clear();
				generator.dispose();
			}
			fonts.clear();
			if (packer != null) {
				for (PixmapPacker.Page p : packer.getPages()) {
					p.getTexture().dispose();
				}
				packer.dispose();
			}
			fonts = null;
		}
		setupFontGenerators(pageSize, systemfont);
	}

	private static Pattern KRMatcher = Pattern.compile("\\p{InHangul_Syllables}");
	private static Pattern SCMatcher = Pattern.compile("\\p{InCJK_Unified_Ideographs}|\\p{InCJK_Symbols_and_Punctuation}|\\p{InHalfwidth_and_Fullwidth_Forms}");
	private static Pattern JPMatcher = Pattern.compile("\\p{InHiragana}|\\p{InKatakana}");

	private static FreeTypeFontGenerator getGeneratorForString(String input) {
		if (KRMatcher.matcher(input).find()) {
			return KRFontGenerator;
		} else if (SCMatcher.matcher(input).find()) {
			return SCFontGenerator;
		} else if (JPMatcher.matcher(input).find()) {
			return JPFontGenerator;
		} else {
			return basicFontGenerator;
		}
	}

	@Override
	public BitmapFont getFont(int size, String text) {
		return getFont(size, text, getGeneratorForString(text));
	}

	public BitmapFont getBoldFont(int size, String text) {
        return getFont(size, text, BOLDITALICFontGenerator);
	}

	public BitmapFont getItalicFont(int size, String text) {
        return getFont(size, text, ITALICFontGenerator);
	}

	public BitmapFont getBoldItalicFont(int size, String text) {
        return getFont(size, text, BOLDITALICFontGenerator);
	}

	public BitmapFont getFont(int size, String text, FreeTypeFontGenerator generator) {
		if (generator == null) {
			return null;
		}

		if (!fonts.get(generator).containsKey(size)) {
			FreeTypeFontGenerator.FreeTypeFontParameter parameters = new FreeTypeFontGenerator.FreeTypeFontParameter();
			parameters.size = size;
			parameters.flip = true;
			parameters.borderWidth = parameters.size / 10f;
			parameters.renderCount = 3;
			parameters.hinting = FreeTypeFontGenerator.Hinting.None;
			parameters.spaceX = -(int) parameters.borderWidth;
			parameters.incremental = true;
			if (generator == basicFontGenerator) {
				//if we're using latin/cyrillic, we can safely pre-generate some common letters
				//(we define common as >4% frequency in english)
				parameters.characters = "�etaoinshrdl";
			} else {
				parameters.characters = "�";
			}
			parameters.packer = packer;

			try {
				BitmapFont font = generator.generateFont(parameters);
				font.getData().missingGlyph = font.getData().getGlyph('�');
				fonts.get(generator).put(size, font);
			} catch ( Exception e ) {
				Gdx.app.log("字体测试", e.toString());
				return null;
			}
		}

		return fonts.get(generator).get(size);
	}

	//splits on newlines, underscores, and chinese/japaneses characters
	private Pattern regularsplitter = Pattern.compile(
        "(?<=\n)|(?=\n)|(?<=§)|(?=§)|" +
        "(?<=\\p{InHiragana})|(?=\\p{InHiragana})|" +
        "(?<=\\p{InKatakana})|(?=\\p{InKatakana})|" +
        "(?<=\\p{InCJK_Unified_Ideographs})|(?=\\p{InCJK_Unified_Ideographs})|" +
        "(?<=\\p{InCJK_Symbols_and_Punctuation})|(?=\\p{InCJK_Symbols_and_Punctuation})|" +
        "(?<=\\p{InHalfwidth_and_Fullwidth_Forms})|(?=\\p{InHalfwidth_and_Fullwidth_Forms})");

	//additionally splits on words, so that each word can be arranged individually
	private Pattern regularsplitterMultiline = Pattern.compile(
        "(?<= )|(?= )|(?<=\n)|(?=\n)|(?<=§)|(?=§)|" +
        "(?<=\\p{InHiragana})|(?=\\p{InHiragana})|" +
        "(?<=\\p{InKatakana})|(?=\\p{InKatakana})|" +
        "(?<=\\p{InCJK_Unified_Ideographs})|(?=\\p{InCJK_Unified_Ideographs})|" +
        "(?<=\\p{InCJK_Symbols_and_Punctuation})|(?=\\p{InCJK_Symbols_and_Punctuation})|" +
        "(?<=\\p{InHalfwidth_and_Fullwidth_Forms})|(?=\\p{InHalfwidth_and_Fullwidth_Forms})");

	//splits on each non-hangul character. Needed for weird android 6.0 font files
	private Pattern android6KRSplitter = Pattern.compile(
        "(?<= )|(?= )|(?<=\n)|(?=\n)|(?<=§)|(?=§)|" +
        "(?!\\p{InHangul_Syllables})|(?<!\\p{InHangul_Syllables})");

	@Override
	public String[] splitforTextBlock(String text, boolean multiline) {
		if (koreanAndroid6OTF && getGeneratorForString(text) == KRFontGenerator) {
			return android6KRSplitter.split(text);
		} else if (multiline) {
			return regularsplitterMultiline.split(text);
		} else {
			return regularsplitter.split(text);
		}
	}

}
