package com.watabou.utils;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.files.FileHandle;

/**
 * @author XiYou
 * Created on 12/03/20.
 */

public class JsonHelper {

    public JsonValue jsonvalue;

    public JsonHelper(String str) {
        jsonvalue = new JsonReader().parse(str);
    }
    
    public JsonHelper(String str, int index) {
        jsonvalue = new JsonReader().parse(str).get(index);
    }

    public JsonHelper(FileHandle fileHandle) {
        this(fileHandle.readString());
    }
    
    public JsonHelper(FileHandle fileHandle, int index) {
        this(fileHandle.readString(), index);
    }
    
    public JsonHelper(JsonHelper jsonHelper) {
        jsonvalue = jsonHelper.jsonvalue;
    }
    
    public JsonHelper(JsonValue JsonValue) {
        jsonvalue = JsonValue;
    }

    public JsonValue readJson() {
        return jsonvalue;
	}
    
    public int size(){//JsonValue数组大小不是从0开始计算的, 所以需要-1
        return jsonvalue.size-1;
    }
    
    public JsonValue findjsonbykeyandvalue(String key, String value) {
        for (int i = 0; i < size(); i++) {
            if (jsonvalue.get(i).getString(key) == value) {
                return jsonvalue.get(i);
            }
        }
        return null;
	}

    public JsonValue key2value(String key) {
        return jsonvalue.get(key);
	}

    public JsonValue index2value(int index) {
        return jsonvalue.get(index);
	}

    public int getint(String key) {
        return jsonvalue.getInt(key);
    }

    public String getstr(String key) {
        return jsonvalue.getString(key);
	}

    public boolean getbln(String key) {
        return jsonvalue.getBoolean(key);
	}
    
    public float getflt(String key) {
        return jsonvalue.getFloat(key);
	}

    public int[] getints(String key) {
        JsonValue j = jsonvalue.get(key);
        int[] result = new int[j.size()];
        for (int i = 0; i < j.size(); i++) {
            result[i] = j.getInt(i);
        }
        return result;
    }

    public String[] getstrs(String key) {
        JsonValue j = jsonvalue.get(key);
        String[] result = new String[j.size()];
        for (int i = 0; i < j.size(); i++) {
            result[i] = j.getString(i);
        }
        return result;
    }

    public boolean[] getblns(String key) {
        JsonValue j = jsonvalue.get(key);
        boolean[] result = new boolean[j.size()];
        for (int i = 0; i < j.size(); i++) {
            result[i] = j.getBoolean(i);
        }
        return result;
    }
    
    public float[] getflts(String key) {
        JsonValue j = jsonvalue.get(key);
        float[] result = new float[j.size()];
        for (int i = 0; i < j.size(); i++) {
            result[i] = j.getFloat(i);
        }
        return result;
    }

    public Object[] getobjs(Object obj, String key) {
        JsonValue j = jsonvalue.get(key);
        Object[] result = new Object[j.size()];
        for (int i = 0; i < j.size(); i++) {
            if (obj instanceof int) {
                result[i] = j.getInt(i);
            } else if (obj instanceof String) {
                result[i] = j.getString(i);
            } else if (obj instanceof boolean) {
                result[i] = j.getBoolean(i);
            } else if (obj instanceof float) {
                result[i] = j.getFloat(i);
            } else {return null;}
        }
        return result;
    }

    public static JsonValue readJson(String str) {
        return new JsonReader().parse(str);
	}

    public static JsonValue readJson(FileHandle fileHandle) {
        return readJson(fileHandle.readString());
	}

    public static JsonValue key2value(String json, String key) {
        return new JsonReader().parse(json).get(key);
	}

    public static int getint(String json, String key) {
        return new JsonReader().parse(json).getInt(key);
	}

    public static String getstr(String json, String key) {
        return new JsonReader().parse(json).getString(key);
	}

    public static String save(Object obj) {
        return new Json().toJson(obj);
    }

    public static String save(Class cl) {
        return new Json().toJson(cl);
    }

    public static Object load(Object obj, String str) {
        if (!str.isEmpty()) {
            Json json = new Json();
            return json.fromJson(obj.getClass(), str);
        }
        return null;
    }

    public static Class load(Class cl, String str) {
        if (!str.isEmpty()) {
            Json json = new Json();
            return json.fromJson(cl, str);
        }
        return null;
    }

}
