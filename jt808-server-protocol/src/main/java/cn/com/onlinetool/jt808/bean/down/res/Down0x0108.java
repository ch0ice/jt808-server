package cn.com.onlinetool.jt808.bean.down.res;

import cn.com.onlinetool.jt808.bean.BasePacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

/**
 * @author: choice
 * 终端升级结果通知
 */
@Data
public class Down0x0108 extends BasePacket {
    public Down0x0108(ByteBuf byteBuf) {
        super(byteBuf);
        this.parse();
    }

    private int type;// 升级类型 1字节 0:终端，12:道路运输证 IC 卡读卡器，52:北斗 卫星定位模块
    private int result;// 升级结果 1字节 0:成功，1:失败，2:取消


    private void parse() {
        ByteBuf bb = this.msgBody;
        this.setType(bb.readByte());
        this.setResult(bb.readByte());
    }
}
