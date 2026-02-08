//: spring.boot.di.domain.model.jdk25.collections.sequenced.TreeMapAsSequenced.java

package spring.boot.di.domain.model.jdk25.collections.sequenced;


import static java.util.Collections.unmodifiableSequencedMap;

import java.util.Map;
import java.util.SequencedMap;
import java.util.TreeMap;


class TreeMapAsSequenced {

    static void main() {

        SequencedMap<Character, Integer> treeMap = new TreeMap<>();
        treeMap.put('k', 1);
        treeMap.put('m', 2);
        SequencedMap<Character, Integer> u = unmodifiableSequencedMap(treeMap);

        treeMap.put('m', 3);
        treeMap.putFirst('m', 4);
        treeMap.putLast('x', 5);

        treeMap.replaceAll((k,v) -> v + v);
        u.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .forEach(System.out::print);
    }

} /// :~