package org.ramer.admin.system.repository.common;

import org.ramer.admin.system.entity.domain.common.DataDict;
import org.ramer.admin.system.repository.BaseRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DataDictRepository extends BaseRepository<DataDict, Long> {
  DataDict findByCodeAndState(String code, int state);

  DataDict findByDataDictTypeCodeAndCodeAndState(String dataDictTypeCode, String code, int state);

  @Query(
      "from org.ramer.admin.system.entity.domain.common.DataDict dd where dd.dataDictType.code= :typeCode and dd.state= :state")
  List<DataDict> findByTypeCodeAndState(
      @Param("typeCode") String typeCode, @Param("state") int state);

  @Query(
      "from org.ramer.admin.system.entity.domain.common.DataDict dd where dd.dataDictType.code= :typeCode and dd.state= :state")
  Page<DataDict> findByTypeCodeAndState(
      @Param("typeCode") String typeCode, @Param("state") int state, Pageable pageable);

  @Query(
      "from org.ramer.admin.system.entity.domain.common.DataDict dd where dd.dataDictType.code= :typeCode and dd.state= :state and (dd.name like :criteria or dd.code like :criteria or dd.remark like :criteria )")
  Page<DataDict> findByTypeCodeAndState(
      @Param("typeCode") String typeCode,
      @Param("criteria") String criteria,
      @Param("state") int state,
      Pageable pageable);

  List<DataDict> findByDataDictTypeCodeAndState(String typeCode, int state);
}