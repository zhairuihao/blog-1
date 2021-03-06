package io.github.ramerf.blog.system.entity.domain;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/** domain类的父类 */
@Data
@MappedSuperclass
@NoArgsConstructor
public abstract class AbstractEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(columnDefinition = "TINYINT DEFAULT 1")
  private Boolean isDelete = Boolean.FALSE;

  @CreationTimestamp
  @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'")
  private Date createTime = new Date();

  @UpdateTimestamp
  @Column(
      columnDefinition =
          "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'")
  private Date updateTime = new Date();
}
