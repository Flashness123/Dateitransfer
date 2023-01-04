package Packets;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Packet {
    protected static final int SessionNumberLength = 2;//vererbt und fur erbende klassen sichtbar
    protected static final int Crc32Length = 4;
    protected static final int PaketNumberLength = 1;

    protected ByteBuffer byteBuffer;

    public byte[] toBytes(){
        byte[] bytes = this.byteBuffer.array();
        return bytes;
    }
}
