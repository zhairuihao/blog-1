package org.ramer.admin.system.entity.domain.common;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.ramer.admin.system.entity.domain.AbstractEntity;
import javax.persistence.*;
import org.hibernate.annotations.Table;

@Entity(name = ManageLog.TABLE_NAME)
@Table(appliesTo = ManageLog.TABLE_NAME, comment = "管理端日志")
@Data
@ToString(exclude = {"manager"})
@EqualsAndHashCode(callSuper = true)
public class ManageLog extends AbstractEntity {
  public static final String TABLE_NAME = "manage_log";

  /** 请求地址 */
  @Column(length = 200, columnDefinition = "VARCHAR(200) COMMENT '请求地址'")
  private String url;

  /** 请求IP */
  @Column(length = 20, columnDefinition = "VARCHAR(20) COMMENT '请求IP'")
  private String ip;

  /** 请求结果 */
  @Column(columnDefinition = "TEXT COMMENT '请求结果'")
  private String result;

  @OneToOne @JsonBackReference private Manager manager;
}
