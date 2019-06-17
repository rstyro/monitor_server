package com.lrs.core.monitor.controller;

import com.lrs.common.annotation.Permission;
import com.lrs.common.annotation.PermissionType;
import com.lrs.common.dto.PageDTO;
import com.lrs.core.base.BaseController;
import com.lrs.core.monitor.entity.Server;
import com.lrs.core.monitor.service.IServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * <p>
 *  服务器监控 前端控制器
 * </p>
 *
 * @author rstyro
 * @since 2019-6-17
 */
@Controller
@RequestMapping("/monitor/server")
public class ServerController extends BaseController {

    private final static String qxurl = "monitor/server/list";

    @Autowired
    private IServerService serverService;

    @GetMapping("/list")
    public String list(Model model, PageDTO dto) throws Exception {
        System.out.println(dto);
        model.addAttribute("keyword",dto.getKeyword());
        model.addAttribute("list",serverService.getList(dto));
        return "page/monitor/server_list";
    }

    @PostMapping(value="/add")
    @ResponseBody
    @Permission(url = qxurl,type = PermissionType.ADD)
    public Object add(Server item) throws Exception {
        item.setId(null);
        return serverService.add(item,this.getAdminUser());
    }

    @PostMapping(value="/edit")
    @ResponseBody
    @Permission(url = qxurl,type = PermissionType.EDIT)
    public Object edit(Server item) throws Exception {
        return serverService.edit(item,this.getAdminUser());
    }

    @PostMapping(value="/del")
    @ResponseBody
    @Permission(url = qxurl,type = PermissionType.DEL)
    public Object del(Long id) throws Exception {
        return serverService.del(id,this.getAdminUser());
    }

    @GetMapping(value="/query")
    @ResponseBody
    @Permission(url = qxurl,type = PermissionType.QUERY)
    public Object query(Long id) throws Exception {
        return serverService.getDetail(id);
    }

}
