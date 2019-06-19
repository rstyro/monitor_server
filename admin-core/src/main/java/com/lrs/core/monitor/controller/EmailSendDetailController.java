package com.lrs.core.monitor.controller;
import com.lrs.common.annotation.Permission;
import com.lrs.common.annotation.PermissionType;
import com.lrs.core.monitor.entity.EmailSendDetail;
import com.lrs.core.monitor.service.IEmailSendDetailService;
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
 *  邮件发送记录 前端控制器
 * </p>
 *
 * @author rstyro
 * @since 2019-6-19
 */
@Controller
@RequestMapping("/monitor/emailSendDetail")
public class EmailSendDetailController extends BaseController {

    private final static String qxurl = "monitor/emailSendDetail/list";

    @Autowired
    private IEmailSendDetailService emailSendDetailService;

    @GetMapping("/list")
    public String list(Model model, PageDTO dto) throws Exception {
        System.out.println(dto);
        model.addAttribute("list",emailSendDetailService.getList(dto));
        return "page/monitor/emailSendDetail_list";
    }

    @PostMapping(value="/add")
    @ResponseBody
    @Permission(url = qxurl,type = PermissionType.ADD)
    public Object add(EmailSendDetail item) throws Exception {
        item.setId(null);
        return emailSendDetailService.add(item,this.getSession());
    }

    @PostMapping(value="/edit")
    @ResponseBody
    @Permission(url = qxurl,type = PermissionType.EDIT)
    public Object edit(EmailSendDetail item) throws Exception {
        return emailSendDetailService.edit(item,this.getSession());
    }

    @PostMapping(value="/del")
    @ResponseBody
    @Permission(url = qxurl,type = PermissionType.DEL)
    public Object del(Long id) throws Exception {
        return emailSendDetailService.del(id,this.getSession());
    }

    @GetMapping(value="/query")
    @ResponseBody
    @Permission(url = qxurl,type = PermissionType.QUERY)
    public Object query(Long id) throws Exception {
        return emailSendDetailService.getDetail(id);
    }

}
