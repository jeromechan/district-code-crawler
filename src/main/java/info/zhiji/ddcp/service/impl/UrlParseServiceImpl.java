package info.zhiji.ddcp.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import info.zhiji.ddcp.domain.DistrictEntity;
import info.zhiji.ddcp.domain.LevelDistrictEntity;
import info.zhiji.ddcp.domain.SplitDistrictEntity;
import info.zhiji.ddcp.service.IUrlParseService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * UrlParseServiceImpl
 *
 * @author jeromechan 202202
 */
@Service
@Slf4j
public class UrlParseServiceImpl implements IUrlParseService {

    @Value("${crawlUrl}")
    private String crawlUrl;

    @Value("${sqlTemplate}")
    private String sqlTemplate;

    /**
     * Generate SQL List
     *
     * @return
     * @throws IOException
     */
    @Override
    public List<String> generateSqlList() throws IOException {
        // 解析原HTML数据结构
        Map<String, DistrictEntity> districtEntities = crawlSourceHtml();

        // 分拆解析List结果数据
        Map<String, LevelDistrictEntity> finalRes = extractDistrictEntities(districtEntities);

        // 二级无实例的，自动将三级提升至二级
        reArrangeSpecialItems(finalRes);

        // 拼接SQL语句
        List<String> sqlList = generateSqlDml(finalRes);
        return sqlList;
    }

    /**
     * 拼接SQL语句
     *
     * @param finalRes
     * @return
     */
    private List<String> generateSqlDml(Map<String, LevelDistrictEntity> finalRes) {
        List<String> sqlList = Lists.newArrayList();
        for (Entry<String, LevelDistrictEntity> province : finalRes.entrySet()) {
            SplitDistrictEntity itemProvince = province.getValue().getElement();
            generateSqlList(sqlList, itemProvince, null);

            if (!CollectionUtils.isEmpty(province.getValue().getChildNodes())) {
                for (Entry<String, LevelDistrictEntity> city : province.getValue()
                    .getChildNodes().entrySet()) {
                    SplitDistrictEntity itemCity = city.getValue().getElement();
                    generateSqlList(sqlList, itemCity, itemProvince);

                    if (!CollectionUtils.isEmpty(city.getValue().getChildNodes())) {
                        for (Entry<String, LevelDistrictEntity> district : city.getValue()
                            .getChildNodes().entrySet()) {
                            SplitDistrictEntity itemDistrict = district.getValue().getElement();
                            generateSqlList(sqlList, itemDistrict, itemCity);
                        }
                    }
                }
            }
        }
        return sqlList;
    }

    /**
     * 二级无实例的，自动将三级提升至二级
     *
     * @param finalRes
     */
    private void reArrangeSpecialItems(Map<String, LevelDistrictEntity> finalRes) {
        List<String> upgradeList = Lists.newArrayList();
        for (Entry<String, LevelDistrictEntity> provinceEntry : finalRes.entrySet()) {
            for (Entry<String, LevelDistrictEntity> cityEntry : provinceEntry.getValue().getChildNodes()
                .entrySet()) {
                if (null == cityEntry.getValue().getElement()) {
                    upgradeList.add(provinceEntry.getKey() + cityEntry.getKey());
                    continue;
                }
            }
        }
        if (!CollectionUtils.isEmpty(upgradeList)) {
            for (String provinceAndCity : upgradeList) {
                String provincePart = provinceAndCity.substring(0, 2);
                String cityPart = provinceAndCity.substring(2, 4);
                if (superProvinceCity.contains(provinceAndCity)) {
                    // 直辖市
                    SplitDistrictEntity provinceElement = finalRes.get(provincePart).getElement();
                    finalRes.get(provincePart).getChildNodes().get(cityPart).setElement(new SplitDistrictEntity(
                        provinceElement.getProvincePart(),
                        provinceElement.getCityPart(),
                        "01",
                        provinceElement.getName(),
                        Lists.newArrayList()
                    ));
                } else {
                    // 非直辖市
                    int i = 1;
                    for (Entry<String, LevelDistrictEntity> entityEntry : finalRes.get(provincePart)
                        .getChildNodes().get(cityPart).getChildNodes().entrySet()) {
                        finalRes.get(provincePart).getChildNodes().put(cityPart + "-" + i, entityEntry.getValue());
                        i++;
                    }
                    finalRes.get(provincePart).getChildNodes().remove(cityPart);
                }
            }
        }
    }

    /**
     * 分拆解析List结果数据
     *
     * @param districtEntities
     * @return
     */
    private Map<String, LevelDistrictEntity> extractDistrictEntities(
        Map<String, DistrictEntity> districtEntities) {
        Map<String, SplitDistrictEntity> splitDistrictEntities = Maps.newHashMap();
        for (DistrictEntity districtEntity : districtEntities.values()) {
            SplitDistrictEntity splitDistrictEntity = new SplitDistrictEntity(
                districtEntity.getCode().substring(0, 2),
                districtEntity.getCode().substring(2, 4),
                districtEntity.getCode().substring(4, 6),
                districtEntity.getName()
            );
            splitDistrictEntities.put(districtEntity.getCode(), splitDistrictEntity);
        }

        Map<String, LevelDistrictEntity> finalRes = Maps.newHashMap();
        for (Entry<String, SplitDistrictEntity> entityEntry : splitDistrictEntities.entrySet()) {
            String code = entityEntry.getKey();
            SplitDistrictEntity splitDistrictEntity = entityEntry.getValue();
            String provincePart = splitDistrictEntity.getProvincePart();
            String cityPart = splitDistrictEntity.getCityPart();
            String districtPart = splitDistrictEntity.getDistrictPart();
            // 构建基础结构
            if (!finalRes.containsKey(provincePart)) {
                finalRes.put(provincePart, new LevelDistrictEntity());
            }
            if ("00".equals(cityPart) && "00".equals(districtPart)) {
                finalRes.get(provincePart).setElement(splitDistrictEntity);
                continue;
            }

            if (CollectionUtils.isEmpty(finalRes.get(provincePart).getChildNodes())
                || !finalRes.get(provincePart).getChildNodes().containsKey(cityPart)) {
                finalRes.get(provincePart).getChildNodes().put(cityPart, new LevelDistrictEntity());
            }
            if ("00".equals(districtPart)) {
                finalRes.get(provincePart).getChildNodes().get(cityPart).setElement(splitDistrictEntity);
                continue;
            }

            if (CollectionUtils.isEmpty(finalRes.get(provincePart).getChildNodes().get(cityPart).getChildNodes())
                || !finalRes.get(provincePart).getChildNodes().get(cityPart).getChildNodes().containsKey(districtPart)) {
                finalRes.get(provincePart).getChildNodes().get(cityPart).getChildNodes().put(districtPart, new LevelDistrictEntity());
            }
            finalRes.get(provincePart).getChildNodes().get(cityPart).getChildNodes().get(districtPart).setElement(splitDistrictEntity);
        }
        return finalRes;
    }

    /**
     * 解析原HTML数据结构
     *
     * @return
     * @throws IOException
     */
    private Map<String, DistrictEntity> crawlSourceHtml() throws IOException {
        Map<String, DistrictEntity> districtEntities = Maps.newHashMap();
        Document doc = Jsoup.connect(crawlUrl).get();
        Elements trs = doc.select("tr");
        final int trStartIndex = 3;
        final int trEndIndex = 3213;
        for (int i = 0; i < trs.size(); i++) {
            if (i > trEndIndex) {
                break;
            }
            if (i < trStartIndex) {
                continue;
            }

            Elements tds = trs.get(i).select("td");
            if (tds.get(1).childNodeSize() < 1) {
                continue;
            }

            String code = tds.get(1).childNode(0).toString();
            String name = null;
            if (tds.get(2).childNodeSize() >= 2) {
                name = tds.get(2).childNode(1).toString();
            } else if (tds.get(2).childNodeSize() == 1){
                name = tds.get(2).childNode(0).toString();
            }

            log.info(tds.toString());
            log.info("-----" + code + "-----" + name + "------");
            DistrictEntity districtEntity = new DistrictEntity(code, name);
            districtEntities.put(code, districtEntity);
        }
        return districtEntities;
    }

    /**
     * 拼接SQL语句
     *
     * @param sqlList
     * @param item
     * @param parentItem
     */
    private void generateSqlList(List<String> sqlList, SplitDistrictEntity item,
                                 SplitDistrictEntity parentItem) {
        String sql = "";
        if (null == parentItem) {
            sql = String.format(sqlTemplate, item.getProvincePart() + item.getCityPart() + item.getDistrictPart(),
                item.getName(), "1");
        } else {
            sql = String.format(sqlTemplate, item.getProvincePart() + item.getCityPart() + item.getDistrictPart(),
                item.getName(), parentItem.getProvincePart() + parentItem.getCityPart() + parentItem.getDistrictPart());
        }
        if (!sqlList.contains(sql)) {
            sqlList.add(sql);
        }
    }
}
