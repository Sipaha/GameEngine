package ru.sipaha.engine.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import ru.sipaha.engine.core.GameObject;
import ru.sipaha.engine.desktop.propertieseditor.GameProperties;
import ru.sipaha.engine.utils.Array;
import ru.sipaha.engine.utils.signals.Listener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Created on 09.11.2014.
 */

public class GameEditor extends JFrame {

    public GameEditor() {
        final EditorApplication application = new EditorApplication();

        setSize(1500, 900);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        JMenuBar menu = new JMenuBar();
        menu.add(new JMenu("File"));
        menu.add(new JMenu("Settings"));
        getContentPane().add(menu, BorderLayout.NORTH);

        Container contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        getContentPane().add(contentPanel);

        final GameProperties table = new GameProperties();
        table.setResizable(false,true);
        contentPanel.add(table, BorderLayout.WEST);

        JPanel topPanel = new JPanel();
        topPanel.setPreferredSize(new Dimension(0, 27));
        contentPanel.add(topPanel, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setPreferredSize(new Dimension(0,20));
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        String[] data = {"one", "two", "three", "four"};
        JList<String> sceneObjectsList = new JList<>(data);

        JList<String> layersList = new JList<>(data);

        JSplitPane rightSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, sceneObjectsList, layersList);
        rightSplitPane.setDividerSize(4);
        rightSplitPane.setPreferredSize(new Dimension(300, 0));
        contentPanel.add(rightSplitPane, BorderLayout.EAST);

        final JToggleButton btn = new JToggleButton("Run");
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(0,0,0,0));
        btn.setPreferredSize(new Dimension(60, 19));
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                application.setTestRunning(btn.isSelected());
                btn.setText(btn.isSelected() ? "Stop" : "Run");
            }
        });
        topPanel.add(btn);

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

        final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.vSyncEnabled = false;

        final LwjglApplication app = new LwjglApplication(application, config);
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

        app.postRunnable(new Runnable() {
            @Override
            public void run() {
                application.getController().onSelect.add(new Listener<Array<GameObject>>() {
                    @Override
                    public void receive(Array<GameObject> selected) {
                        table.set(selected);
                    }
                });
                application.getController().onChange.add(new Listener<Array<GameObject>>() {
                    @Override
                    public void receive(Array<GameObject> object) {
                        table.repaint();
                    }
                });
            }
        });
    }
}
