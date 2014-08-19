package ru.sipaha.engine.graphics.batches;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.graphics.RenderUnit;

public class GOBatch extends Batch {

    private Array<GameObject> gameObjects = new Array<>(false, 32, GameObject.class);;

    public GOBatch(ShaderProgram shader, Texture texture, int z_order) {
        super(shader, texture, z_order);
    }

    public GOBatch(RenderUnit batch) {
        super(batch);
    }

    @Override
    protected int prepareVertices(float[] vertices, int verticesCount) {
        GameObject[] g = gameObjects.items;
        for(int i = 0, s = gameObjects.size; i < s; i++) {
            System.arraycopy(g[i].renderer.data, 0, vertices, verticesCount, SPRITE_SIZE);
            verticesCount += SPRITE_SIZE;
        }
        return verticesCount;
    }

    public void addGameObject(GameObject go) {
        gameObjects.add(go);
    }

    public void removeGameObject(GameObject go) {
        gameObjects.removeValue(go, true);
    }
}
