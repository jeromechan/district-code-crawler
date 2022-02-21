package info.zhiji.ddcp.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import info.zhiji.ddcp.service.IUrlParseService;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Start Controller for domestic-district-code-parser
 *
 * @author jeromechan 202202
 */
@RestController
@RequestMapping(value = "/starter")
public class StarterController {

    /**
     * iUrlParseService
     */
    @Resource
    private IUrlParseService iUrlParseService;

    /**
     * 国家行政区划代码HTML抓取与解析
     *
     * @author jeromechan 202202
     * @return
     */
    @GetMapping(value = "/parse")
    public String parse() throws IOException {
        List<String> sqlList = iUrlParseService.generateSqlList();
        StringBuilder sqlStr = new StringBuilder();
        if (!CollectionUtils.isEmpty(sqlList)) {
            for (String str : sqlList) {
                sqlStr.append(str).append("<br/>");
            }
        }
        return sqlStr.toString();
    }
}
