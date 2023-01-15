package beleg.packets;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

public class StartPacket extends Packet {
    private static final byte[] START_SIGNAL = "Start".getBytes();
    private static final int FILE_LENGTH_LENGTH = 8;
    private static final int FILENAME_LENGTH_LENGTH = 2;

    /**
     *
     * @param sessionNumber Session number for each session
     * @param fileName File name of the file to transmit
     * @param fileLength length of file to transmit
     */
    public StartPacket(byte[] sessionNumber, String fileName, long fileLength) {
        /*this.byteBuffer = ByteBuffer.allocate(
            (
                SESSION_NUMBER_LENGTH +
                PAKET_NUMBER_LENGTH +
                START_SIGNAL.length +
                FILE_LENGTH_LENGTH +
                FILENAME_LENGTH_LENGTH +
                fileName.length()
            )
        );*/

        ByteBuffer nonPayloadByteBuffer = ByteBuffer.allocate(
                SESSION_NUMBER_LENGTH +
                        PAKET_NUMBER_LENGTH
        );

        nonPayloadByteBuffer.put(sessionNumber);

        byte paketNumber = 0;
        nonPayloadByteBuffer.put(paketNumber);
        //nonPayloadByteBuffer.put(START_SIGNAL);

        ByteBuffer payloadByteBuffer = ByteBuffer.allocate(
                START_SIGNAL.length+
                        FILE_LENGTH_LENGTH +
                        FILENAME_LENGTH_LENGTH +
                        fileName.length()
        );

        payloadByteBuffer.put(START_SIGNAL);
        payloadByteBuffer.putLong(fileLength);
        payloadByteBuffer.putShort((short) fileName.length());
        payloadByteBuffer.put(fileName.getBytes());

        this.byteBuffer = ByteBuffer.allocate(
                nonPayloadByteBuffer.capacity() +
                        payloadByteBuffer.capacity() +
                        CRC32_LENGTH
        );

        this.byteBuffer.put(nonPayloadByteBuffer.array());
        this.byteBuffer.put(payloadByteBuffer.array());

        CRC32 crc32 = new CRC32();
        crc32.update(payloadByteBuffer.array());

        this.byteBuffer.putInt((int) crc32.getValue());
    }
}

