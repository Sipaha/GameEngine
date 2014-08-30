package ru.sipaha.engine.scripts;

import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.utils.GameObjectsArray;
import ru.sipaha.engine.utils.MathHelper;

public class Search extends Script {

    public enum SearchStrategy {LAST, FIRST, NEAREST, STRONGEST}
    public Search template;

    public float radius = 0f;
    public GameObjectsArray searchTargets;
    public String searchTag;
    public GameObject target;
    public float distance;
    public SearchStrategy strategy = SearchStrategy.NEAREST;

    public Search(){}

    public Search(Search source) {
        radius = source.radius;
        template = source;
    }

    @Override
    public void start(Engine engine) {
        searchTargets = engine.tagManager.getGameObjectsWithTag(searchTag);
    }

    @Override
    public void fixedUpdate(float delta) {
        Transform transform = gameObject.transform;
        target = null;
        distance = radius * radius;

        GameObject[] targets = searchTargets.items;
        for(GameObject target : targets) {
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
    public Script reset() {
        radius = template.radius;
        searchTag = template.searchTag;
        target = null;
        distance = template.distance;
        strategy = template.strategy;
        return this;
    }
}