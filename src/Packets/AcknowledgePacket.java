package Packets;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

public class AcknowledgePacket extends Packet {
    private static final int AckNumber_Length = 1;
    private static final int MaximalPackageNumber_Length = 1;   //1 da nur stop and wait, gbn wahrscheinlich nicht machen

    public AcknowledgePacket(byte[] sessionNumber, byte packetNumber, byte gbnN) {
        this.byteBuffer = ByteBuffer.allocate(SessionNumberLength + AckNumber_Length + MaximalPackageNumber_Length);

        this.byteBuffer.put(sessionNumber);
        this.byteBuffer.put(packetNumber);
        this.byteBuffer.put(gbnN);
    }

    public AcknowledgePacket(byte[] sessionNumber, byte packetNumber, byte gbnN, byte[] data) {
        this(sessionNumber, packetNumber, gbnN);

        byte[] payload = this.byteBuffer.array();
        this.byteBuffer = ByteBuffer.allocate(this.byteBuffer.capacity() + Crc32Length);
        this.byteBuffer.put(payload);

        CRC32 crc32 = new CRC32();
        crc32.update(data);

        this.byteBuffer.putInt((int) crc32.getValue());
    }
}
