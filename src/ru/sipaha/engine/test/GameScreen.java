package ru.sipaha.engine.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.Entity;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.scripts.Script;
import ru.sipaha.engine.utils.ContactListener;

import javax.xml.soap.Text;

public class GameScreen implements Screen {
    Engine engine = new Engine();

    public GameScreen() {
        Gdx.gl.glClearColor(0.5f,0.5f,0.5f,1f);

        Texture[] textures = new Texture[3];
        for(int i = 0; i < textures.length; i++) {
            textures[i] = new Texture("images/"+Gdx.files.internal((i+1)+".png"));
        }

        TextureRegion arrow = new TextureRegion(new Texture(Gdx.files.internal("images/arrow.png")));//textures[0]);//
        Entity[] entities = new Entity[4];
        Transform[] transforms = new Transform[4];
        for(int i = 0; i < entities.length; i++) {
            entities[i] = new Entity(arrow);
            entities[i].name = Integer.toString(i);
            float u,v,u2,v2;
            switch (i) {
                case 0: u = 0; v = 0; u2 = 0.5f; v2 = 0.5f; break;
                case 1: u = 0.5f; v = 0; u2 = 1f; v2 = 0.5f; break;
                case 2: u = 0.5f; v = 0.5f; u2 = 1f; v2 = 1f; break;
                case 3: u = 0; v = 0.5f; u2 = 0.5f; v2 = 1f; break;
                default: u = 0; v = 0; u2 = 0; v2 = 0;
            }
            entities[i].renderer.setUV(u,v,u2,v2);
            transforms[i] = new Transform(new Transform().setPosition(128,128));
            transforms[i].parentId = i-1;
            entities[i].transformId = i;
        }

        GameObject g = new GameObject(entities,transforms,new Script[0], arrow.getTexture(),null,5);
        g.renderer.setLinearFilter();
        g.transform.setPosition(100, 100);
        g.transform.motion.va = 1;
        g.transform.motion.a_velocity = 40;
        g.createBody(0.85f);
        engine.setReplicator(g, "Name");

        engine.setPhysicsDebugDrawing(true);
        engine.initialize();

        GameObject gg = engine.createGameObject("Name");

        gg.getTransform("3").motion.va = 1;
        gg.getTransform("3").motion.a_velocity = 40;

        GameObject gameObject = engine.createGameObject("Name");
        gameObject.transform.setPosition(200,200);
        gameObject.transform.motion.xy_velocity = 20;
        gameObject.transform.motion.moveTo(600,600);

        gameObject.body.addContactListener(new ContactListener() {
            @Override
            public void beginContact(GameObject gameObject) {
                System.out.println("begin!");
            }

            @Override
            public void endContact(GameObject gameObject) {
                System.out.println("end!");
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        engine.update(delta);
    }

    @Override
    public void resize(int width, int height) {
        engine.resize(width, height);
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
