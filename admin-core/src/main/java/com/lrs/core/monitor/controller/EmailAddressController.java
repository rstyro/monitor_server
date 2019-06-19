package com.lrs.core.monitor.controller;
import com.lrs.common.annotation.Permission;
import com.lrs.common.annotation.PermissionType;
import com.lrs.core.monitor.entity.EmailAddress;
import com.lrs.core.monitor.service.IEmailAddressService;
import com.lrs.common.dto.PageDTO;
import com.lrs.core.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * <p>
 *  报警邮件发送地址列表 前端控制器
 * </p>
 *
 * @author rstyro
 * @since 2019-6-19
 */
@Controller
@RequestMapping("/monitor/emailAddress")
public class EmailAddressController extends BaseController {

    private final static String qxurl = "monitor/emailAddress/list";

    @Autowired
    private IEmailAddressService emailAddressService;

    @GetMapping("/list")
    public String list(Model model, PageDTO dto) throws Exception {
        System.out.println(dto);
        model.addAttribute("list",emailAddressService.getList(dto));
        return "page/monitor/emailAddress_list";
    }

    @PostMapping(value="/add")
    @ResponseBody
    @Permission(url = qxurl,type = PermissionType.ADD)
    public Object add(EmailAddress item) throws Exception {
        item.setId(null);
        return emailAddressService.add(item,this.getSession());
    }

    @PostMapping(value="/edit")
    @ResponseBody
    @Permission(url = qxurl,type = PermissionType.EDIT)
    public Object edit(EmailAddress item) throws Exception {
        return emailAddressService.edit(item,this.getSession());
    }

    @PostMapping(value="/del")
    @ResponseBody
    @Permission(url = qxurl,type = PermissionType.DEL)
    public Object del(Long id) throws Exception {
        return emailAddressService.del(id,this.getSession());
    }

    @GetMapping(value="/query")
    @ResponseBody
    @Permission(url = qxurl,type = PermissionType.QUERY)
    public Object query(Long id) throws Exception {
        return emailAddressService.getDetail(id);
    }

}
