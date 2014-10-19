package ru.sipaha.engine.scripts;

import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.Script;
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
    public void start(Engine engine) {
        shellTemplate = engine.factory.getTemplate(shellName);
        targetCatcher = gameObject.getScript(TargetCatcher.class);
        if(weaponName != null) {
            weaponTransform = gameObject.getTransform(weaponName);
        } else {
            weaponTransform = gameObject.getTransform();
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
            GameObject shell = shellTemplate.copy();
            shell.getTransform().unhook(weaponTransform);
            gameObject.startAnimation(shootAnimation);
        }
    }

    @Override
    public void reset() {
        fireRate = template.fireRate;
        timer = 0;
        shellTemplate = template.shellTemplate;
        damage = template.damage;
    }

}
