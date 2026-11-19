package pfc.gui.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import pfc.language.Language;
import pfc.video.VideoSession;

/**
 * GUI classes for the Player.
 */
public abstract class PlayerWindow extends JFrame {

    static final long serialVersionUID = -1323515012494644868L;

    private JLabel videoLabel = null;
    private PlayerPanel playerPanel = null;
    private Language lang = null;
    private GridBagLayout gridbag;
    private GridBagConstraints constraints;
    private VideoSession videoSession;
    private final Object frameRenderLock = new Object();
    private BufferedImage pendingFrame;
    private boolean renderScheduled;
    private int renderedFrameWidth = -1;
    private int renderedFrameHeight = -1;

    public PlayerWindow(Language lang) {
        this.lang = lang;

        gridbag = new GridBagLayout();
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(2, 2, 2, 2);
        this.setLayout(gridbag);
        this.setResizable(false);

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((d.getWidth() - this.getWidth()) / 2);
        int y = (int) ((d.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);
    }

    public void setVideoSession(VideoSession videoSession) {
        this.videoSession = videoSession;
    }

    public void initialize() {
        if (playerPanel != null) {
            return;
        }

        MainWindow.setRunningPlayer(true);
        videoLabel = new JLabel();
        playerPanel = new PlayerPanel(videoLabel);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        gridbag.setConstraints(playerPanel, constraints);
        playerPanel.setBorder(BorderFactory.createTitledBorder(this.lang.OPTIONS_PLAYER_PANEL_TITLE));
        this.add(playerPanel);
        initializeComponents();
        this.pack();
    }

    public abstract void initializeComponents();

    public void updateFrame(final BufferedImage image) {
        if (image == null) {
            return;
        }

        boolean scheduleRender = false;
        synchronized (frameRenderLock) {
            pendingFrame = image;
            if (!renderScheduled) {
                renderScheduled = true;
                scheduleRender = true;
            }
        }

        if (scheduleRender) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    renderLatestFrame();
                }
            });
        }
    }

    private void renderLatestFrame() {
        BufferedImage imageToRender;
        synchronized (frameRenderLock) {
            imageToRender = pendingFrame;
            pendingFrame = null;
        }

        if (imageToRender != null && videoLabel != null) {
            videoLabel.setIcon(new ImageIcon(imageToRender));

            boolean sizeChanged = imageToRender.getWidth() != renderedFrameWidth
                    || imageToRender.getHeight() != renderedFrameHeight;
            if (sizeChanged) {
                renderedFrameWidth = imageToRender.getWidth();
                renderedFrameHeight = imageToRender.getHeight();
                playerPanel.revalidate();
                pack();
            }
            else {
                videoLabel.repaint();
                playerPanel.repaint();
            }
        }

        boolean reschedule = false;
        synchronized (frameRenderLock) {
            if (pendingFrame != null) {
                reschedule = true;
            }
            else {
                renderScheduled = false;
            }
        }

        if (reschedule) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    renderLatestFrame();
                }
            });
        }
    }

    public void closeRTP() {
        if (videoSession != null) {
            videoSession.close();
            videoSession = null;
        }

        if (playerPanel != null) {
            this.remove(playerPanel);
            playerPanel.removeAll();
            playerPanel = null;
            videoLabel = null;
        }

        synchronized (frameRenderLock) {
            pendingFrame = null;
            renderScheduled = false;
            renderedFrameWidth = -1;
            renderedFrameHeight = -1;
        }

        setVisible(false);
        dispose();
        MainWindow.setRunningPlayer(false);
    }

    public void addNotify() {
        super.addNotify();
        this.pack();
    }

    class PlayerPanel extends JPanel {

        private static final long serialVersionUID = -1564372944630348513L;

        JLabel imageLabel;

        public PlayerPanel(JLabel imageLabel) {
            this.imageLabel = imageLabel;
            setLayout(new BorderLayout());
            add("Center", imageLabel);
        }

        public Dimension getPreferredSize() {
            if (imageLabel != null && imageLabel.getIcon() != null) {
                return new Dimension(imageLabel.getIcon().getIconWidth(), imageLabel.getIcon().getIconHeight());
            }
            return new Dimension(320, 240);
        }
    }
}
