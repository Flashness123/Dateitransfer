package beleg;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.net.InetAddress.getByName;

/**
 * @author Jörg Vogt
 */

public class Socket {
  private static final int MTU_max = 1480; // for receiver
  private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  private static boolean isServer;
  private final DatagramSocket socket;
  BlockingQueue<DatagramPacket> queue = new ArrayBlockingQueue<DatagramPacket>(256);
  // http://tutorials.jenkov.com/java-util-concurrent/blockingqueue.html
  beleg.Channel channel;
  private InetAddress peerAddress;
  private int port;

  /**
   * Client usage
   *
   * @param host Host to connect
   * @param port Port to connect
   * @throws SocketException      in case of problems
   * @throws UnknownHostException as the exception says
   */
  public Socket(String host, int port) throws SocketException, UnknownHostException {
    isServer = false;
    this.port = port;
    peerAddress = getByName(host);
    socket = new DatagramSocket();
    channel = new beleg.Channel(socket, 0, 0);
    // Linux default Receive buffer: 208 kB -> about 150 beleg.beleg.packets
    // sysctl -a | grep udp
    // sysctl -w net.core.rmem_max=8388608
    logger.log(Level.FINER, "C: Rcv Buffer Size: " + socket.getReceiveBufferSize());
  }

  /**
   * Server usage
   *
   * @param port  Port for the connection from clients
   * @param loss  simulate network loss (0...1)
   * @param delay simulate network delay (average RTT in ms)
   * @throws SocketException in case of socket problems
   */
  public Socket(int port, double loss, int delay) throws SocketException {
    isServer = true;
    socket = new DatagramSocket(port);
    logger.log(Level.FINER, "Rcv Buffer Size: " + socket.getReceiveBufferSize());
    channel = new beleg.Channel(socket, loss, delay);
  }

  /**
   * Client/server: sends a packet to fixed destination
   *
   * @param sendData DataPacket to send
   */
  public void sendPacket(byte[] sendData) {
    DatagramPacket packet = new DatagramPacket(sendData, sendData.length, peerAddress, port);
    channel.sendPacket(packet);  // socket.send()
  }

  /**
   * Client: get a packet from Server
   * Server: get a packet from Client and saves address
   *
   * @return Data
   * @throws TimeoutException socket timeout
   */
  public DatagramPacket receivePacket() throws TimeoutException {
    DatagramPacket dataPacket = new DatagramPacket(new byte[MTU_max], MTU_max);
    try {
      channel.receivePacket(dataPacket);  // socket
    } catch (SocketTimeoutException e) {
      throw new TimeoutException();
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (isServer) {
      peerAddress = dataPacket.getAddress();
      port = dataPacket.getPort();
    }
    return dataPacket;
  }

  /**
   * Sets the timeout for receiving a packet
   *
   * @param timeout the value to set for the receiving socket
   */
  public void setTimeout(int timeout) {
    try {
      socket.setSoTimeout(timeout);
    } catch (SocketException e) {
      e.printStackTrace();
    }
  }
}