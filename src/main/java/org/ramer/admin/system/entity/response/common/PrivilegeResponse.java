package org.ramer.admin.system.entity.response.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import lombok.*;
import org.ramer.admin.system.entity.domain.common.Privilege;
import org.ramer.admin.system.entity.response.AbstractEntityResponse;
import org.springframework.beans.BeanUtils;

/**
 * 权限.
 *
 * @author ramer
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "权限")
public class PrivilegeResponse extends AbstractEntityResponse {

  @ApiModelProperty(value = "权限表达式,class前缀:(read|create|write|delete)", example = "manager:read")
  private String exp;

  @ApiModelProperty(value = "名称")
  private String name;

  public static PrivilegeResponse of(final Privilege privilege) {
    if (Objects.isNull(privilege)) {
      return null;
    }
    PrivilegeResponse poJo = new PrivilegeResponse();
    BeanUtils.copyProperties(privilege, poJo);
    return poJo;
  }
}
