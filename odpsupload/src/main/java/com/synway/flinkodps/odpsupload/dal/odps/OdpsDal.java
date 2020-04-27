package com.synway.flinkodps.odpsupload.dal.odps;

import com.aliyun.odps.Instance;
import com.aliyun.odps.Odps;
import com.aliyun.odps.OdpsException;
import com.aliyun.odps.task.SQLTask;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.standardizedataplatform.odpsupload.dal.odps
 * @date:2019/12/30
 */
@Slf4j
public class OdpsDal extends OdpsAbstractBase implements Serializable {
    private static final long serialVersionUID = -3030699848716868682L;

    private OdpsDal(){}

    private volatile static OdpsDal odpsDal = null;
    public static OdpsDal build(){
        if(Objects.isNull(odpsDal)){
            synchronized (OdpsDal.class){
                if(Objects.isNull(odpsDal)){
                    odpsDal = new OdpsDal();
                }
            }
        }
        return odpsDal;
    }

    @Override
    public boolean execSql(String sql) {
        Odps odps = createOdps();

        Instance instance;
        try {
            instance = SQLTask.run(odps, sql + ";");
            instance.waitForSuccess();

            Map<String, Instance.TaskStatus> taskStatus = instance.getTaskStatus();
            for (Map.Entry<String, Instance.TaskStatus> status : taskStatus.entrySet()) {
                status.getKey();// AnonymousSQLTask
            }
        } catch (OdpsException e) {
            log.error("execute sql error:{}.{}",e.getMessage(),sql);
            return false;
        }

        return true;
    }

    @Override
    public List<Object[]> execBySql(String sql) {
        Odps odps = createOdps();

        List<Object[]> list = Lists.newArrayList();

        Instance instance;
        try {
            instance = SQLTask.run(odps, sql + ";");
            instance.waitForSuccess();

            Map<String, String> results = instance.getTaskResults();
            Map<String, Instance.TaskStatus> taskStatus = instance.getTaskStatus();

            String strResult;
            for (Map.Entry<String, Instance.TaskStatus> status : taskStatus.entrySet()) {
                strResult = results.get(status.getKey());
                strResult = strResult.replace("\"", "");

                String[] strs = strResult.split("\n");

                int j = 0;
                for (String str : strs) {
                    //去除第一行标题头
                    if (j == 0) {
                        j++;
                        continue;
                    }
                    String[] strFields = str.split(",");

                    Object[] obj = new Object[strFields.length];
                    for (int i = 0; i < obj.length; i++) {
                        obj[i] = strFields[i];
                    }
                    list.add(obj);
                }
            }
        } catch (OdpsException e) {
            log.error("execute sql error:{}.{}",e.getMessage(),sql);
        }

        return list;
    }

    @Override
    public <T> List<T> execQuery(String sql, Class<T> c) {
        return null;
    }
}
