package io.github.ramerf.blog.validator;

import io.github.ramerf.blog.entity.domain.Article;
import io.github.ramerf.blog.entity.pojo.ArticlePoJo;
import io.github.ramerf.blog.entity.pojo.TagPoJo;
import io.github.ramerf.blog.entity.request.ArticleRequest;
import io.github.ramerf.blog.service.TagService;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/** @author ramer */
@Component
public class ArticleValidator implements Validator {
  @Resource private TagService service;

  @Override
  public boolean supports(final Class<?> clazz) {
    return clazz.isAssignableFrom(Article.class)
        || clazz.isAssignableFrom(ArticleRequest.class)
        || clazz.isAssignableFrom(ArticlePoJo.class);
  }

  @Override
  public void validate(final Object target, @Nonnull final Errors errors) {
    ArticleRequest article = (ArticleRequest) target;
    if (article == null) {
      errors.rejectValue(null, "article.null", "文章 不能为空");
    } else {
      if (Objects.isNull(article.getId())) {
        if (StringUtils.isEmpty(article.getTitle())) {
          errors.rejectValue("title", "article.title.length", "标题 不能为空");
        }
        if (StringUtils.isEmpty(article.getDescription())) {
          errors.rejectValue("description", "article.description.length", "概述 不能为空");
        }
        if (CollectionUtils.isEmpty(article.getTagIds())) {
          errors.rejectValue("tagIds", "article.tagIds.empty", "标签 不能为空");
        } else {
          final boolean notExist =
              article.getTagIds().stream()
                  .anyMatch(o -> service.count(condition -> condition.eq(TagPoJo::setId, o)) < 1);
          if (notExist) {
            errors.rejectValue("tagIds", "article.tagIds.empty", "标签 值无效");
          }
        }
      }
    }
  }
}
