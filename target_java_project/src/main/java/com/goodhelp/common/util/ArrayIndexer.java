package com.goodhelp.common.util;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility for indexing collections by a key.
 * Migrated from PHP: App\Common\Service\ArrayIndexer
 */
@Component
public class ArrayIndexer {
    
    /**
     * Index a list by a key, returning a map with unique keys.
     * If duplicate keys exist, the last value wins.
     * 
     * @param list List to index
     * @param keyExtractor Function to extract the key from each element
     * @return Map indexed by the extracted key
     */
    public <K, V> Map<K, V> byKeyUnique(List<V> list, Function<V, K> keyExtractor) {
        return list.stream()
            .collect(Collectors.toMap(
                keyExtractor,
                Function.identity(),
                (existing, replacement) -> replacement,
                LinkedHashMap::new
            ));
    }
    
    /**
     * Index a list of maps by a string key.
     * 
     * @param list List of maps
     * @param keyName Name of the key field
     * @return Map indexed by the key value
     */
    public <V> Map<Object, Map<String, V>> byKeyUnique(List<Map<String, V>> list, String keyName) {
        Map<Object, Map<String, V>> result = new LinkedHashMap<>();
        for (Map<String, V> item : list) {
            Object key = item.get(keyName);
            if (key != null) {
                result.put(key, item);
            }
        }
        return result;
    }
    
    /**
     * Group a list by a key, returning a map with lists as values.
     * 
     * @param list List to group
     * @param keyExtractor Function to extract the key from each element
     * @return Map with lists grouped by key
     */
    public <K, V> Map<K, List<V>> byKey(List<V> list, Function<V, K> keyExtractor) {
        return list.stream()
            .collect(Collectors.groupingBy(
                keyExtractor,
                LinkedHashMap::new,
                Collectors.toList()
            ));
    }
    
    /**
     * Group a list of maps by a string key.
     * 
     * @param list List of maps
     * @param keyName Name of the key field
     * @return Map with lists grouped by key
     */
    public <V> Map<Object, List<Map<String, V>>> byKey(List<Map<String, V>> list, String keyName) {
        Map<Object, List<Map<String, V>>> result = new LinkedHashMap<>();
        for (Map<String, V> item : list) {
            Object key = item.get(keyName);
            if (key != null) {
                result.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
            }
        }
        return result;
    }
}
