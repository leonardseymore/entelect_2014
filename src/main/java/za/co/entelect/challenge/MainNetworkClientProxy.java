package za.co.entelect.challenge;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;

public class MainNetworkClientProxy {

    private static final String OUTPUT_FILE_NAME = "game.state";

    public static void main(String[] args) throws IOException {
        String maze = readMaze(args[0]);
        String host = args[1];
        int port = Integer.parseInt(args[2]);
        String result = callServer(maze, host, port);
        writeMaze(result, OUTPUT_FILE_NAME);
    }

    public static String callServer(String maze, String host, int port) throws IOException {
        SocketChannel socket = SocketChannel.open();
        socket.configureBlocking(false);
        socket.connect(new InetSocketAddress(host, port));

        Selector selector = Selector.open();
        socket.register(selector, SelectionKey.OP_CONNECT);

        ByteBuffer mazeBuffer = ByteBuffer.wrap(maze.getBytes());

        while (selector.select() > 0) {
            Set keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();

            while (it.hasNext()) {
                SelectionKey key = it.next();
                SocketChannel channel = (SocketChannel) key.channel();
                it.remove();

                switch (key.readyOps()) {
                    case SelectionKey.OP_CONNECT:
                        if (channel.isConnectionPending()) {
                            channel.finishConnect();
                            System.out.println("Connection was pending but now is finished connecting.");
                        }
                        socket.register(selector, SelectionKey.OP_WRITE);
                        System.out.println("CONNECTED TO SERVER");
                        break;
                    case SelectionKey.OP_READ:
                        ByteBuffer responseBuffer = ByteBuffer.allocate(1024);

                        StringBuilder buffer = new StringBuilder();
                        int bytesRead = socket.read(responseBuffer);
                        while (bytesRead != 0) {
                            responseBuffer.flip();
                            String line = new String(responseBuffer.array(), responseBuffer.position(), responseBuffer.remaining());
                            buffer.append(line);
                            responseBuffer.clear();
                            bytesRead = socket.read(responseBuffer);
                        }
                        System.out.println("READ:\n" + buffer.toString());
                        return buffer.toString();
                    case SelectionKey.OP_WRITE:
                        while (mazeBuffer.hasRemaining()) {
                            channel.write(mazeBuffer);
                        }
                        System.out.println("WRITE");
                        socket.register(selector, SelectionKey.OP_READ);
                        break;
                    default:
                        //System.err.println("Unhandled " + key.readyOps());
                        break;
                }
            }
        }

        return null;
    }

    private static String readMaze(String filePath) throws FileNotFoundException {
        StringBuilder buffer = new StringBuilder();
        Scanner reader = new Scanner(new File(filePath));
        while (reader.hasNext()) {
            String row = reader.nextLine();
            buffer.append(row + "\n");
        }
        return buffer.toString();
    }

    private static void writeMaze(String result, String filePath) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(filePath);
        writer.print(result);
        writer.close();
    }


}
