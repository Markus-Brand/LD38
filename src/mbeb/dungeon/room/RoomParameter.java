package mbeb.dungeon.room;

import java.util.EnumMap;
import java.util.Map;

public class RoomParameter {

    public enum Type {
        LEFT_NEIGHBOUR,
        RIGHT_NEIGHBOUR,
        TOP_NEIGHBOUR,
        BOTTOM_NEIGHBOUR;
    }

    private Map<Type, Boolean> values = new EnumMap<>(Type.class);

    public boolean contains(Type key) {
        return values.containsKey(key);
    }

    public boolean get(Type key) {
        return values.get(key) == null ? false : values.get(key);
    }

    public void set(Type key, Boolean value) {
        values.put(key, value);
    }

    public void unset(Type key) {
		values.remove(key);
    }

    public void clear() {
        values.clear();
    }
}
