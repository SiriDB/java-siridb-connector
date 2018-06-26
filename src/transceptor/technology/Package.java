package transceptor.technology;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 *
 * @author Tristan Nottelman
 */
class Package {

    public static final int HEADER_SIZE = 8;
    private final int length;
    private final short id;
    private final byte type;
    private final byte checkbit;
    private byte[] body;

    Package(byte[] payload) {
        this.length = convertByteToNumber(Arrays.copyOfRange(payload, 0, 4)).intValue();
        this.id = convertByteToNumber(Arrays.copyOfRange(payload, 4, 6)).shortValue();
        this.type = (byte) (payload[6]);
        this.checkbit = (byte) (payload[7]);
        this.body = null;
    }

    Package(int length, short id, byte type, byte[] body) {
        this.length = length;
        this.id = id;
        this.type = type;
        this.checkbit = (byte) (type ^ 255);
        this.body = body;
    }

    boolean isValid() {
        return (type == ((checkbit + 256) ^ 255));
    }

    void setBody(byte[] body) {
        this.body = body;
    }

    ByteBuffer toByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(8 + length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.clear();
        buffer.putInt(length);
        buffer.putShort(id);
        buffer.put(type);
        buffer.put(checkbit);
        if (getBody() != null) {
            buffer.put(body);
        }
        buffer.flip();
        return buffer;
    }

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

    byte[] getBody() {
        return body;
    }

    int getLength() {
        return length;
    }

    short getId() {
        return id;
    }

    short getCheckbit() {
        return checkbit;
    }

    byte getType() {
        return type;
    }
    
    @Override
    public String toString() {
        return Package.class + ": " + getLength() + ", " + getId() + ", " + getType() + ", " + getCheckbit() + ", " + Arrays.toString(getBody());
    }
}
