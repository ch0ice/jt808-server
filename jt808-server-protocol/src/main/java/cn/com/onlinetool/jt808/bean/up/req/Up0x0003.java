package cn.com.onlinetool.jt808.bean.up.req;

import cn.com.onlinetool.jt808.bean.BasePacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.ToString;

/**
 * 终端注销
 */
@ToString(callSuper = true)
@Data
public class Up0x0003 extends BasePacket {
    public Up0x0003(ByteBuf byteBuf) {
        super(byteBuf);
    }
}
