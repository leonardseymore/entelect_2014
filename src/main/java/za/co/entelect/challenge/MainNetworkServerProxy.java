package za.co.entelect.challenge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.entelect.challenge.agents.Hayw1r3d;
import za.co.entelect.challenge.agents.PacmanAgent;
import za.co.entelect.challenge.domain.GameState;
import za.co.entelect.challenge.domain.XY;
import za.co.entelect.challenge.groovy.GameFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class MainNetworkServerProxy {

    private static final Logger logger = LoggerFactory.getLogger(MainNetworkServerProxy.class);

    private int port;
    private PacmanAgent agent;

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, IOException, NoMoveFoundException {
        int port = Integer.parseInt(args[0]);
        String botClass = args[1];
        PacmanAgent agent = (PacmanAgent) Class.forName(botClass).newInstance();
        MainNetworkServerProxy networkProxy = new MainNetworkServerProxy(port, agent);
        networkProxy.server();
    }

    public MainNetworkServerProxy(int port, PacmanAgent agent) {
        this.port = port;
        this.agent = agent;
    }

    public void server() throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Selector selector = Selector.open();

        ServerSocketChannel server1 = ServerSocketChannel.open();
        server1.configureBlocking(false);
        server1.socket().bind(new InetSocketAddress(port));
        server1.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SocketChannel client;
                SelectionKey key = iter.next();
                iter.remove();
                switch (key.readyOps()) {
                    case SelectionKey.OP_ACCEPT:
                        client = ((ServerSocketChannel) key.channel()).accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_READ);
                        break;
                    case SelectionKey.OP_READ:
                        client = (SocketChannel) key.channel();
                        buffer.clear();
                        if (client.read(buffer) != -1) {
                            buffer.flip();
                            String line = new String(buffer.array(), buffer.position(), buffer.remaining());
                            System.out.println(line);
                            if (line.startsWith("CLOSE")) {
                                client.close();
                            } else if (line.startsWith("QUIT")) {
                                for (SelectionKey k : selector.keys()) {
                                    k.cancel();
                                    k.channel().close();
                                }
                                selector.close();
                                return;
                            } else {
                                GameState gameState = GameFactory.fromString(line);

                                XY pos = gameState.getCurrentPosition();
                                XY move = null;
                                try {
                                    move = agent.pullNextMove(gameState, pos);
                                } catch (NoMoveFoundException ex) {
                                    client.write(ByteBuffer.wrap("No valid move found".getBytes()));
                                }
                                gameState.updateCell(pos, Constants.SPACE, false);
                                gameState.updateCell(move, Constants.PLAYER_A, false);

                                ByteBuffer out = ByteBuffer.wrap(GameFactory.gameStateToString(gameState).getBytes());
                                while (out.hasRemaining()) {
                                    client.write(out);
                                }
                            }
                        } else {
                            key.cancel();
                        }
                        break;
                    default:
                        System.err.println("Unhandled " + key.readyOps());
                        break;
                }
            }
        }
    }
}
