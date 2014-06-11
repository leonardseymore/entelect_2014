package za.co.entelect.challenge.agents;

import za.co.entelect.challenge.NoMoveFoundException;
import za.co.entelect.challenge.Util;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;
import za.co.entelect.challenge.swing.Draw;
import za.co.entelect.challenge.swing.Keyboard;

import java.awt.*;
import java.awt.event.KeyEvent;

public class KeyboardAgent extends PacmanAgent {

    protected XY getMove(GameState gameState, XY pos) throws NoMoveFoundException {
        Keyboard keyboard = Keyboard.getInstance();

        XY moveTo = new XY(pos.x, pos.y);
        if (keyboard.keyDown(KeyEvent.VK_UP)) {
            moveTo.y--;
        }

        if (keyboard.keyDown(KeyEvent.VK_RIGHT)) {
            moveTo.x++;
        }

        if (keyboard.keyDown(KeyEvent.VK_DOWN)) {
            moveTo.y++;
        }

        if (keyboard.keyDown(KeyEvent.VK_LEFT)) {
            moveTo.x--;
        }

        if (Util.canMoveTo(gameState, moveTo, pos)) {
            return moveTo;
        }
        return pos;
    }

    @Override
    public void drawDebugInfo(Graphics2D g, GameState gameState, XY pos) {
        g.setColor(Color.gray);
        g.setStroke(new BasicStroke(3));
        Draw.drawCross(g, pos.x, pos.y);
        g.setStroke(new BasicStroke(1));
    }
}
