/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.appsolve.padelcampus.comparators;

import java.util.Comparator;
import java.util.Map;

/**
 *
 * @author dominik
 * @param <K>
 * @param <V>
 */
public class MapValueComparator<K extends Comparable, V extends Comparable<V>> implements Comparator<K>{
    
    Map<K,V> map;
 
    public MapValueComparator(Map<K, V> map) {
        this.map = map;
    }

    @Override
    public int compare(K keyA, K keyB) {
        V valueA = (V) map.get(keyA);
        V valueB = (V) map.get(keyB);
        int result = valueB.compareTo(valueA);
        //in case the values of a map entry are identical, the key decides
        if (result == 0){
            result = keyA.compareTo(keyB);
        }
        return result;
    }
}
