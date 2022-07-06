package cn.com.onlinetool.jt808.bean.up.res;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.consts.JT808Constant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 终端注册应答
 */
@Data
public class Up0x8100 extends BasePacket {

    public static final byte SUCCESS = 0;//成功
    public static final byte VEHICLE_ALREADY_REGISTER = 1;//车辆已被注册
    public static final byte NOT_IN_DB = 2;//数据库无该车辆
    public static final byte TERMINAL_ALREADY_REGISTER = 3;//终端已被注册

    private int replyFlowId; //应答流水号 2字节
    private byte result;    //结果 1字节
    private String authCode; //鉴权码

    public Up0x8100() {
        this.getHeader().setMsgId(JT808Constant.UP_REGISTER_RES);
    }

    @Override
    public ByteBuf toByteBufMsg() {
        ByteBuf bb = super.toByteBufMsg();
        bb.writeShort(replyFlowId);
        bb.writeByte(result);
        if (result == SUCCESS && StringUtils.isNotBlank(authCode)) {//成功才写入鉴权码
            bb.writeBytes(authCode.getBytes(JT808Constant.DEFAULT_CHARSET));
        }
        return bb;
    }

    public static Up0x8100 success(BasePacket msg, int flowId) {
        return response(msg,flowId,SUCCESS);
    }

    private static Up0x8100 response(BasePacket msg, int flowId,byte result){
        Up0x8100 resp = new Up0x8100();
        Header header = resp.getHeader();
        header.setTerminalPhone(msg.getHeader().getTerminalPhone());
        header.setFlowId(flowId);
        resp.setReplyFlowId(msg.getHeader().getFlowId());
        //封装返回的信息
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.heapBuffer(5);
        //应答流水号(2个字节)对应的终端消息的流水号
        byteBuf.writeShort(header.getFlowId());
        byteBuf.writeByte(result);
        //鉴权码
        if(result == SUCCESS){
            byteBuf.writeBytes(JT808Constant.AUTH_CODE.getBytes(JT808Constant.DEFAULT_CHARSET));
        }
        //设置返回的消息
        resp.setMsgBody(byteBuf);
        return resp;
    }
}
