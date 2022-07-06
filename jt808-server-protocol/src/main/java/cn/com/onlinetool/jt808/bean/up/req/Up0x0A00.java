package cn.com.onlinetool.jt808.bean.up.req;

import cn.com.onlinetool.jt808.bean.BasePacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.ToString;

/**
 * 终端RSA公钥
 */
@ToString(callSuper = true)
@Data
public class Up0x0A00 extends BasePacket {

    private byte[] e;//终端 RSA 公钥{e,n}中的 e   4字节
    private byte[] n;//RSA 公钥{e,n}中的 n  128字节

    public Up0x0A00(ByteBuf byteBuf) {
        super(byteBuf);
        this.parse();
    }

    private void parse() {
        this.setE(readBytes(4));
        this.setN(readBytes(128));
    }
}
