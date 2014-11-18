package ru.sipaha.engine.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.desktop.properties.GameProperties;
import ru.sipaha.engine.desktop.properties.Property;
import ru.sipaha.engine.graphics.Camera;
import ru.sipaha.engine.test.GameScreen;
import ru.sipaha.engine.test.TestGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created on 09.11.2014.
 */

public class GameEditor extends JFrame {

    public GameEditor() {
        final TestGame game = new TestGame();

        setSize(1280, 720);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        final Container contentPanel = getContentPane();
        contentPanel.setLayout(new BorderLayout());
        final GameProperties table = new GameProperties();
        table.setResizable(false,true);
        contentPanel.add(table, BorderLayout.WEST);
        Canvas canvas = new Canvas();
        contentPanel.add(canvas);
        setVisible(true);

        LwjglNativesLoader.load();
        try {
            Display.setParent(canvas);
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(0);
        }

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.vSyncEnabled = false;

        final LwjglApplication app = new LwjglApplication(game, config);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                app.stop();
                app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        Display.destroy();
                        System.exit(0);
                    }
                });
            }
        });
        final JFrame currFrame = this;
        app.postRunnable(new Runnable() {
            @Override
            public void run() {
                Engine engine = ((GameScreen)game.getScreen()).engine;
                table.setEngine(engine);
                Camera gameCamera = engine.renderer.getRenderLayer().camera;
                EditorRenderLayer layer = new EditorRenderLayer(gameCamera);
                engine.input.addProcessor(new EditorController(layer, gameCamera,
                                                        engine.tagManager.getUnitsWithTag("Editable")));
                engine.renderer.addRenderLayer(layer);
                currFrame.repaint();
            }
        });


    }

    private Property createColorProperty() {
        return null;//new Property("FloatValue", new Values.Float(new Values.Bool(),4523.34f));
    }
}
