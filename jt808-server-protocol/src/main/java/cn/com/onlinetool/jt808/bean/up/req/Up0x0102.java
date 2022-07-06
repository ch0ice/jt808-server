package cn.com.onlinetool.jt808.bean.up.req;

import cn.com.onlinetool.jt808.bean.BasePacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.ToString;

/**
 * 终端鉴权
 */
@ToString(callSuper = true)
@Data
public class Up0x0102 extends BasePacket {

    private String authCode;//鉴权码

    public Up0x0102(ByteBuf byteBuf) {
        super(byteBuf);
        this.parse();
    }

    private void parse() {
        this.setAuthCode(readString(this.msgBody.readableBytes()));
    }
}
