package cn.com.onlinetool.jt808.bean.down.res;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.bean.up.req.Up0x0200;
import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * @author: choice
 * 位置信息查询应答
 */
@Data
public class Down0x0201 extends BasePacket {
    public Down0x0201(ByteBuf byteBuf) {
        super(byteBuf);
        this.parse();
    }

    private short flowId;// 流水号 2字节
    private Up0x0200 up0x0200;


    private void parse() {
        ByteBuf bb = this.msgBody;
        this.setFlowId(bb.readShort());
        Up0x0200 up0x0200 = new Up0x0200(bb);
        this.setUp0x0200(up0x0200);
    }
}
