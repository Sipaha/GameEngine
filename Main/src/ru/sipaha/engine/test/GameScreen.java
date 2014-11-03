package ru.sipaha.engine.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.core.animation.AnimatedFloat;
import ru.sipaha.engine.core.animation.Animation;
import ru.sipaha.engine.test.scripts.*;
import ru.sipaha.engine.utils.curves.PiecewiseLinCurve;
import ru.sipaha.engine.utils.signals.Listener;
import ru.sipaha.engine.utils.structures.SpriteFrame;

public class GameScreen implements Screen {
    final Engine engine = new Engine();

    public GameScreen() {
        Texture[] textures = new Texture[3];
        for(int i = 0; i < textures.length; i++) {
            textures[i] = new Texture("images/"+Gdx.files.internal((i+1)+".png"));
        }

        TextureRegion arrow = new TextureRegion(new Texture(Gdx.files.internal("images/arrow.png")));//textures[0]);//
        GameObject g = new GameObject();
        g.setTexture(arrow.getTexture());
        Entity prev = null;
        for(int i = 0; i < 4; i++) {
            Entity e = new Entity(arrow);
            e.setName(Integer.toString(i));
            float u,v,u2,v2;
            switch (i) {
                case 0: u = 0; v = 0; u2 = 0.5f; v2 = 0.5f; break;
                case 1: u = 0.5f; v = 0; u2 = 1f; v2 = 0.5f; break;
                case 2: u = 0.5f; v = 0.5f; u2 = 1f; v2 = 1f; break;
                case 3: u = 0; v = 0.5f; u2 = 0.5f; v2 = 1f; break;
                default: u = 0; v = 0; u2 = 0; v2 = 0;
            }
            e.uv.set(new float[]{u, v, u2, v2});
            e.transform.setPosition(128, 128);
            e.setParent(prev);
            g.addEntity(e);
            prev = e;
        }
        g.setLinearFilter();
        g.getTransform().setPosition(100, 100);
        g.getTransform().motion.va = 1;
        g.getTransform().motion.a_velocity = 10;
        g.setZOrder(1);
        engine.tagManager.setTag(g, "Enemy");
        //g.createBody(0.85f);

        PiecewiseLinCurve curve = new PiecewiseLinCurve(new Vector2(0,0), new Vector2(4,1), new Vector2(5,0.8f),
                                                        new Vector2(6,1f), new Vector2(7,0.8f), new Vector2(8,1f), new Vector2(12,0));
        Animation animation = new Animation("test", new AnimatedFloat(g.getEntity(0).colorA, curve)).setLoop(true).start();
        g.addAnimation(animation);
        engine.factory.addTemplate(g, "Name");

        Texture t = new Texture(Gdx.files.internal("images/sprite.png"));
        SpriteFrame[] frames = new SpriteFrame[5];
        for(int i = 0; i < frames.length;i++) frames[i] = new SpriteFrame(i*i+1, 0, 0.2f*i, 1, 0.2f*(i+1));
        //AnimationProperties animation = new SpriteAnimation("Sprite", frames).setLoop(true).setPauseTime(3f).setPingPong(true);
        g = new GameObject(new TextureRegion(t, frames[0].u, frames[0].v, frames[0].u2, frames[0].v2));
        //g.addAnimation(animation);
        g.addScript(ShellsShooting.class, new ShellsShooting("Shell", "name", null, 1f));
        g.addScript(TargetHolder.class, new Search("Enemy", 250));
        g.addScript(TargetCatcher.class, new AngleTracking());
        g.setZOrder(5);
        engine.factory.addTemplate(g, "SpriteTest");

        g = new GameObject(new Texture(Gdx.files.internal("images/4.png")));
        g.getTransform().motion.xy_velocity = 140;
        g.getTransform().motion.move_forward = true;
        g.getTransform().setPosition(0, 70);
        g.life.lifeTime = 4;
        g.life.onLifetimeExpired.add(new Listener<GameObject>() {
            @Override
            public void receive(GameObject object) {
                engine.factory.create("Explosion").getTransform().unhook(object.getTransform());
            }
        });
        g.setZOrder(10);
        engine.factory.addTemplate(g, "Shell");

        t = new Texture(Gdx.files.internal("images/explosion.png"));
        g = new GameObject(new TextureRegion(t, 96, 96));
        g.setLinearFilter();
        g.getTransform().setScale(2f);
        frames = new SpriteFrame[17];
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < (i<3?5:2); j++) {
                float u = (j*96)/480f;
                float v = (i*96)/384f;
                int frameNum = i*5+j;
                frames[frameNum] = new SpriteFrame(0.07f*frameNum+0.07f, u, v, u+96/480f, v+96/384f);
            }
        }
        //g.addAnimation(new SpriteAnimation("explosion_animation", frames), true);
        g.life.lifeTime = 1.19f;
        engine.factory.addTemplate(g, "Explosion");


        /*InterfaceLayer interfaceLayer = new InterfaceLayer();
        UIElement element = new UIElement(new Texture(Gdx.files.internal("images/1.png")));
        element.setRightPadding(50);
        element.setTopPadding(50);
        interfaceLayer.add(element);
        engine.renderer.addRenderLayer(interfaceLayer);*/

        engine.initialize();

        for(int i = 0; i < 200; i++) {
            GameObject gg = engine.factory.create("Name");
            gg.getTransform().setPosition((float)Math.random()*Gdx.graphics.getWidth(), (float)Math.random()*Gdx.graphics.getHeight());
        }

        GameObject gameObject = engine.factory.create("Name");
        gameObject.getTransform().setPosition(200, 200);
        gameObject.getTransform().motion.moveTo(600, 600);
        gameObject.getAnimation("test").randomizeTimeOffset();

        gameObject = engine.factory.create("SpriteTest");
        gameObject.getTransform().setPosition(500, 200);
        //gameObject.startAnimation("Sprite");

        engine.input.addProcessor(new InputAdapter(){
            int oldX,oldY;
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                oldX = screenX;
                oldY = screenY;
                return false;
            }
            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                int deltaX = oldX - screenX;
                int deltaY = screenY - oldY;
                engine.renderer.getRenderLayer().camera.moveWithZoom(deltaX, deltaY);
                oldX = screenX;
                oldY = screenY;
                return false;
            }
        });
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        engine.renderer.getRenderLayer().camera.setZoom(0.5f);
    }

    @Override
    public void render(float delta) {
        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        engine.renderer.resize(width, height);
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
