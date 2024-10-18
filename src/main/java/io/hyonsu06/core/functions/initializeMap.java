package io.hyonsu06.core.functions;

public class initializeMap {
    /*
    // Combined method to initialize and populate a nested Map with dynamic key generation
    public static <K, V> Map<K, Object> initializeAndPopulateNestedMap(
            V value, Function<Integer, K> keyGenerator, Class<?>... classes) {
        return initializeAndPopulateHelper(value, keyGenerator, classes, 0);
    }

    // Overloaded method to initialize and populate a nested Map using any Enum type
    public static <E extends Enum<E>, V> Map<E, Object> initializeAndPopulateNestedMap(
            V value, Class<E> enumClass, Class<?>... classes) {
        return initializeAndPopulateHelper(value, enumClass, classes, 0);
    }

    // Overloaded method to initialize and populate a nested Map using a List of keys
    public static <K, V> Map<K, Object> initializeAndPopulateNestedMap(
            V value, List<K> keys, Class<?>... classes) {
        return initializeAndPopulateHelper(value, keys, classes, 0);
    }

    private static <K, V> Map<K, Object> initializeAndPopulateHelper(
            V value, Object keySource, Class<?>[] classes, int index) {
        if (index >= classes.length) {
            // Base case: Return a new map that holds the leaf value
            Map<K, Object> leafMap = new HashMap<>();
            leafMap.put(null, value); // Using null as a placeholder for the key; adjust as needed
            return leafMap;
        }

        Map<K, Object> map = new HashMap<>();

        // Determine the keys to use based on the provided source
        List<K> keys = new ArrayList<>();
        if (keySource instanceof Class<?>) {
            // If keySource is an enum class, get its values
            if (((Class<?>) keySource).isEnum()) {
                for (Object enumConstant : ((Class<?>) keySource).getEnumConstants()) {
                    keys.add((K) enumConstant);
                }
            }
        } else if (keySource instanceof List<?>) {
            // If keySource is a list, cast it directly
            keys = (List<K>) keySource;
        }

        // Generate keys based on the provided key source
        for (K key : keys) {
            map.put(key, initializeAndPopulateHelper(value, keySource, classes, index + 1));
        }
        return map;
    }

     */
}