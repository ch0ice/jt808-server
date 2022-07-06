package cn.com.onlinetool.jt808.service.impl;

import cn.com.onlinetool.jt808.bean.BasePacket;
import cn.com.onlinetool.jt808.service.CacheService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheServiceImpl implements CacheService {

    // ==== \/ package

    private static final Map<String, Map<Integer, BasePacket>> packageMap = new ConcurrentHashMap<>();

    @Override
    public boolean containsPackages(String terminalPhone) {
        return packageMap.containsKey(terminalPhone);
    }

    @Override
    public void setPackages(String terminalPhone, Map<Integer, BasePacket> packages) {
        packageMap.put(terminalPhone, packages);
    }

    @Override
    public Map<Integer, BasePacket> getPackages(String terminalPhone) {
        return packageMap.get(terminalPhone);
    }


    // ==== \/ auth
    private static Map<String, String> authMap = new ConcurrentHashMap<>();

    @Override
    public boolean containsAuth(String terminalPhone) {
        return authMap.containsKey(terminalPhone);
    }

    @Override
    public void removeAuth(String terminalPhone) {
        authMap.remove(terminalPhone);
    }

    @Override
    public String getAuth(String terminalPhone) {
        return authMap.get(terminalPhone);
    }

    @Override
    public void setAuth(String terminalPhone, String str) {
        authMap.put(terminalPhone, str);
    }

    // ==== \/ sent package - 目前进保存前一次下发的数据的缓存

    private static Map<String, Map<Integer, BasePacket>> sentPackageMap = new ConcurrentHashMap<>();

    @Override
    public boolean containsSentPackages(String terminalPhone) {
        return sentPackageMap.containsKey(terminalPhone);
    }

    @Override
    public void setSentPackages(String terminalPhone, Map<Integer, BasePacket> packages) {
        sentPackageMap.put(terminalPhone, packages);
    }

    @Override
    public Map<Integer, BasePacket> getSentPackages(String terminalPhone) {
        return sentPackageMap.get(terminalPhone);
    }

}