package cn.com.onlinetool.jt808.bean.up.res;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.consts.JT808Constant;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.Data;
import org.springframework.beans.BeanUtils;

/**
 * 平台通用应答
 */
@Data
public class Up0x8001 extends BasePacket {

    public static final byte SUCCESS = 0;//成功/确认
    public static final byte FAILURE = 1;//失败
    public static final byte MSG_ERROR = 2;//消息有误
    public static final byte UNSUPPORTED = 3;//不支持
    public static final byte ALARM_PROCESS_ACK = 4;//报警处理确认

    private short replyFlowId; //应答流水号 2字节
    private short replyId; //应答 ID  2字节
    private byte result;    //结果 1字节

    public Up0x8001() {
        this.getHeader().setMsgId(JT808Constant.UP_COMMON_PLATFORM_RES);
    }

    @Override
    public ByteBuf toByteBufMsg() {
        ByteBuf bb = super.toByteBufMsg();
        bb.writeShort(replyFlowId);
        bb.writeShort(replyId);
        bb.writeByte(result);
        return bb;
    }

    public static Up0x8001 success(BasePacket msg, int flowId) {
        return response(msg,flowId,SUCCESS);
    }


    public static Up0x8001 failure(BasePacket msg, int flowId) {
        return response(msg,flowId,FAILURE);
    }

    public static Up0x8001 unsupported(BasePacket msg, int flowId) {
        return response(msg,flowId,UNSUPPORTED);
    }


    private static Up0x8001 response(BasePacket msg, int flowId,byte result){
        Up0x8001 resp = new Up0x8001();
        Header header = msg.getHeader();
        Header respHeader = resp.getHeader();
        BeanUtils.copyProperties(header, respHeader);
        //设置消息ID
        respHeader.setMsgId(JT808Constant.UP_COMMON_PLATFORM_RES);
        //设置消息流水号
        respHeader.setFlowId(flowId);

        //封装返回的信息
        ByteBuf msgBody = ByteBufAllocator.DEFAULT.heapBuffer(5);
        //应答流水号(2个字节)对应的终端消息的流水号
        msgBody.writeShort(header.getFlowId());
        //应答ID(2个字节)对应的终端消息的ID
        msgBody.writeShort(header.getMsgId());
        msgBody.writeByte(result);
        //设置返回的消息
        resp.setMsgBody(msgBody);
        return resp;



    }
}
