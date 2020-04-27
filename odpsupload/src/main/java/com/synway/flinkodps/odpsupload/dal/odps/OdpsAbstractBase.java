package com.synway.flinkodps.odpsupload.dal.odps;

import com.aliyun.odps.Odps;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.synway.flinkodps.common.utils.ConfigUtils;
import com.synway.flinkodps.odpsupload.dal.DbBase;

import java.io.Serializable;

/**
 * @author: create by paulo
 * @version: v1.0
 * @description: com.synway.standardizedataplatform.odpsupload.dal.odps
 * @date:2019/12/30
 */
public abstract class OdpsAbstractBase implements DbBase, Serializable {
    private static final long serialVersionUID = 2823993243587610054L;

    /**
     * 创建odps连接
     */
    public Odps createOdps() {
        String accessId = ConfigUtils.get("access-id","LbmcnJnX7UYBEOsl");
        String accessKey = ConfigUtils.get("access-key","SeQByyMUA3flRTQhdtts61JiQWmofD");
        String odpsUrl = ConfigUtils.get("odps-url","http://service.cn-shaoxing-ses-d01.odps.ses.jz/api");
        String odpsTabProject =ConfigUtils.get("odps-tab-project","synods");

        Account account = new AliyunAccount(accessId,accessKey);

        Odps odps = new Odps(account);
        odps.setEndpoint(odpsUrl);
        odps.setDefaultProject(odpsTabProject);
        return odps;
    }
}
