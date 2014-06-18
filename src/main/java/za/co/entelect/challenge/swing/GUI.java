package za.co.entelect.challenge.swing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.Constants;
import za.co.entelect.challenge.NoMoveFoundException;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.ai.filters.PillCluster;
import za.co.entelect.challenge.domain.Game;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.Set;

public class GUI extends JFrame implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(GUI.class);

    public static final int DEFAULT_WIDTH = Constants.ZOOM_LEVEL * Constants.WIDTH;
    public static final int DEFAULT_HEIGHT = Constants.ZOOM_LEVEL * Constants.HEIGHT;

    private Game game;
    private boolean printHelp;
    private boolean fullScreen;

    private Keyboard keyboard;
    private Mouse mouse;
    private boolean paused = true;
    private boolean showNames = true;
    private boolean drawDebugInfo = false;

    private Font arial = new Font("Arial", Font.BOLD, 10);
    private Font bigArial = new Font("Courier New", Font.BOLD, 16);

    private int phase = 0;
    private float pulse = 0;
    private int pulseSign = 1;
    private int pulseInterval = 100;
    private long lastPulseMove = System.currentTimeMillis();

    private int frameSleep = 100;
    private int frameSleepMultiplier = 1;
    private long lastUpdate = System.currentTimeMillis();

    private boolean renderInfluenceMap = false;

    private float scaleX = 1f;
    private float scaleY = 1f;

    private static GraphicsDevice device;

    private InfluenceMapRenderer influenceMapRenderer = new InfluenceMapRenderer();

    public GUI(Game game) {
        this.game = game;
        fullScreen = false;
        device = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0];
        restart();

        setIgnoreRepaint(true);
        setTitle(Constants.APP_TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        pack();

        keyboard = Keyboard.getInstance();
        addKeyListener(keyboard);

        Mouse.init(this, 1);
        mouse = Mouse.getInstance();
        addMouseListener(mouse);
        addMouseMotionListener(mouse);

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int width = getWidth();
                int height = getHeight();
                Insets insets = getInsets();
                if (!fullScreen) {
                    width -= insets.left;
                    width -= insets.right;
                    height -= insets.top;
                    height -= insets.bottom;
                }
                scaleX = width / (float) DEFAULT_WIDTH;
                scaleY = height / (float) DEFAULT_HEIGHT;
            }
        });
    }

    private void restart() {
        logger.debug("Restart");
        game.restart();
    }

    public Game getGame() {
        return game;
    }

    public void run() {
        createBufferStrategy(2);

        BufferStrategy buffer = getBufferStrategy();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();

        Graphics graphics = null;
        Graphics2D g = null;

        while (true) {
            if (System.currentTimeMillis() > lastPulseMove + pulseInterval) {
                lastPulseMove = System.currentTimeMillis();
                pulse += 0.1f * pulseSign;

                if (pulse > 1 || pulse < 0) {
                    phase++;
                    if (phase > Integer.MAX_VALUE) {
                        phase = 0;
                    }
                    pulseSign *= -1;
                    pulse += 0.1f * pulseSign;
                }
            }

            try {
                keyboard.poll();
                mouse.poll();

                if (keyboard.keyDownOnce(KeyEvent.VK_B)) {
                    if (game.canPopGameState()) {
                        game.popGameState();
                    }
                }
                if (keyboard.keyDownOnce(KeyEvent.VK_A)) {
                    game.getCurrentPlayerAgent().reset();
                    game.swapPlayerAgents();
                    game.getCurrentPlayerAgent().reset();
                }
                if (keyboard.keyDownOnce(KeyEvent.VK_E)) {
                    game.getCurrentPlayerAgent().reset();
                }
                if (keyboard.keyDownOnce(KeyEvent.VK_W)) {
                    showNames = !showNames;
                }
                if (keyboard.keyDownOnce(KeyEvent.VK_D)) {
                    Util.writeTmpMaze(game.getGameState());
                }
                if (keyboard.keyDownOnce(KeyEvent.VK_X)) {
                    drawDebugInfo = !drawDebugInfo;
                }
                if (keyboard.keyDownOnce(KeyEvent.VK_R)) {
                    restart();
                }
                if (keyboard.keyDownOnce(KeyEvent.VK_H)) {
                    printHelp = !printHelp;
                }
                if (keyboard.keyDownOnce(KeyEvent.VK_I)) {
                    renderInfluenceMap = !renderInfluenceMap;
                }
                if (keyboard.keyDownOnce(KeyEvent.VK_P)) {
                    paused = !paused;
                    if (paused) {
                        setTitle(Constants.APP_TITLE + " - PAUSED");
                    } else {
                        setTitle(Constants.APP_TITLE);
                    }
                }
                if (keyboard.keyDownOnce(KeyEvent.VK_F)) {
                    frameSleepMultiplier = Math.max(frameSleepMultiplier - 1, 0);
                }
                if (keyboard.keyDownOnce(KeyEvent.VK_S)) {
                    frameSleepMultiplier++;
                }
                if (keyboard.keyDownOnce(KeyEvent.VK_N)) {
                    if (System.currentTimeMillis() - lastUpdate > frameSleep * frameSleepMultiplier) {
                        game.update();
                        lastUpdate = System.currentTimeMillis();
                    }
                }
                if (keyboard.keyDownOnce(KeyEvent.VK_ESCAPE)) {
                    fullScreen = !fullScreen;
                    if (fullScreen) {
                        device.setFullScreenWindow(this);
                    } else {
                        device.setFullScreenWindow(null);
                    }
                }
                if (!paused) {
                    if (System.currentTimeMillis() - lastUpdate > frameSleep * frameSleepMultiplier) {
                        game.update();
                        lastUpdate = System.currentTimeMillis();
                    }
                }

                Insets insets = getInsets();
                BufferedImage bi = gc.createCompatibleImage(getWidth(), getHeight());
                g = bi.createGraphics();
                g.setFont(arial);

                AffineTransform t = g.getTransform();
                if (!fullScreen) {
                    g.translate(insets.left, insets.top);
                }
                g.scale(scaleX, scaleY);

                g.setColor(Constants.COLOR_SWING_BLANK);
                g.fillRect(0, 0, getWidth() - insets.right - insets.left, getHeight());

                GameState gameState = game.getGameState();
                renderMaze(g, gameState);
                if (gameState.isGameOver()) {
                    g.setFont(bigArial);
                    g.setColor(new Color(120, 174, 255, 100));
                    g.fillRect(0, 0, getWidth(), getHeight());
                    int x = 10;
                    int y = 10;
                    g.setColor(Color.getHSBColor(.6f, 0f, Math.min(1f, 0.7f + Math.abs(pulse))));
                    g.drawString("Game Over", x, y += 15);
                    g.drawString(Util.getWinnerDescription(gameState) + " " + game.getWinnerAgent(), x, y += 15);
                    g.drawString("Scores: A(" + game.getPlayerAAgentClass() + ")=" + gameState.getPlayerAScore() + ", B(" + game.getPlayerBAgentClass() + ")=" + gameState.getPlayerBScore(), x, y += 15);
                }

                if (printHelp) {
                    g.setColor(new Color(123, 123, 123, 190));
                    g.fillRect(0, 0, getWidth(), 100);
                    g.setColor(Color.white);

                    int x = 10;
                    int y = 10;
                    g.drawString("p=pause, r=restart, d=dump, x=debug, i=influence, w=names", x, y += 12);
                    if (renderInfluenceMap) {
                        g.drawString("0=c,1=yi,2=oi,3=iy,4=io,5=t,6=v,7=yp,8=op", x, y += 12);
                        float yp = gameState.getInfluenceMap().getTotalYPotential();
                        float op = gameState.getInfluenceMap().getTotalOPotential();
                        g.drawString("Y pot.: " + yp + ", O pot.: " + op, x, y += 12);
                    }
                    g.drawString("Turn #" + gameState.getNumMoves() + ", #!" + gameState.getMovesNoPills() + ": " + (gameState.isGameOver() ? "GAME OVER" : gameState.getCurrentPlayer()), x, y += 12);

                    g.drawString("Zobrist hash: " + gameState.getHash(), x, y += 12);
                    g.drawString("Zobrist hash fp: " + Util.hashFirstPrincipal(gameState), x, y += 12);
                }

                graphics = buffer.getDrawGraphics();
                graphics.drawImage(bi, 0, 0, null);
                if (!buffer.contentsLost()) {
                    buffer.show();
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    logger.warn("Thread interrupted", ex);
                }
            } finally {
                if (graphics != null) {
                    graphics.dispose();
                }

                if (g != null) {
                    g.dispose();
                }
            }
        }
    }

    private void renderMaze(Graphics2D g, GameState gameState) {
        if (renderInfluenceMap) {
            influenceMapRenderer.render(g, gameState);
        }
        //pillClusterRenderer.render(g, gameState);
        Draw.drawMaze(g, gameState, pulse, phase);

        XY pos = gameState.getCurrentPosition();
        Color c = gameState.getCurrentPlayer() == Constants.PLAYER_A ? Constants.PLAYER_A_COLOR : Constants.PLAYER_B_COLOR;
        int strobe = Math.min((int) (pulse * 10), 5);
        c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255 / (strobe + 1));
        g.setColor(c);
        Draw.drawRect(g, pos.x, pos.y, -strobe);
        c = new Color(c.getRed(), c.getGreen(), c.getBlue(), 127 / (strobe + 1));
        g.setColor(c);
        Draw.drawRect(g, pos.x, pos.y, -strobe * 2);

        if (gameState.getNumMoves() == 0 || showNames) {
            XY a = gameState.getPlayerPos(Constants.PLAYER_A);
            g.setColor(Color.black);
            Draw.drawString(g, a.x, a.y, game.getPlayerAAgentClass(), 1, -1);
            g.setFont(arial);
            g.setColor(Constants.PLAYER_A_COLOR);
            Draw.drawString(g, a.x, a.y, game.getPlayerAAgentClass(), 0, -2);

            XY b = gameState.getPlayerPos(Constants.PLAYER_B);
            g.setColor(Color.black);
            Draw.drawString(g, b.x, b.y, game.getPlayerBAgentClass(), 1, -1);
            g.setFont(arial);
            g.setColor(Constants.PLAYER_B_COLOR);
            Draw.drawString(g, b.x, b.y, game.getPlayerBAgentClass(), 0, -2);
        }

        if (drawDebugInfo) {
            XY nextMove = null;
            if (!gameState.isGameOver()) {
                try {
                    nextMove = game.getCurrentPlayerAgent().peekNextMove(gameState, gameState.getCurrentPosition());
                } catch (NoMoveFoundException ex) {
                    logger.error("Problem peeking at next move", ex);
                    game.getGameState().setGameOver(true);
                }
            }

            game.getCurrentPlayerAgent().drawDebugInfo(g, gameState, gameState.getCurrentPosition());

            if (nextMove != null) {
                g.setColor(Color.red);
                Draw.drawCross(g, nextMove.x, nextMove.y);
            }
        }
    }
}
