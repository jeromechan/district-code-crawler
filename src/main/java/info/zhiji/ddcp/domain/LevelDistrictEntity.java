package info.zhiji.ddcp.domain;

import java.util.Map;

import com.google.common.collect.Maps;
import lombok.Data;

/**
 * LevelDistrictEntity
 * Demo Data Structure:
 * {
 *     "21": {
 *         "element": {
 *             "code": "210000",
 *             "name": "省"
 *         },
 *         "childNodes": {
 *             "10": {
 *                 "element": {
 *                     "code": "211000",
 *                     "name": "市"
 *                 },
 *                 "childNodes": {
 *                     "01": {
 *                         "element": {
 *                             "code": "211001",
 *                             "name": "区1"
 *                         },
 *                         "childNodes": {}
 *                     },
 *                     "02": {
 *                         "element": {
 *                             "code": "211002",
 *                             "name": "区2"
 *                         },
 *                         "childNodes": {}
 *                     }
 *                 }
 *             }
 *         }
 *     }
 * }
 *
 * @author jeromechan 202202
 */
@Data
public class LevelDistrictEntity {

    /**
     * Current Node
     */
    private SplitDistrictEntity element;

    /**
     * Children Nodes
     */
    private Map<String, LevelDistrictEntity> childNodes = Maps.newHashMap();

    /**
     * Constructor
     */
    public LevelDistrictEntity() {
    }

    /**
     * Constructor
     *
     * @param element
     * @param childNodes
     */
    public LevelDistrictEntity(SplitDistrictEntity element) {
        this.element = element;
    }
}
