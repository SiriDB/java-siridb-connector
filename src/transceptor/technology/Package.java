package transceptor.technology;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 *
 * @author Tristan Nottelman
 */
public class Package {

    public static final int HEADER_SIZE = 8;
    private int length;
    private short pid;
    private byte tipe;
    private byte checkbit;
    private byte[] body;

    public Package(byte[] payload) {
        this.length = convertByteToNumber(Arrays.copyOfRange(payload, 0, 4)).intValue();
        this.pid = convertByteToNumber(Arrays.copyOfRange(payload, 4, 6)).shortValue();
        this.tipe = payload[6];
        this.checkbit = payload[7];
        this.body = null;
    }

    public Package(int length, short pid, byte tipe, byte checkbit, byte[] body) {
        this.length = length;
        this.pid = pid;
        this.tipe = tipe;
        this.checkbit = checkbit;
        this.body = body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public ByteBuffer toByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(8 + length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.clear();
        buffer.putInt(length);
        buffer.putShort(pid);
        buffer.put(tipe);
        buffer.put(checkbit);
        buffer.put(body);
        buffer.flip();
        return buffer;
    }

    /**
     * Converts array of bytes to number
     *
     * @param b byte array
     * @return
     */
    private Number convertByteToNumber(byte[] b) {
        ByteBuffer wrapped = ByteBuffer.wrap(b);
        wrapped.order(ByteOrder.LITTLE_ENDIAN);
        switch (b.length) {
            case 1:
                return (int) wrapped.get(0);
            case 2:
                return (int) wrapped.getShort();
            case 4:
                return wrapped.getInt();
            case 8:
                return wrapped.getLong();
        }
        return 0;
    }
    
    public byte[] getBody() {
        return body;
    }

    public int getLength() {
        return length;
    }
    
    public short getPid() {
        return pid;
    }
}
