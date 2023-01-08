



import Packets.AcknowledgePacket;
import Packets.DataPacket;
import Packets.StartPacket;


import java.io.*;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

public class FileTransfer implements FT {
    private final boolean isServer;
    private final Socket socket;
    private String host;
    private String dir;
    private String fileName;
    private ARQ arq;
    private byte[] sessionNumber = new byte[2];
    private byte packetNumber = 1;
    private long fileLength;

    /**
     * Constructor for client
     *
     * @param host     host address
     * @param socket   socket over which to provide the data
     * @param fileName name of the file to be sent
     * @param arq      beleg.arq protocol type
     */
    public FileTransfer(String host, Socket socket, String fileName, String arq) {
        this.isServer = false;
        this.host = host;
        this.socket = socket;
        this.fileName = fileName;

        if ("gbn".equals(arq)) {
            this.arq = new GoBackN(socket);//noch nicht implementioiert
        } else {
            this.arq = new StopWaitProtocol(socket);
        }

        this.arq.setMTU(1480);
    }

    /**
     * Constructor for server specifying directory to store the files sent in
     *
     * @param socket socket on which to listen to the incoming data
     * @param dir    directory in which to store the contents of the socket
     */
    public FileTransfer(Socket socket, String dir) {
        this.isServer = true;
        this.socket = socket;
        this.dir = dir;
        this.arq = new StopWaitProtocol(socket);
    }

    @Override
    public boolean file_req() throws IOException {
        File file = new File(fileName);
        try (InputStream inputStream = new FileInputStream(file)) {
            new Random().nextBytes(sessionNumber);

            StartPacket startPacket = new StartPacket(sessionNumber, fileName, file.length());
            byte[] packetData = startPacket.toBytes();
            if (!arq.data_req(packetData, packetData.length, false)) {
                return false;
            }

            byte[] data = new byte[arq.getMTU()];
            while (inputStream.read(data) != -1) {
                DataPacket dataPacket = new DataPacket(sessionNumber, packetNumber, data);
                packetData = dataPacket.toBytes();
                arq.data_req(packetData, packetData.length, false);
                packetNumber++;
            }

            CRC32 crc32 = new CRC32();
            crc32.update(inputStream.readAllBytes());

            DataPacket lastPacket = new DataPacket(
                    sessionNumber,
                    packetNumber,
                    ByteBuffer.allocate(4).putInt((int) crc32.getValue()).array()
            );
            packetData = lastPacket.toBytes();
            return arq.data_req(packetData, packetData.length, true);
        }
    }

    @Override
    public boolean file_init() throws IOException {
        try {
            DatagramPacket startPacket = socket.receivePacket();
            byte[] startPacketData = startPacket.getData();

            if (!"Start".equals(new String(Arrays.copyOfRange(startPacketData, 3, 8), StandardCharsets.UTF_8))) {
                return false;
            }

            long fileLength = ByteBuffer
                    .allocate(Long.BYTES)
                    .put(Arrays.copyOfRange(startPacketData, 8, 16))
                    .flip()
                    .getLong();

            sessionNumber = Arrays.copyOfRange(startPacketData, 0, 2);

            AcknowledgePacket acknowledgePacket = new AcknowledgePacket(sessionNumber, (byte) 0, (byte) 1);
            socket.sendPacket(acknowledgePacket.toBytes());

            ByteBuffer bytesReceived = ByteBuffer.allocate((int) fileLength + this.arq.getMTU());
            System.out.println("filelength: " + fileLength + "\tallocated: " + bytesReceived.capacity());
            int numberOfBytesReceived = 0;

            while (numberOfBytesReceived < fileLength) {
                byte[] data = arq.data_ind_req();
                bytesReceived.put(data);
                numberOfBytesReceived += data.length;
            }

            short fileNameLength = ByteBuffer
                    .allocate(Short.BYTES)
                    .put(Arrays.copyOfRange(startPacketData, 16, 18))
                    .flip()
                    .getShort();

            acknowledgePacket = new AcknowledgePacket(sessionNumber, packetNumber, (byte) 1, bytesReceived.array());
            socket.sendPacket(acknowledgePacket.toBytes());

            File saveFile = new File(dir, new String(Arrays.copyOfRange(startPacketData, 18, 18 + fileNameLength), StandardCharsets.UTF_8));
            if (saveFile.exists()) {
                saveFile = new File(dir, new String(Arrays.copyOfRange(startPacketData, 18, 18 + fileNameLength), StandardCharsets.UTF_8) + "1");
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(saveFile)) {
                fileOutputStream.write(bytesReceived.array());
            }
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
