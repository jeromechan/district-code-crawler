package info.zhiji.ddcp.domain;

import lombok.Data;

/**
 * DistrictEntity
 *
 * @author jeromechan 202202
 */
@Data
public class DistrictEntity {
    /**
     * district code
     */
    private String code;

    /**
     * district name
     */
    private String name;

    /**
     * Constructor
     *
     * @param code
     * @param name
     */
    public DistrictEntity(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
