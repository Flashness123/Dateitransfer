package beleg;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Channel {
  private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
  private static double lossRate = 0.0;
  private static int averageDelay = 0; // milliseconds end-to-end
  private static Random random;
  private final DatagramSocket socket;
  BlockingQueue<DatagramPacket> queue = new ArrayBlockingQueue<DatagramPacket>(256);
  ChannelDelay ch = new ChannelDelay();

  Channel(DatagramSocket socket, double loss, int delay) {
    this.socket = socket;
    setChannelSimulator(loss, delay);
    logger.log(Level.INFO, "beleg.Channel: loss: " + loss + " delay: " + delay + " ms");
    ch.start();
  }

  public static void setChannelSimulator(double loss, int delay) {
    lossRate = loss;
    averageDelay = delay;
    // Create random number generator for use in simulating packet loss and network delay.
    random = new Random();
    long seed = 1;
    random.setSeed(seed);
  }

  private static boolean simulateLoss() {
    boolean error = (random.nextDouble() < lossRate);
    if (error) {
      logger.log(Level.CONFIG, "*** simulate packet lost ***");
    }
    return !error;
  }

  public void sendPacket(DatagramPacket packet) {
    if (simulateLoss()) {
      queue.add(packet);
    }
  }

  public void receivePacket(DatagramPacket dataPacket) throws IOException {
    do {
      socket.receive(dataPacket);
    } while (!simulateLoss());  // no delay on receive path
  }

  class ChannelDelay extends Thread {
    DatagramPacket packet;

    public void run() {
      do {
        try {
          packet = queue.take(); // blocks
          Thread.sleep((int) (random.nextDouble() * 1.0 * averageDelay + 0.5 * averageDelay));
          socket.send(packet);
          // send all beleg.beleg.packets in queue at the same time
          while (!queue.isEmpty()) {
            packet = queue.take();
            socket.send(packet);
          }
        } catch (InterruptedException | IOException e) {
          e.printStackTrace();
        }
      } while (true);
    }
  }

}