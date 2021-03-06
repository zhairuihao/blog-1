package io.github.ramerf.blog.system.entity.domain.common;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.Objects;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.Table;
import org.hibernate.annotations.Where;
import io.github.ramerf.blog.system.entity.domain.AbstractEntity;

@Entity(name = Menu.TABLE_NAME)
@Table(appliesTo = Menu.TABLE_NAME, comment = "菜单")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Menu extends AbstractEntity {
  public static final String TABLE_NAME = "menu";

  /** 名称 */
  @Column(columnDefinition = "VARCHAR(25) COMMENT '名称'")
  private String name;

  /** 地址 */
  @Column(columnDefinition = "VARCHAR(100) COMMENT '地址'")
  private String url;

  /** 排序权重 */
  @Column(columnDefinition = "TINYINT(4) COMMENT '排序权重'")
  @Builder.Default
  private Integer sortWeight = 0;

  /** ICON FONT 图标 */
  @Column(columnDefinition = "VARCHAR(25) DEFAULT 'people' COMMENT 'ICON FONT图标'")
  @Builder.Default
  private String icon = "people";

  /** 备注 */
  @Column(columnDefinition = "VARCHAR(100) COMMENT '备注'")
  private String remark;

  /** 是否有子节点 */
  @Column(columnDefinition = "BIT DEFAULT 0 COMMENT '是否有子节点'")
  @Builder.Default
  private Boolean hasChild = false;

  /** 父级 */
  @Column(name = "parent_id")
  private Long parentId;

  @ManyToOne
  @JoinColumn(
      name = "parent_id",
      insertable = false,
      updatable = false,
      foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
  @JsonBackReference
  @Where(clause = "is_delete = false")
  private Menu parent;

  public static Menu of(Long id) {
    if (Objects.isNull(id)) {
      return null;
    }
    final Menu menu = new Menu();
    menu.setId(id);
    return menu;
  }
}
