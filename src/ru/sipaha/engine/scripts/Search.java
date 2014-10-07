package ru.sipaha.engine.scripts;

import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.utils.GameObjectsArray;
import ru.sipaha.engine.utils.MathHelper;

public class Search extends Script implements TargetHolder {

    public enum SearchStrategy {LAST, FIRST, NEAREST, STRONGEST}

    public float radius = 0f;
    public GameObject target;
    public float distance;
    public SearchStrategy strategy = SearchStrategy.NEAREST;

    private Search template;
    private GameObjectsArray searchTargets;
    private String searchTag;

    public Search(String searchTag){
        this(searchTag, 200);
    }

    public Search(String searchTag, float radius) {
        this.searchTag = searchTag;
        this.radius = radius;
    }

    public Search(Search prototype) {
        template = prototype;
        searchTargets = template.searchTargets;
        reset();
    }

    @Override
    public void initialize(Engine engine) {
        super.initialize(engine);
        searchTargets = engine.tagManager.getGameObjectsWithTag(searchTag);
    }

    @Override
    public void fixedUpdate(float delta) {
        Transform transform = gameObject.transform;
        target = null;
        distance = radius * radius;

        for(GameObject target : searchTargets) {
            Transform targetTransform = target.transform;

            float distanceToTarget = MathHelper.sqrDistance(transform.tx, transform.ty,
                    targetTransform.tx, targetTransform.ty);

            if(distanceToTarget < distance) {
                switch (strategy) {
                    case NEAREST:
                        this.target = target;
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
    public GameObject getTarget() {
        return target;
    }

    @Override
    public float getDistanceToTarget() {
        return distance;
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
