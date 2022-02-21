package info.zhiji.ddcp.service;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * IUrlParseService
 *
 * @author jeromechan 202202
 */
public interface IUrlParseService {

    /**
     * 直辖市"省code-市code"集合
     */
    public static final List<String> superProvinceCity = Lists.newArrayList("1101", "1201", "3101", "5001", "5002");

    /**
     * Generate SQL List
     *
     * @return
     * @throws IOException
     */
    List<String> generateSqlList() throws IOException;
}
