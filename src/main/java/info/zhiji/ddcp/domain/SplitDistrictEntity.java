package info.zhiji.ddcp.domain;

import java.util.List;

import com.google.common.collect.Lists;
import lombok.Data;

/**
 * SplitDistrictEntity
 *
 * @author jeromechan 202202
 */
@Data
public class SplitDistrictEntity {
    /**
     * Province Part, always the 1st~2nd digits
     */
    private String provincePart;

    /**
     * City Part, always the 3rd~4th digits
     */
    private String cityPart;

    /**
     * District Part, always the 5th~6th digits
     */
    private String districtPart;

    /**
     * district name
     */
    private String name;

    /**
     * ChildNodes
     */
    private List<SplitDistrictEntity> childNodes = Lists.newArrayList();

    /**
     * Constructor
     *
     * @param provincePart
     * @param cityPart
     * @param districtPart
     * @param name
     */
    public SplitDistrictEntity(String provincePart, String cityPart, String districtPart, String name) {
        this.provincePart = provincePart;
        this.cityPart = cityPart;
        this.districtPart = districtPart;
        this.name = name;
    }

    /**
     * Constructor
     *
     * @param provincePart
     * @param cityPart
     * @param districtPart
     * @param name
     */
    public SplitDistrictEntity(String provincePart, String cityPart, String districtPart, String name,
                               List<SplitDistrictEntity> childNodes) {
        this.provincePart = provincePart;
        this.cityPart = cityPart;
        this.districtPart = districtPart;
        this.name = name;
        this.childNodes = childNodes;
    }
}
