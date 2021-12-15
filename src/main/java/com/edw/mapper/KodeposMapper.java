package com.edw.mapper;

import com.edw.bean.Kodepos;
import org.apache.ibatis.annotations.Insert;

/**
 * <pre>
 *     com.edw.mapper.KodeposMapper
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 13 Des 2021 15:08
 */
public interface KodeposMapper {

    @Insert("INSERT INTO `db_kodepos`.`tbl_kodepos` " +
            "(`kelurahan`, `kecamatan`, `kabupaten`, `provinsi`, `kodepos`) " +
            "VALUES (#{kelurahan}, #{kecamatan}, #{kabupaten}, #{provinsi}, #{kodepos})")
    Integer insert(Kodepos kodepos);
}
