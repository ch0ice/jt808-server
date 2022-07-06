package cn.com.onlinetool.jt808.service;

import cn.com.onlinetool.jt808.bean.BasePacket;

import java.util.Map;

/**
 *
 */
public interface CacheService {
    /**
     * 是否含此电话号码对应的包
     *
     * @param terminalPhone 终端对应 12 位电话号码
     * @return 是否含有
     */
    boolean containsPackages(String terminalPhone);

    /**
     * 设置电话号码对应的包组
     *
     * @param terminalPhone 终端对应 12 位电话号码
     * @param packages      包列表
     */
    void setPackages(String terminalPhone, Map<Integer, BasePacket> packages);

    /**
     * 获取电话号码对应的包组
     *
     * @param terminalPhone 终端对应 12 位电话号码
     * @return 包列表
     */
    Map<Integer, BasePacket> getPackages(String terminalPhone);

    /**
     * 电话号码对应的会话是否已经鉴权
     *
     * @param terminalPhone 终端对应 12 位电话号码
     * @return 鉴权与否
     */
    boolean containsAuth(String terminalPhone);

    /**
     * 去掉电话号码对应的鉴权信息
     *
     * @param terminalPhone 终端对应 12 位电话号码
     */
    void removeAuth(String terminalPhone);

    /**
     * 通过电话号码获取鉴权码
     *
     * @param terminalPhone 终端对应 12 位电话号码
     * @return 鉴权码
     */
    String getAuth(String terminalPhone);

    /**
     * 设置电话号码对应的鉴权码
     *
     * @param terminalPhone 终端对应 12 位电话号码
     * @param str           鉴权码
     */
    void setAuth(String terminalPhone, String str);

    /**
     * 是否含此电话号码对应的包
     *
     * @param terminalPhone 终端对应 12 位电话号码
     * @return 是否含有
     */
    boolean containsSentPackages(String terminalPhone);

    /**
     * 设置电话号码对应的包组
     *
     * @param terminalPhone 终端对应 12 位电话号码
     * @param packages      包列表
     */
    void setSentPackages(String terminalPhone, Map<Integer, BasePacket> packages);

    /**
     * 获取电话号码对应的包组
     *
     * @param terminalPhone 终端对应 12 位电话号码
     * @return 包列表
     */
    Map<Integer, BasePacket> getSentPackages(String terminalPhone);
}