package com.edw.mapper;

import com.edw.bean.Regions;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

/**
 * <pre>
 *     com.edw.mapper.RegionsMapper
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 14 Des 2021 12:19
 */
public interface RegionsMapper {

    @Insert("INSERT INTO `tbl_regions` " +
            "(`region_code`, `region_name`, `parent_code`) " +
            "VALUES (#{regionCode}, #{regionName}, #{parentCode})")
    Integer insert(Regions regions);

    @Select("select * from `tbl_regions` where region_code = #{regionCode}")
    Regions getRegion(String regionCode);
}
