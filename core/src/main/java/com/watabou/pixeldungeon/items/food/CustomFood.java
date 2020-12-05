package com.watabou.pixeldungeon.items.food;

import com.watabou.utils.JsonHelper;
import com.badlogic.gdx.Gdx;

public class CustomFood extends Food {
    JsonHelper modloader = new JsonHelper(Gdx.files.internal("json/food.json"));
    
    {
        id = modloader.jsonvalue.get(0).getString("id");
        name = modloader.jsonvalue.get(0).getString("name");
        message = modloader.jsonvalue.get(0).getString("message");
        Atlas = modloader.jsonvalue.get(0).getString("atlas");
        image = modloader.jsonvalue.get(0).getInt("image");
        image_size = modloader.jsonvalue.get(0).getInt("image_size");
        energy = modloader.jsonvalue.get(0).getFloat("energy");
    }

    @Override
    public String info() {
        return modloader.jsonvalue.get(0).getString("info");
    }

    @Override
    public int price() {
        return modloader.jsonvalue.get(0).getInt("price") * quantity;
	}
    
}
