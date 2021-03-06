package io.github.ramerf.blog.system.validator.common;

import io.github.ramerf.blog.system.entity.domain.common.CommonMediaCategory;
import io.github.ramerf.blog.system.entity.pojo.common.CommonMediaCategoryPoJo;
import io.github.ramerf.blog.system.entity.request.common.CommonMediaCategoryRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;


/** @author ramer */
@Component
public class CommonMediaCategoryValidator implements Validator {
  @Override
  public boolean supports(final Class<?> clazz) {
    return clazz.isAssignableFrom(CommonMediaCategory.class) || clazz.isAssignableFrom(CommonMediaCategoryRequest.class) || clazz.isAssignableFrom(CommonMediaCategoryPoJo.class);
  }

  @Override
  public void validate(final Object target, final Errors errors) {
    // TODO-WARN: 添加通用多媒体类别校验规则
  }
}
