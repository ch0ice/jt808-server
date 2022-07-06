package cn.com.onlinetool.jt808.bean.down.res;

import cn.com.onlinetool.jt808.bean.BasePacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * @author: choice
 * 终端通用应答
 */
@Data
public class Down0x0001 extends BasePacket {

    private short replyFlowId; //应答流水号 2字节
    private short replyId; //应答 ID  2字节
    private byte result;    //结果 1字节 0:成功/确认;1:失败;2:消息有误;3:不支持

    public Down0x0001(ByteBuf byteBuf) {
        super(byteBuf);
        this.parse();
    }

    private void parse() {
        ByteBuf bb = this.msgBody;
        this.setReplyFlowId(bb.readShort());
        this.setReplyId(bb.readShort());
        this.setResult(bb.readByte());
    }
}
