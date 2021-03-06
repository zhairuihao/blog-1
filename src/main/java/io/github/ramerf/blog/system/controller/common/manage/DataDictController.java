package io.github.ramerf.blog.system.controller.common.manage;

import io.github.ramerf.wind.core.entity.response.Rs;
import io.swagger.annotations.*;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import io.github.ramerf.blog.system.entity.Constant.AccessPath;
import io.github.ramerf.blog.system.entity.domain.common.DataDict;
import io.github.ramerf.blog.system.entity.pojo.common.DataDictPoJo;
import io.github.ramerf.blog.system.entity.request.common.DataDictRequest;
import io.github.ramerf.blog.system.entity.response.common.DataDictResponse;
import io.github.ramerf.blog.system.service.common.*;
import io.github.ramerf.blog.system.util.TextUtil;
import io.github.ramerf.blog.system.validator.common.DataDictValidator;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@Controller
@RequestMapping(AccessPath.MANAGE + "/dataDict")
@PreAuthorize("hasAnyAuthority('global:read','manage:read','dataDict:read')")
@Api(tags = "管理端: 数据字典接口")
@SuppressWarnings("UnusedDeclaration")
public class DataDictController {
  @Resource private DataDictTypeService typeService;
  @Resource private DataDictService service;
  @Resource private CommonService commonService;
  @Resource private DataDictValidator validator;

  @InitBinder(value = {"dataDict"})
  void initBinder(WebDataBinder binder) {
    binder.addValidators(validator);
  }

  @GetMapping("/index")
  @ApiOperation("数据字典页面")
  public String index(@ApiIgnore HttpSession session, @ApiIgnore Map<String, Object> map) {
    commonService.writeMenuAndSiteInfo(session, map);
    map.put("types", typeService.list(null));
    return "manage/data_dict/index";
  }

  @GetMapping("/page")
  @ResponseBody
  @ApiOperation("根据数据字典类型获取数据字典列表")
  public ResponseEntity<Rs<PageImpl<DataDictResponse>>> list(
      @RequestParam(value = "typeId", required = false) String typeIdStr,
      @RequestParam(value = "page", required = false) String pageStr,
      @RequestParam(value = "size", required = false) String sizeStr,
      @ApiParam("查询条件") @RequestParam(value = "criteria", required = false) String criteria) {
    final long typeId = TextUtil.validLong(typeIdStr, -1);
    final int[] pageAndSize = TextUtil.validFixPageAndSize(pageStr, sizeStr);
    return commonService.page(
        service.page(typeId, criteria, pageAndSize[0], pageAndSize[1]), DataDictResponse::of);
  }

  @GetMapping
  @ApiOperation("添加数据字典页面")
  public String create(@ApiIgnore HttpSession session, @ApiIgnore Map<String, Object> map) {
    commonService.writeMenuAndSiteInfo(session, map);
    map.put("types", typeService.list(null));
    return "manage/data_dict/edit";
  }

  @PostMapping
  @ResponseBody
  @PreAuthorize("hasAnyAuthority('global:create','dataDict:create')")
  @ApiOperation("添加数据字典")
  public ResponseEntity<Rs<Object>> create(
      @Valid DataDictRequest dataDictRequest, @ApiIgnore BindingResult bindingResult) {
    log.info(" DataDictController.create : [{}]", dataDictRequest);
    return commonService.create(service, DataDict.class, dataDictRequest, bindingResult);
  }

  @GetMapping("/{id}")
  @ApiOperation("更新数据字典页面")
  public String update(
      @PathVariable(value = "id") String idStr,
      @RequestParam(value = "typeId", required = false) String typeIdStr,
      @ApiIgnore HttpSession session,
      @ApiIgnore Map<String, Object> map) {
    return commonService.update(
        service,
        DataDictPoJo.class,
        idStr,
        "manage/data_dict/edit",
        map,
        "dataDict",
        id -> {
          commonService.writeMenuAndSiteInfo(session, map);
          map.put("types", typeService.list(null));
          map.put("dataDict", service.getById(id));
        },
        false);
  }

  @PutMapping("/{id}")
  @ResponseBody
  @PreAuthorize("hasAnyAuthority('global:write','dataDict:write')")
  @ApiOperation("更新数据字典")
  public ResponseEntity<Rs<Object>> update(
      @PathVariable(value = "id") String idStr,
      @Valid DataDictRequest dataDictRequest,
      @ApiIgnore BindingResult bindingResult) {
    log.info(" DataDictController.update : [{}]", dataDictRequest);
    return commonService.update(service, DataDict.class, dataDictRequest, idStr, bindingResult);
  }

  @DeleteMapping("/{id}")
  @ResponseBody
  @PreAuthorize("hasAnyAuthority('global:delete','dataDict:delete')")
  @ApiOperation("删除数据字典")
  public ResponseEntity<Rs<Object>> delete(@PathVariable("id") String idStr) {
    log.info(" DataDictController.delete : [{}]", idStr);
    return commonService.delete(service, idStr);
  }

  @DeleteMapping("/deleteBatch")
  @ResponseBody
  @PreAuthorize("hasAnyAuthority('global:delete','dataDict:delete')")
  @ApiOperation("删除数据字典批量")
  public ResponseEntity<Rs<Object>> deleteBatch(@RequestParam("ids") List<Long> ids) {
    log.info(" DataDictController.deleteBatch : [{}]", ids);
    return commonService.deleteBatch(service, ids);
  }
}
