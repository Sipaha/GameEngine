package ru.sipaha.engine.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.core.animation.Animation;
import ru.sipaha.engine.core.animation.discrete.SpriteAnimation;
import ru.sipaha.engine.core.animation.сontinuous.ContinuousAnimation;
import ru.sipaha.engine.core.animation.сontinuous.AnimatedAlpha;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.graphics.Camera;
import ru.sipaha.engine.scripts.*;
import ru.sipaha.engine.utils.curves.PiecewiseLinCurve;
import ru.sipaha.engine.utils.signals.Listener;
import ru.sipaha.engine.utils.structures.SpriteFrame;

import java.lang.reflect.AnnotatedType;
import java.math.BigDecimal;

public class GameScreen implements Screen {
    final Engine engine = new Engine();

    public GameScreen() {
        Gdx.gl.glClearColor(0.5f,0.5f,0.5f,1f);

        Texture[] textures = new Texture[3];
        for(int i = 0; i < textures.length; i++) {
            textures[i] = new Texture("images/"+Gdx.files.internal((i+1)+".png"));
        }


        TextureRegion arrow = new TextureRegion(new Texture(Gdx.files.internal("images/arrow.png")));//textures[0]);//
        GameObject g = new GameObject();
        g.setTexture(arrow.getTexture());
        for(int i = 0; i < 4; i++) {
            Entity e = new Entity(arrow);
            e.name = Integer.toString(i);
            float u,v,u2,v2;
            switch (i) {
                case 0: u = 0; v = 0; u2 = 0.5f; v2 = 0.5f; break;
                case 1: u = 0.5f; v = 0; u2 = 1f; v2 = 0.5f; break;
                case 2: u = 0.5f; v = 0.5f; u2 = 1f; v2 = 1f; break;
                case 3: u = 0; v = 0.5f; u2 = 0.5f; v2 = 1f; break;
                default: u = 0; v = 0; u2 = 0; v2 = 0;
            }
            e.renderer.setUV(u,v,u2,v2);
            Transform transform = new Transform().setPosition(128,128);
            transform.parentId = i-1;
            e.transformId = i;
            g.addEntity(e);
            g.addTransform(transform);
        }
        g.renderer.setLinearFilter();
        g.transform.setPosition(100, 100);
        g.transform.motion.va = 1;
        g.transform.motion.a_velocity = 10;
        engine.tagManager.setTag(g, "Enemy");
        //g.createBody(0.85f);

        PiecewiseLinCurve curve = new PiecewiseLinCurve(new Vector2(0,0), new Vector2(5,1), new Vector2(10,0));
        AnimatedAlpha animatedAlpha = new AnimatedAlpha(curve);
        Animation animation = new ContinuousAnimation("Test",animatedAlpha).setPauseTime(3f);
        g.addAnimation(animation);
        engine.setReplicator(g, "Name");

        Texture t = new Texture(Gdx.files.internal("images/sprite.png"));
        SpriteFrame[] frames = new SpriteFrame[5];
        for(int i = 0; i < frames.length;i++) frames[i] = new SpriteFrame(i*i+1, 0, 0.2f*i, 1, 0.2f*(i+1));
        animation = new SpriteAnimation("Sprite", frames).setLoop(true).setPauseTime(3f).setNeedBackMove(true);
        g = new GameObject(new TextureRegion(t,frames[0].u,frames[0].v,frames[0].u2,frames[0].v2),8);
        g.addAnimation(animation);
        g.createBody(1);
        g.addScript(ShellsShooting.class, new ShellsShooting("Shell", "name", null, 3f));
        g.addScript(TargetHolder.class, new Search("Enemy", 250));
        g.addScript(TargetCatcher.class, new AngleTracking());
        engine.setReplicator(g, "SpriteTest");

        g = new GameObject(new Texture(Gdx.files.internal("images/4.png")));
        g.transform.motion.xy_velocity = 140;
        g.transform.motion.move_forward = true;
        g.transform.setPosition(0, 70);
        g.life.lifeTime = 4;
        g.life.onLifetimeExpired.add(new Listener<GameObject>() {
            @Override
            public void receive(GameObject object) {
                engine.createGameObject("Explosion").transform.unhook(object.transform);
            }
        });
        engine.setReplicator(g, "Shell");

        t = new Texture(Gdx.files.internal("images/explosion.png"));
        g = new GameObject(new TextureRegion(t, 96, 96));
        g.transform.setScale(2f);
        frames = new SpriteFrame[17];
        for(int i = 0; i < 4; i++) {
            for(int j = 0; j < (i<3?5:2); j++) {
                float u = (j*96)/480f;
                float v = (i*96)/384f;
                int frameNum = i*5+j;
                frames[frameNum] = new SpriteFrame(0.07f*frameNum+0.07f, u, v, u+96/480f, v+96/384f);
            }
        }
        g.addAnimation(new SpriteAnimation("explosion_animation", frames), true);
        g.life.lifeTime = 1.19f;
        engine.setReplicator(g, "Explosion");

        engine.initialize();

        /*for(int i = 0; i < 400; i++) {
            GameObject gg = engine.createGameObject("Name");
            gg.transform.setPosition(Math.random()*Gdx.graphics.getWidth(), Math.random()*Gdx.graphics.getHeight());
        }*/

        GameObject gameObject = engine.createGameObject("Name");
        gameObject.transform.setPosition(200, 200);
        gameObject.transform.motion.moveTo(600, 600);
        gameObject.startAnimation("Test");

        gameObject = engine.createGameObject("SpriteTest");
        gameObject.transform.setPosition(500,200);
        gameObject.startAnimation("Sprite");

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
                engine.getRenderLayer().camera.moveWithZoom(deltaX, deltaY);
                oldX = screenX;
                oldY = screenY;
                return false;
            }
        });
        engine.getRenderLayer().camera.setZoom(2f);
        //engine.getRenderLayer().camera.setViewLimits(0, 0, 1000, 1000);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        engine.getRenderLayer().camera.setViewport(width, height);
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
