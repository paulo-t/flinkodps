package com.synway.flinkodps.odpsupload.app.model;

import com.aliyun.odps.tunnel.TableTunnel;
import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.flinkodps.odpsupload.app.model
 * @date:2020/4/26
 */
@Data
public class SessionInfo {
    /**
     * 项目名
     */
    private String project;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 数据上传会话
     */
    private TableTunnel.UploadSession uploadSession;
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 上传的blockId
     */
    private AtomicInteger blockId = new AtomicInteger(0);
    /**
     * 获取当前的blockId
     */
    public int getCurrentBlockId(){
        return blockId.get();
    }
    /**
     * 获取一个新的blobkId
     */
    public int getNewBlockId(){
        return blockId.incrementAndGet();
    }
    /**
     * 弃用blockId
     */
    public int abandon(){
        return blockId.addAndGet(99999);
    }
}
