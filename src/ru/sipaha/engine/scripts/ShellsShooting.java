package ru.sipaha.engine.scripts;

import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.Replicator;
import ru.sipaha.engine.gameobjectdata.Transform;

public class ShellsShooting extends Script {

    public float fireRate = 1f;
    public float timer;
    public Replicator shell;
    public float damage;

    @Override
    public void start(Engine engine) {

    }

    @Override
    public void fixedUpdate(float delta) {
        /*Transform transform = gameObject.transform;
        if(timer < fireRate) {
            timer += delta;
        } else {
            timer = fireRate;
        }
        if(timer >= fireRate && !mm.items[e.id].haveATarget && schm.items[e.id].target != null) {
            timer -= fireRate;
            GameObject shell = Game.engine.createAsEntity(weapon.shell);
            Transform shellTransform = tm.items[shell.id];
            shellTransform.parent = transform;
            shellTransform.reqUnhook = true;
            em.items[shell.id].damage = weapon.damage;
            e.gameObject.animator.start("ShootAnimation");
        }*/
    }

    @Override
    public Script reset() {
        return null;
    }

}
