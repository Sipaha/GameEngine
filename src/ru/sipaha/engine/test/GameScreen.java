package ru.sipaha.engine.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.gameobjectdata.MeshRenderer;
import ru.sipaha.engine.gameobjectdata.Motion;
import ru.sipaha.engine.gameobjectdata.Transform;
import ru.sipaha.engine.graphics.SceneRenderer;
import ru.sipaha.engine.graphics.batches.Batch;
import ru.sipaha.engine.graphics.batches.BatchArray;
import ru.sipaha.engine.graphics.batches.GOBatch;

public class GameScreen implements Screen {
    SceneRenderer renderer = new SceneRenderer();

    int t = 0, l = 0;
    Texture m;
    GameObject shell;
    Array<GameObject> shells = new Array<>();

    public GameScreen() {
        Gdx.gl.glClearColor(0.5f,0.5f,0.5f,1f);

        Texture[] textures = new Texture[3];
        for(int i = 0; i < textures.length; i++) {
            textures[i] = new Texture("images/"+Gdx.files.internal((i+1)+".png"));
        }

        GameObject[] gameObjects = new GameObject[10];
        for(int i = 0; i < gameObjects.length; i++) {
            gameObjects[i] = createMorda(new TextureRegion(textures[(int)Math.round(Math.random()*2)]),(int)Math.round(Math.random()*5));
            renderer.prepareBatchForGameObject(gameObjects[i]);
        }

        renderer.rebuildBatchesArrays();

        for(int i = 0; i < 30; i++) {
            renderer.addGameObject(gameObjects[(int)(Math.random()*9)].copy().updateData(0.2f));
        }
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
