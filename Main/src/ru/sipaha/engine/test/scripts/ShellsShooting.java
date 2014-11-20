package ru.sipaha.engine.test.scripts;

import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.Script;
import ru.sipaha.engine.core.Transform;
import ru.sipaha.engine.core.animation.Animation;

public class ShellsShooting extends Script {

    public float fireRate = 1f;
    public float damage;

    private ShellsShooting template;

    private float timer;

    private String shellName;
    private String weaponName;
    private String shootAnimationName;

    private TargetCatcher targetCatcher;
    private Transform weaponTransform;
    private GameObject shellTemplate;
    private Animation shootAnimation;

    public ShellsShooting(String shellName, String weaponName) {
        this(shellName, weaponName, null, 1);
    }

    public ShellsShooting(String shellName, String weaponName, String shootAnimationName, float fireRate) {
        this.shellName = shellName;
        this.weaponName = weaponName;
        this.shootAnimationName = shootAnimationName;
        this.fireRate = fireRate;
    }

    public ShellsShooting(ShellsShooting prototype) {
        shellName = prototype.shellName;
        template = prototype;
        reset();
    }

    @Override
    public void initialize(Engine engine) {
        shellTemplate = engine.factory.getTemplate(shellName);
    }

    @Override
    public void start(Engine engine) {
        targetCatcher = gameObject.getScript(TargetCatcher.class);
        if(weaponName != null) {
            weaponTransform = gameObject.getEntity(weaponName).transform;
        } else {
            weaponTransform = gameObject.transform;
        }
        if(shootAnimationName != null) shootAnimation = gameObject.getAnimation(shootAnimationName);
    }

    @Override
    public void fixedUpdate(float delta) {
        if(timer < fireRate) {
            timer += delta;
        } else {
            timer = fireRate;
        }
        if(timer >= fireRate && targetCatcher.targetIsCatched()) {
            timer -= fireRate;
            GameObject shell = shellTemplate.getCopy();
            shell.transform.unhook(weaponTransform);
            if(shootAnimation != null) shootAnimation.start();
        }
    }

    @Override
    public void reset() {
        fireRate = template.fireRate;
        timer = 0;
        damage = template.damage;
    }

}
