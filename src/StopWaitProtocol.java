

import Packets.AcknowledgePacket;


import java.net.DatagramPacket;
import java.util.Arrays;

public class StopWaitProtocol implements ARQ {
    private int retransmissions = 0;
    private int retries = 0;
    private final Socket socket;
    private int mtu = 1480;

    public StopWaitProtocol(Socket socket) {
        this.socket = socket;
    }

    @Override
    public boolean data_req(byte[] hlData, int hlSize, boolean lastTransmission) {
        byte[] sessionAndPacketNumber = Arrays.copyOfRange(hlData, 0, 3);
        int packetRetries = 0;
        boolean packetSentSuccessfully = false;
        do {
            try {
                socket.sendPacket(hlData);
                DatagramPacket ackPacket = socket.receivePacket();
                byte[] ackPacketData = ackPacket.getData();
                if (!Arrays.equals(sessionAndPacketNumber, Arrays.copyOfRange(ackPacketData, 0, 3))) {
                    return false;
                }
                packetSentSuccessfully = true;
            } catch (TimeoutException e) {
                packetRetries++;
            }
        } while (!packetSentSuccessfully && packetRetries < 10);
        retries += packetRetries;

        return packetSentSuccessfully;
    }

    @Override
    public byte[] data_ind_req(int remaining) throws TimeoutException {
        DatagramPacket datagramPacket = socket.receivePacket();
        byte[] data = datagramPacket.getData();
        AcknowledgePacket acknowledgePacket = new AcknowledgePacket(Arrays.copyOfRange(data, 0, 2), data[2], (byte) 1);
        socket.sendPacket(acknowledgePacket.toBytes());
        return Arrays.copyOfRange(datagramPacket.getData(), 3, 3+Math.min(mtu, remaining) );
    }

    @Override
    public int getBackData() {
        return 0;
    }

    @Override
    public void closeConnection() {
    }

    @Override
    public int getRetransmissionCounter() {
        return retransmissions;
    }

    @Override
    public int getRetryCounterStat() {
        return retries;
    }

    @Override
    public int getMTU() {
        return mtu;
    }

    @Override
    public void setMTU(int mtu) {
        this.mtu = mtu;
    }
}
