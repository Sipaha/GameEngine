package ru.sipaha.engine.graphics.batches;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.gameobjectdata.MeshRenderer;
import ru.sipaha.engine.graphics.RenderUnit;
import ru.sipaha.engine.utils.GameObjectsArray;

public class GOBatch extends Batch {

    private GameObjectsArray gameObjects = new GameObjectsArray(false, 32);

    public GOBatch(ShaderProgram shader, Texture texture, int z_order) {
        super(shader, texture, z_order);
    }

    public GOBatch(RenderUnit batch) {
        super(batch);
    }

    @Override
    protected int prepareVertices(float[] vertices, int verticesCount) {
        for(GameObject g : gameObjects) {
            if(g.renderer.visible) {
                System.arraycopy(g.renderer.data, 0, vertices, verticesCount, SPRITE_SIZE);
                verticesCount += SPRITE_SIZE;
            }
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
