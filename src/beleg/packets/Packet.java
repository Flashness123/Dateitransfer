package beleg.packets;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public abstract class Packet {
    protected static final int SESSION_NUMBER_LENGTH = 2;
    protected static final int PAKET_NUMBER_LENGTH = 1;
    protected static final int CRC32_LENGTH = 4;

    protected ByteBuffer byteBuffer;

    public byte[] toBytes() {
        byte[] bytes = this.byteBuffer.array();
        // System.out.println(new String(bytes, StandardCharsets.UTF_8));
        return bytes;
    }
}
