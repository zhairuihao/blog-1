package io.github.ramerf.blog.system.service.common.impl;

import com.alibaba.fastjson.JSONObject;
import io.github.ramerf.wind.core.entity.response.Rs;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import io.github.ramerf.blog.system.entity.Constant.*;
import io.github.ramerf.blog.system.entity.domain.AbstractEntity;
import io.github.ramerf.blog.system.entity.domain.common.Manager;
import io.github.ramerf.blog.system.entity.pojo.AbstractEntityPoJo;
import io.github.ramerf.blog.system.entity.request.AbstractEntityRequest;
import io.github.ramerf.blog.system.entity.response.common.MenuResponse;
import io.github.ramerf.blog.system.exception.CommonException;
import io.github.ramerf.blog.system.service.BaseService;
import io.github.ramerf.blog.system.service.common.*;
import io.github.ramerf.blog.system.util.CollectionUtils;
import io.github.ramerf.blog.system.util.TextUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import springfox.documentation.annotations.ApiIgnore;

/** @author ramer */
@Component("manageCommon")
@Slf4j
public class CommonServiceImpl implements CommonService {
  @Resource private MenuService menuService;
  @Resource private ConfigService configService;

  @Override
  public void writeMenuAndSiteInfo(@ApiIgnore HttpSession session, Map<String, Object> map) {
    final Manager manager = (Manager) session.getAttribute(SessionKey.LOGIN_MANAGER);
    final Long managerId = manager.getId();
    // 所有可用菜单
    final List<MenuResponse> menuList =
        CollectionUtils.list(menuService.listByManager(managerId), MenuResponse::of, null, null);
    // 可用菜单的树形结构
    final List<MenuResponse> menus = Objects.requireNonNull(menuList, "菜单为空");
    final List<MenuResponse> responses =
        menus.stream()
            .filter(response -> Objects.isNull(response.getParentId()))
            .collect(Collectors.toList());
    menus.removeAll(responses);
    Stack<MenuResponse> retain = new Stack<>();
    responses.forEach(retain::push);
    while (retain.size() > 0 && menus.size() > 0) {
      MenuResponse menu = retain.pop();
      // 当前节点的子节点
      List<MenuResponse> children =
          menus.stream()
              .filter(menuResponse -> menuResponse.getParentId().equals(menu.getId()))
              .collect(Collectors.toList());
      // 子节点具有叶子节点,入栈
      children.stream().filter(MenuResponse::getHasChild).forEach(retain::push);
      menu.setChildren(children);
      menus.removeAll(children);
    }
    map.put("menus", responses);
    map.put(
        "menuLists",
        CollectionUtils.list(menuService.listByManager(managerId), MenuResponse::of, null, null));
    JSONObject siteJson = new JSONObject();
    siteJson.put("title", configService.getSiteInfo(ConfigCode.SITE_TITLE));
    siteJson.put("name", configService.getSiteInfo(ConfigCode.SITE_NAME));
    siteJson.put("copyright", configService.getSiteInfo(ConfigCode.SITE_COPYRIGHT));
    map.put("site", siteJson);
  }

  @Override
  public <S extends BaseService<T, E>, T extends AbstractEntity, E extends AbstractEntityPoJo>
      ResponseEntity create(S invoke, T entity, BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return Rs.fail(collectBindingResult(bindingResult));
    }
    try {
      entity = invoke.create(entity);
      return entity == null
          ? Rs.fail(Txt.FAIL_EXEC_ADD_EXIST)
          : entity.getId() > 0 ? Rs.ok(entity.getId()) : Rs.fail(Txt.FAIL_EXEC_ADD);
    } catch (Exception e) {
      log.warn(" CommonServiceImpl.create : [{}]", e.getMessage());
      return Rs.fail(
          !StringUtils.isEmpty(e.getMessage())
                  && (e instanceof CommonException || e instanceof NullPointerException)
              ? e.getMessage()
              : Txt.FAIL_EXEC);
    }
  }

  @Override
  public <S extends BaseService<T, E>, T extends AbstractEntity, E extends AbstractEntityPoJo>
      String update(
          S invoke,
          Class<E> clazz,
          final String idStr,
          final String page,
          Map<String, Object> map,
          String propName) {
    return update(invoke, clazz, idStr, page, map, propName, null, true);
  }

  @Override
  public <S extends BaseService<T, E>, T extends AbstractEntity, E extends AbstractEntityPoJo>
      String update(
          S invoke,
          Class<E> clazz,
          final String idStr,
          final String page,
          Map<String, Object> map,
          String propName,
          Consumer<Long> consumer) {
    return update(invoke, clazz, idStr, page, map, propName, consumer, true);
  }

  @Override
  public <S extends BaseService<T, E>, T extends AbstractEntity, E extends AbstractEntityPoJo>
      String update(
          S invoke,
          Class<E> clazz,
          final String idStr,
          final String page,
          Map<String, Object> map,
          String propName,
          Consumer<Long> consumer,
          boolean mapPoJo) {
    final long id = TextUtil.validLong(idStr, 0);
    if (id < 1) {
      throw CommonException.of("id 格式不正确");
    }
    if (Objects.nonNull(consumer)) {
      consumer.accept(id);
    }
    if (mapPoJo) {
      map.put(propName, invoke.getPoJoById(id, clazz));
    }
    return page;
  }

  @Override
  public <S extends BaseService<T, E>, T extends AbstractEntity, E extends AbstractEntityPoJo>
      ResponseEntity<Rs<Object>> update(
          S invoke, T entity, String idStr, BindingResult bindingResult) {
    final long id = TextUtil.validLong(idStr, 0);
    if (id < 1) {
      return Rs.wrongFormat("id");
    }
    if (bindingResult.hasErrors()) {
      return Rs.fail(collectBindingResult(bindingResult));
    }
    entity.setId(id);
    try {
      entity = invoke.update(entity);
      return entity == null
          ? Rs.fail(Txt.FAIL_EXEC_UPDATE_NOT_EXIST)
          : entity.getId() > 0
              ? Rs.ok(entity.getId(), Txt.SUCCESS_EXEC_UPDATE)
              : Rs.fail(Txt.FAIL_EXEC_UPDATE);
    } catch (Exception e) {
      log.warn(" CommonServiceImpl.update : [{}]", e.getMessage());
      return Rs.fail(
          !StringUtils.isEmpty(e.getMessage())
                  && (e instanceof CommonException || e instanceof NullPointerException)
              ? e.getMessage()
              : Txt.FAIL_EXEC);
    }
  }

  @Override
  public <
          S extends BaseService<T, E>,
          T extends AbstractEntity,
          E extends AbstractEntityPoJo,
          R extends AbstractEntityRequest>
      ResponseEntity<Rs<Object>> create(
          final S invoke,
          Class<T> clazz,
          final R entity,
          final BindingResult bindingResult,
          String... includeNullProperties) {
    return createOrUpdate(invoke, clazz, entity, bindingResult, true);
  }

  @Override
  public <
          S extends BaseService<T, E>,
          T extends AbstractEntity,
          E extends AbstractEntityPoJo,
          R extends AbstractEntityRequest>
      ResponseEntity<Rs<Object>> update(
          final S invoke,
          Class<T> clazz,
          final R entity,
          String idStr,
          final BindingResult bindingResult,
          String... includeNullProperties) {
    final long id = TextUtil.validLong(idStr, -1);
    if (id < 1) {
      return Rs.wrongFormat("id");
    }
    try {
      Objects.requireNonNull(BeanUtils.findDeclaredMethod(entity.getClass(), "setId", Long.class))
          .invoke(entity, id);
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.warn(" CommonServiceImpl.update : [{}]", e.getMessage());
      return Rs.fail(Txt.ERROR_SYSTEM);
    }
    return createOrUpdate(invoke, clazz, entity, bindingResult, false, includeNullProperties);
  }

  @Override
  public <S extends BaseService<T, E>, T extends AbstractEntity, E extends AbstractEntityPoJo>
      ResponseEntity<Rs<Object>> delete(final S invoke, final String idStr) {
    long id = TextUtil.validLong(idStr, 0);
    if (id < 1) {
      return Rs.wrongFormat("id");
    }
    try {
      invoke.delete(id);
    } catch (Exception e) {
      log.warn(" CommonServiceImpl.delete : [{}]", e.getMessage());
      return Rs.fail(StringUtils.isEmpty(e.getMessage()) ? Txt.FAIL_EXEC : e.getMessage());
    }
    return Rs.ok(null, Txt.SUCCESS_EXEC_DELETE);
  }

  @Override
  public <S extends BaseService<T, E>, T extends AbstractEntity, E extends AbstractEntityPoJo>
      ResponseEntity<Rs<Object>> deleteBatch(final S invoke, final List<Long> ids) {
    try {
      invoke.deleteBatch(ids);
    } catch (Exception e) {
      log.warn(" CommonServiceImpl.deleteBatch : [{}]", e.getMessage());
      return Rs.fail(StringUtils.isEmpty(e.getMessage()) ? Txt.FAIL_EXEC : e.getMessage());
    }
    return Rs.ok(null, Txt.SUCCESS_EXEC_DELETE);
  }

  @Override
  public <T extends AbstractEntity, E> ResponseEntity<Rs<List<E>>> list(
      final List<T> lists, final Function<T, E> function, final Predicate<E> filterFunction) {
    return Rs.ok(
        Objects.isNull(filterFunction)
            ? lists.stream().map(function).collect(Collectors.toList())
            : lists.stream().map(function).filter(filterFunction).collect(Collectors.toList()));
  }

  @Override
  public <T extends AbstractEntity, R> ResponseEntity<Rs<PageImpl<R>>> page(
      final Page<T> page, final Function<T, R> function) {
    return Rs.ok(
        new PageImpl<>(
            page.getContent().stream().map(function).collect(Collectors.toList()),
            page.getPageable(),
            page.getTotalElements()));
  }
  //
  //  @Override
  //  public <T extends AbstractEntity, R> ResponseEntity<CommonResponse<R>> page(
  //      final Page<T> page, final Function<T, R> function) {
  //    return null;
  //  }

  @Override
  public String collectBindingResult(BindingResult bindingResult) {
    StringBuilder errorMsg = new StringBuilder("提交信息有误: ");
    bindingResult
        .getAllErrors()
        .forEach(
            error ->
                errorMsg
                    .append("<br/>")
                    .append(
                        Objects.requireNonNull(error.getDefaultMessage())
                                .contains("Failed to convert property")
                            ? ((FieldError) error).getField() + " 格式不正确"
                            : error.getDefaultMessage()));
    return errorMsg.toString();
  }
  /**
   * 创建或更新.
   *
   * @param invoke 服务层实现类.
   * @param entity 要更新的request {@link AbstractEntityRequest} 对象.
   * @param create 是否是创建.
   * @param bindingResult 校验器校验结果.
   * @param <T> 服务层实现类.
   * @param <E> 要更新的对象.
   * @return {@link ResponseEntity}
   */
  private <
          S extends BaseService<T, E>,
          T extends AbstractEntity,
          E extends AbstractEntityPoJo,
          R extends AbstractEntityRequest>
      ResponseEntity<Rs<Object>> createOrUpdate(
          final S invoke,
          Class<T> clazz,
          final R entity,
          final BindingResult bindingResult,
          boolean create,
          String... includeNullProperties) {
    if (bindingResult.hasErrors()) {
      return Rs.fail(collectBindingResult(bindingResult));
    }
    try {
      if (create) {
        Objects.requireNonNull(BeanUtils.findDeclaredMethod(entity.getClass(), "setId", Long.class))
            .invoke(entity, (Long) null);
      }
      final T obj = invoke.createOrUpdate(clazz, entity, includeNullProperties);
      return Objects.isNull(obj)
          ? Rs.fail(create ? Txt.FAIL_EXEC_ADD : Txt.FAIL_EXEC_UPDATE_NOT_EXIST)
          : Rs.ok(obj.getId(), create ? Txt.SUCCESS_EXEC_ADD : Txt.SUCCESS_EXEC_UPDATE);
    } catch (Exception e) {
      log.warn(
          " CommonServiceImpl.update : [{}]",
          e instanceof CommonException || e instanceof NullPointerException
              ? e.getMessage()
              : Txt.ERROR_SYSTEM);
      log.error(e.getMessage(), e);
      return Rs.fail(
          !StringUtils.isEmpty(e.getMessage())
                  && (e instanceof CommonException || e instanceof NullPointerException)
              ? e.getMessage()
              : Txt.FAIL_EXEC);
    }
  }
}
