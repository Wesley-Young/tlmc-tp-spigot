package cn.timelessmc.teleport.home;

import org.jetbrains.annotations.NotNull;

import java.util.Properties;
import java.util.TreeMap;

public class HomeEntrySubMap extends TreeMap<String, HomeEntry> {
    public HomeEntrySubMap() {
    }

    public HomeEntrySubMap(@NotNull Properties rawMap) {
        rawMap.forEach((key, value) -> this.put((String) key, HomeEntry.parse((String) value)));
    }

    public Properties toProperties() {
        Properties res = new Properties();
        this.forEach((key, value) -> res.put(key, value.toString()));
        return res;
    }
}
