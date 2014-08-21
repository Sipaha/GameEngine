package ru.sipaha.engine.scripts;

import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.MathHelper;

public class Search extends Script {

    public enum SearchStrategy {LAST, FIRST, NEAREST, STRONGEST}

    public float radius = 0f;
    public Array<GameObject> searchTargets;
    public String searchTag;
    public GameObject target;
    public float distance;
    public SearchStrategy strategy = SearchStrategy.NEAREST;

    @Override
    public void start(Engine engine) {
        searchTargets = engine.tagManager.getGameObjectsWithTag(searchTag);
    }

    @Override
    public void fixedUpdate(float delta) {
        Transform transform = go.transform;
        target = null;
        distance = radius * radius;

        GameObject[] targets = searchTargets.items;
        for(int i = 0, s = searchTargets.size; i < s; i++) {
            GameObject target = targets[i];
            Transform targetTransform = target.transform;

            float distanceToTarget = MathHelper.sqrDistance(transform.tx, transform.ty,
                                                targetTransform.tx, targetTransform.ty);

            if(distanceToTarget < distance) {
                switch (strategy) {
                    case NEAREST:
                        distance = distanceToTarget;
                        break;
                    case LAST:
                        //todo
                        break;
                    case FIRST:
                        //todo
                        break;
                    case STRONGEST:
                        //todo
                        break;
                }
            }
        }
        if(target != null) distance = (float) Math.sqrt(distance);
    }

    @Override
    public void set(Script source) {
        Search search = (Search) source;
        radius = search.radius;
        searchTag = search.searchTag;
        target = null;
        distance = search.distance;
        strategy = search.strategy;
    }
}
