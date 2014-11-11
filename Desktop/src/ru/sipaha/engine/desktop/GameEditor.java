package ru.sipaha.engine.desktop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import ru.sipaha.engine.core.Engine;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.core.Values;
import ru.sipaha.engine.desktop.properties.GameProperties;
import ru.sipaha.engine.desktop.properties.PropertiesTable;
import ru.sipaha.engine.desktop.properties.PropertiesTableModel;
import ru.sipaha.engine.desktop.properties.Property;
import ru.sipaha.engine.desktop.properties.editors.BlendingEditor;
import ru.sipaha.engine.desktop.properties.editors.ColorEditor;
import ru.sipaha.engine.desktop.properties.renderers.ColorRenderer;
import ru.sipaha.engine.desktop.properties.editors.FloatValueEditor;
import ru.sipaha.engine.desktop.properties.renderers.SectionRenderer;
import ru.sipaha.engine.desktop.properties.sections.RenderUnitSection;
import ru.sipaha.engine.desktop.properties.sections.TransformSection;
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

    Iterable<GameObject> editableObjects;

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
                editableObjects = (Iterable)engine.tagManager.getUnitsWithTag("Editable");
                for(GameObject g : editableObjects) table.set(g);
                currFrame.repaint();
            }
        });


    }

    private Property createColorProperty() {
        return null;//new Property("FloatValue", new Values.Float(new Values.Bool(),4523.34f));
    }
}
