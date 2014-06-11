package za.co.entelect.challenge.ai.gametree;

import java.util.HashMap;
import java.util.Map;

/**
* Created by leonardseymore on 2014/05/01.
*/
class TranspositionTable {
    Map<Long, TranspositionEntry> map = new HashMap<>();

    public TranspositionEntry get(long hash) {
        return map.get(hash);
    }

    public void put(long hash, TranspositionEntry entry) {
        if (map.containsKey(hash)) {
            TranspositionEntry existing = map.get(hash);
            if (entry.depth < existing.depth) {
                existing.depth = entry.depth;
            }
        } else {
            map.put(hash, entry);
        }
    }

    public int size() {
        return map.size();
    }
}
