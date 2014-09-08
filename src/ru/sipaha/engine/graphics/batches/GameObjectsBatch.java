package ru.sipaha.engine.graphics.batches;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.gameobjectdata.GameObjectRenderer;
import ru.sipaha.engine.graphics.RenderUnit;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.GameObjectsArray;

public class GameObjectsBatch extends Batch {

    private Array<GameObjectRenderer> gameObjects = new Array<>(false, 32, GameObjectRenderer.class);

    public GameObjectsBatch(ShaderProgram shader, Texture texture, int z_order) {
        super(shader, texture, z_order);
    }

    public GameObjectsBatch(RenderUnit batch) {
        super(batch);
    }

    @Override
    protected int prepareVertices(float[] vertices, int verticesCount) {
        try {
            GameObjectRenderer[] objects = gameObjects.items;
            for(int i = 0, s = gameObjects.size; i < s; i++) {
                verticesCount = objects[i].render(vertices, verticesCount);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Gdx.app.error("Game Engine", "Vertices length is too low! Length = "+vertices.length);
        }
        return verticesCount;
    }

    public void addGameObjectRenderer(GameObjectRenderer go) {
        gameObjects.add(go);
    }

    public void removeGameObjectRenderer(GameObjectRenderer go) {
        gameObjects.removeValue(go, true);
    }
}
