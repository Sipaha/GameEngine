package ru.sipaha.engine.graphics.batches;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import ru.sipaha.engine.core.GameObject;

public class GOBatch extends Batch {

    public static final int GO_SIZE = 20;
    public final int size;

    private Array<GameObject> gameObjects;

    public GOBatch(int size, ShaderProgram shader, Texture texture, int z_order) {
        super(size * 4, size * 6, shader, texture, z_order);
        if (size > 5460) throw new IllegalArgumentException("Can't have more than 5460 sprites per batch: " + size);
        this.size = size;
        gameObjects = new Array<>(false, 32, GameObject.class);
    }

    @Override
    protected void prepareIndices(Mesh mesh) {
        int len = size * 6;
        short[] indices = new short[len];
        short j = 0;
        for (int i = 0; i < len; i += 6, j += 4) {
            indices[i] = j;
            indices[i + 1] = (short)(j + 1);
            indices[i + 2] = (short)(j + 2);
            indices[i + 3] = (short)(j + 2);
            indices[i + 4] = (short)(j + 3);
            indices[i + 5] = j;
        }
        mesh.setIndices(indices);
    }

    @Override
    protected void prepareVertices() {
        GameObject[] g = gameObjects.items;
        for(int i = 0, s = gameObjects.size; i < s; i++) {
            System.arraycopy(g[i].renderer.data, 0, vertices, verticesCount, GO_SIZE);
            verticesCount += GO_SIZE;
        }
    }

    public void addGameObject(GameObject go) {
        gameObjects.add(go);
    }

    public void addGameObjects(GOBatch b) {
        gameObjects.addAll(b.gameObjects);
    }

    public void removeGameObject(GameObject go) {
        gameObjects.removeValue(go, true);
    }

    public boolean isFull() {
        return size == gameObjects.size;
    }
}
