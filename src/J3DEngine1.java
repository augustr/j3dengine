import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import gfx.*;
import gfx.opengl.*;

public class J3DEngine1 extends JFrame implements WindowListener
{
    protected AbstractRenderer renderer = null;

    public J3DEngine1()
    {
        super("Graphics Engine");

        Container c = this.getContentPane();
        c.setLayout(new BorderLayout());
        c.add(makeRenderPanel(640, 480), BorderLayout.CENTER);

        this.addWindowListener(this);
        this.pack();
        this.setVisible(true);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });
    }

    private JPanel makeRenderPanel(int width, int height)
    {
        JPanel renderPane = new JPanel();
        renderPane.setLayout(new BorderLayout());
        renderPane.setOpaque(false);
        renderPane.setPreferredSize(new Dimension(width, height));

        renderer = this.makeCanvas(width, height);
        //renderPane.add(renderer, BorderLayout.CENTER);

        renderPane.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                Dimension d = evt.getComponent().getSize();
                //renderer.reshape(0,0,d.width, d.height);
            }
        });

        return renderPane;
    }

    private AbstractRenderer makeCanvas(int width, int height)
    {
        return new OpenGLRenderer(width, height);
    }

    public void exit()
    {
        this.renderer.close();
        System.exit(0);
    }

    public void run()
    {
        // Setup
        AbstractSprite sprite = this.renderer.createSprite("filename", 640, 480);
        float rotation = 0.0f;

        // Main loop
        while(true) {
            rotation += Math.PI/180;
            if (rotation > 2*Math.PI) { rotation = 0.0f; }

            this.renderer.beginRender();
            sprite.render(0.0f, 0.0f,1.0f,rotation,1.0f,1.0f);
            this.renderer.endRender();
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        J3DEngine1 engine = new J3DEngine1();
        engine.run();
    }

    public void windowActivated(WindowEvent e) { }
    public void windowDeactivated(WindowEvent e) { }
    public void windowDeiconified(WindowEvent e) { }
    public void windowIconified(WindowEvent e) { }
    public void windowClosing(WindowEvent e) { }
    public void windowClosed(WindowEvent e) { }
    public void windowOpened(WindowEvent e) { }
}