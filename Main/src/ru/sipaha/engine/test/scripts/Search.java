package ru.sipaha.engine.test.scripts;

import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.Script;
import ru.sipaha.engine.core.Transform;
import ru.sipaha.engine.utils.MathHelper;

public class Search extends Script implements TargetHolder {

    public enum SearchStrategy {LAST, FIRST, NEAREST, STRONGEST}

    public float radius = 0f;
    public GameObject target;
    public float distance;
    public SearchStrategy strategy = SearchStrategy.NEAREST;

    private Search template;
    private Iterable<GameObject> searchTargets;
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
    protected void start(Engine engine) {
        super.start(engine);
    }

    @Override
    public void initialize(Engine engine) {
        super.initialize(engine);
        searchTargets = (Iterable)engine.tagManager.getUnitsWithTag(searchTag);
    }

    @Override
    public void fixedUpdate(float delta) {

        target = null;
        distance = radius * radius;

        for(GameObject target : searchTargets) {
            Transform targetTransform = target.transform;

            float distanceToTarget = MathHelper.sqrDistance(gameObject.transform.x.get(), gameObject.transform.y.get(),
                    targetTransform.x.get(), targetTransform.y.get());

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
    public void reset() {
        radius = template.radius;
        searchTag = template.searchTag;
        target = null;
        distance = template.distance;
        strategy = template.strategy;
    }
}
