package ru.sipaha.engine.scripts;

import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.core.Replicator;
import ru.sipaha.engine.core.animation.Animation;
import ru.sipaha.engine.gameobjectdata.Transform;

public class ShellsShooting extends Script {

    public float fireRate = 1f;
    public float damage;

    private ShellsShooting template;

    private float timer;

    private String shellName;
    private String weaponName;
    private String shootAnimationName;

    private Search search;
    private Transform weaponTransform;
    private Replicator shellReplicator;
    private Animation shootAnimation;

    public ShellsShooting(String shellName, String weaponName, String shootAnimationName) {
        this.shellName = shellName;
        this.weaponName = weaponName;
        this.shootAnimationName = shootAnimationName;
    }

    public ShellsShooting(ShellsShooting prototype) {
        shellName = prototype.shellName;
        shellReplicator = prototype.shellReplicator;
        template = prototype;
        reset();
    }

    @Override
    public void start(Engine engine) {
        shellReplicator = engine.getReplicator(shellName);
        search = gameObject.getScript(Search.class);
        weaponTransform = gameObject.getTransform(weaponName);
        if(shootAnimationName != null) shootAnimation = gameObject.getAnimation(shootAnimationName);
    }

    @Override
    public void fixedUpdate(float delta) {
        Transform transform = gameObject.transform;
        if(timer < fireRate) {
            timer += delta;
        } else {
            timer = fireRate;
        }
        if(timer >= fireRate && !transform.motion.haveATarget && search.target != null) {
            timer -= fireRate;
            GameObject shell = shellReplicator.get();
            shell.transform.update(weaponTransform, 0f);
            gameObject.startAnimation(shootAnimation);
        }
    }

    @Override
    public Script reset() {
        fireRate = template.fireRate;
        timer = 0;
        shellReplicator = template.shellReplicator;
        damage = template.damage;
        return this;
    }

}
