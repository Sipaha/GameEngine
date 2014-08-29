package ru.sipaha.engine.graphics.batches;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.graphics.RenderUnit;
import ru.sipaha.engine.utils.GameObjectsArray;

public class GameObjectsBatch extends Batch {

    private GameObjectsArray gameObjects = new GameObjectsArray(false, 32);

    public GameObjectsBatch(ShaderProgram shader, Texture texture, int z_order) {
        super(shader, texture, z_order);
    }

    public GameObjectsBatch(RenderUnit batch) {
        super(batch);
    }

    @Override
    protected int prepareVertices(float[] vertices, int verticesCount) {
        for(GameObject g : gameObjects) verticesCount = g.render(vertices, verticesCount);
        return verticesCount;
    }

    public void addGameObject(GameObject go) {
        gameObjects.add(go);
    }

    public void removeGameObject(GameObject go) {
        gameObjects.removeValue(go, true);
    }
}
