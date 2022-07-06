package cn.com.onlinetool.jt808.bean.up.req;

import cn.com.onlinetool.jt808.bean.BasePacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 位置信息汇报 批量
 */
@ToString(callSuper = true)
@Data
public class Up0x0704 extends BasePacket {

    public Up0x0704(ByteBuf byteBuf) {
        super(byteBuf);
        this.parse();

    }

    private short count;//包个数 1字节
    private short type;//位置类型0:正常位置批量汇报; 1:盲区补报
    private List<Up0x0200> up0x0200List = new ArrayList<>();//位置列表


    private void parse() {
        ByteBuf bb = this.msgBody;
        this.setCount(bb.readShort());
        this.setType(bb.readByte());
        for (int i = 0; i < this.getCount(); i++) {
            short len = bb.readShort();
            Up0x0200 up0x0200 = new Up0x0200(bb.readBytes(len));
            this.getUp0x0200List().add(up0x0200);
        }
    }
}
