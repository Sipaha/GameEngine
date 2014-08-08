package ru.frozen.prolen;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import ru.frozen.prolen.core.gameobject.MeshRenderer;
import ru.frozen.prolen.core.gameobject.Motion;
import ru.frozen.prolen.core.gameobject.Transform;
import ru.frozen.prolen.core.gameobject.GameObject;
import ru.frozen.prolen.core.graphics.SceneRenderer;

public class GameScreen implements Screen {
    SceneRenderer renderer = new SceneRenderer();

    int t = 0, l = 0;
    GameObject m;
    GameObject shell;
    Array<GameObject> shells = new Array<>();

    public GameScreen() {
        Gdx.gl.glClearColor(0.5f,0.5f,0.5f,1f);

        TextureRegion morda = new TextureRegion(new Texture(Gdx.files.internal("morda.png")));
        TextureRegion two = new TextureRegion(new Texture(Gdx.files.internal("2.png")));
        TextureRegion four_cher = new TextureRegion(new Texture(Gdx.files.internal("4cher.png")));
        TextureRegion four_cherkras = new TextureRegion(new Texture(Gdx.files.internal("4cherkras.png")));

        m = createMorda(morda, 5);
        m.motion.xy_velocity = 20;
        m.motion.moveTo(200, 200);
        m.motion.a_velocity = 20;
        m.motion.rotateTo(100);
        renderer.addGO(m);

        shell = createMorda(two,6);
        shell.transform.setPosition(0, 30);
        shell.motion.xy_velocity = 20;
        shell.motion.move_forward = true;
    }

    public GameObject createMorda(TextureRegion t,int z_order) {
        GameObject g = new GameObject("test");
        g.transform = new Transform();
        g.transform.setPosition((float) Math.random() * Gdx.graphics.getWidth(),
                (float) Math.random() * Gdx.graphics.getHeight());
        g.renderer = new MeshRenderer(t,null,z_order);
        g.renderer.setLinearFilter();
        g.motion = new Motion();
        g.updateData(0.2f);
        return g;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        m.updateData(delta);
        for (int i = 0; i < shells.size; i++) shells.get(i).updateData(delta);
        if(!m.motion.haveXYTarget) {
            switch (t) {
                case 0: m.motion.moveTo(100,100); break;
                case 1: m.motion.moveTo(300,100); break;
                case 2: m.motion.moveTo(5, 400); break;
            }
            t++;
        }
        if(!m.motion.haveATarget) {
            switch (l) {
                case 0:
                    m.motion.rotateTo(30);
                    GameObject s = shell.copy();
                    renderer.addGO(s);
                    shells.add(s);
                    m.shoot(s);
                    break;
                case 1:
                    m.motion.rotateTo(30);
                    s = shell.copy();
                    renderer.addGO(s);
                    shells.add(s);
                    m.shoot(s);
                    break;
                case 2:
                    m.motion.rotateTo(30);
                    s = shell.copy();
                    renderer.addGO(s);
                    shells.add(s);
                    m.shoot(s);
                    break;
            }
            l++;
        }
        renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
