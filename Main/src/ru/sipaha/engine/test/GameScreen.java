package ru.sipaha.engine.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.Sprite;
import ru.sipaha.engine.core.animation.AnimatedFloat;
import ru.sipaha.engine.core.animation.AnimatedSprite;
import ru.sipaha.engine.core.animation.Animation;
import ru.sipaha.engine.test.scripts.*;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.functions.IntFunction;
import ru.sipaha.engine.utils.functions.PiecewiseLinFunction;
import ru.sipaha.engine.utils.signals.Listener;

public class GameScreen implements Screen {
    public final Engine engine = new Engine();

    public GameScreen() {
        createTestObjects(engine);
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

    public static void createTestObjects(final Engine engine) {
        Texture[] textures = new Texture[3];
        for(int i = 0; i < textures.length; i++) {
            textures[i] = new Texture("images/"+Gdx.files.internal((i+1)+".png"));
        }

        TextureRegion arrow = new TextureRegion(new Texture(Gdx.files.internal("images/arrow.png")));//textures[0]);//
        GameObject g = new GameObject();
        g.setTexture(arrow.getTexture());
        Sprite prev = null;
        for(int i = 0; i < 4; i++) {
            Sprite e = new Sprite(arrow);
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
            if(i > 0) e.transform.setPosition(128, 128);
            if(prev != null) e.transform.parent = prev.transform;
            g.addEntity(e);
            prev = e;
        }
        g.transform.setPosition(100, 100);
        g.motion.va = 1;
        g.motion.a_velocity = 10;
        g.zOrder.set(1);
        engine.tagManager.setTag(g, "Enemy");
        engine.tagManager.setTag(g, "Editable");
        //g.createBody(0.85f);

        PiecewiseLinFunction curve = new PiecewiseLinFunction(new Vector2(0,0), new Vector2(4,1), new Vector2(5,0.8f),
                new Vector2(6,1f), new Vector2(7,0.8f), new Vector2(8,1f), new Vector2(12,0));
        Animation animation = new Animation("test", new AnimatedFloat(((Sprite)g.getEntity(0)).colorA, curve)).setLoop(true).start();
        g.addAnimation(animation);
        engine.factory.addTemplate(g, "Name");

        Texture t = new Texture(Gdx.files.internal("images/sprite.png"));
        Array<float[]> frames = new Array<>(true, 5, float[].class);
        Vector2[] points = new Vector2[6];
        for(int i = 0; i < 5; i++) {
            frames.add(new float[]{0, 0.2f*i, 1, 0.2f*(i+1)});
            points[i] = new Vector2(i*3,i);
        }
        points[5] = new Vector2(5*3, 4);
        float[] f = frames.get(0);
        g = new GameObject(new TextureRegion(t, f[0], f[1], f[2], f[3]));
        animation = new Animation("SpriteTest",new AnimatedSprite(((Sprite)g.getEntity(0)).uv, frames, new IntFunction(points)));
        g.addAnimation(animation.start().setLoop(true));
        g.addScript(ShellsShooting.class, new ShellsShooting("Shell", "name", null, 1f));
        g.addScript(TargetHolder.class, new Search("Enemy", 250));
        g.addScript(TargetCatcher.class, new AngleTracking());
        g.zOrder.set(5);
        ((Sprite)g.getEntity(0)).colorA.set(0.5f);
        engine.tagManager.setTag(g,"Editable");
        engine.factory.addTemplate(g, "SpriteTest");

        g = new GameObject(new Texture(Gdx.files.internal("images/4.png")));
        g.motion.xy_velocity = 140;
        g.motion.move_forward = true;
        g.transform.setPosition(0, 70);
        g.life.lifeTime = 4;
        g.life.onLifetimeExpired.add(new Listener<GameObject>() {
            @Override
            public void receive(GameObject object) {
                object.getEngine().factory.create("Explosion").transform.unhook(object.transform);
            }
        });
        g.zOrder.set(10);
        engine.factory.addTemplate(g, "Shell");

        t = new Texture(Gdx.files.internal("images/explosion.png"));
        g = new GameObject(new TextureRegion(t, 96, 96));
        g.transform.setScale(2f);
        frames = new Array<>(true, 17, float[].class);
        points = new Vector2[18];
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < (i<3?5:2); j++) {
                float u = (j*96)/480f;
                float v = (i*96)/384f;
                int frameNum = i*5+j;
                points[frameNum] = new Vector2(0.07f*frameNum, frameNum);
                frames.add(new float[]{u, v, u+96/480f, v+96/384f});
            }
        }
        points[17] = new Vector2(0.07f*17, 16);
        g.addAnimation(new Animation("explosion_animation", new AnimatedSprite(((Sprite)g.getEntity(0)).uv, frames, new IntFunction(points))).start());
        g.life.lifeTime = 1.19f;
        engine.factory.addTemplate(g, "Explosion");


        /*InterfaceLayer interfaceLayer = new InterfaceLayer();
        UIElement element = new UIElement(new Texture(Gdx.files.internal("images/1.png")));
        element.setRightPadding(50);
        element.setTopPadding(50);
        interfaceLayer.add(element);
        engine.renderer.addRenderLayer(interfaceLayer);*/

        engine.initialize();

       /* for(int i = 0; i < 200; i++) {
            GameObject gg = engine.factory.create("Name");
            gg.getTransform().setPosition((float)Math.random()*Gdx.graphics.getWidth(), (float)Math.random()*Gdx.graphics.getHeight());
        }*/

        GameObject gameObject = engine.factory.create("Name");
        gameObject.transform.setPosition(200, 200);
        gameObject.motion.moveTo(600, 600);
        gameObject.getAnimation("test").randomizeTimeOffset();

        gameObject = engine.factory.create("SpriteTest");
        gameObject.transform.setPosition(500, 200);
        //gameObject.startAnimation("Sprite");

        /*engine.input.addProcessor(new InputAdapter(){
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
        });*/
        Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
        engine.renderer.getRenderLayer().camera.setZoom(0.5f);
    }
}
