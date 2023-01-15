package beleg.packets;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class AcknowledgePacket extends Packet {
    private static final int ACK_NUMBER_LENGTH = 1;
    private static final int MAX_NUMBER_OF_PACKETS_LENGTH = 1;

    public AcknowledgePacket(byte[] sessionNumber, byte packetNumber, byte gbnN) {
        this.byteBuffer = ByteBuffer.allocate(SESSION_NUMBER_LENGTH + ACK_NUMBER_LENGTH + MAX_NUMBER_OF_PACKETS_LENGTH);

        this.byteBuffer.put(sessionNumber);
        this.byteBuffer.put(packetNumber);
        this.byteBuffer.put(gbnN);
    }

    public AcknowledgePacket(byte[] sessionNumber, byte packetNumber, byte gbnN, byte[] data) {
        this(sessionNumber, packetNumber, gbnN);

        byte[] payload = this.byteBuffer.array();
        this.byteBuffer = ByteBuffer.allocate(this.byteBuffer.capacity() + CRC32_LENGTH);
        this.byteBuffer.put(payload);

        CRC32 crc32 = new CRC32();
        crc32.update(data);

        this.byteBuffer.putInt((int) crc32.getValue());
    }
}
