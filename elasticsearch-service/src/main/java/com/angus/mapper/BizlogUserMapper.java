package com.angus.mapper;

import com.angus.model.BizlogUser;
import com.angus.model.BizlogUserExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BizlogUserMapper {
    long countByExample(BizlogUserExample example);

    int deleteByExample(BizlogUserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BizlogUser record);

    int insertSelective(BizlogUser record);

    List<BizlogUser> selectByExample(BizlogUserExample example);

    BizlogUser selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BizlogUser record, @Param("example") BizlogUserExample example);

    int updateByExample(@Param("record") BizlogUser record, @Param("example") BizlogUserExample example);

    int updateByPrimaryKeySelective(BizlogUser record);

    int updateByPrimaryKey(BizlogUser record);
}