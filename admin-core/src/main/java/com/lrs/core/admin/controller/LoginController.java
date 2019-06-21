package com.lrs.core.admin.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lrs.common.constant.Const;
import com.lrs.common.constant.Result;
import com.lrs.core.admin.dto.LoginDTO;
import com.lrs.core.admin.entity.Menu;
import com.lrs.core.admin.entity.User;
import com.lrs.core.admin.service.ILoginService;
import com.lrs.core.base.BaseController;
import com.lrs.core.monitor.entity.EmailSendDetail;
import com.lrs.core.monitor.entity.Server;
import com.lrs.core.monitor.service.IEmailSendDetailService;
import com.lrs.core.monitor.service.IServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author rstyro
 * @since 2018-12-14
 */
@Controller
public class LoginController extends BaseController {
    @Value("${admin.name}")
    private String adminName;

    @Autowired
    private ILoginService loginService;

    @Autowired
    private IServerService serverService;

    @Autowired
    private IEmailSendDetailService emailSendDetailService;


    /**
     * 入口
     * @return
     */
    @RequestMapping(value={"/","/toLogin"},method= RequestMethod.GET)
    public String toLogin(){
        return "login";
    }

    /**
     * 首页
     * @param model
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/index")
    public String index(Model model){
        try {
            List<Menu> allMenu = (List<Menu>) this.getSession().getAttribute(Const.SESSION_ALL_MENU);
            if(allMenu != null){
                model.addAttribute("menus", allMenu);
            }
            int normalCount = serverService.count(new LambdaQueryWrapper<Server>().eq(Server::getStatus,1));
            int unLineCount = serverService.count(new LambdaQueryWrapper<Server>().eq(Server::getStatus,2));
            int sendMailCount = emailSendDetailService.count(new LambdaQueryWrapper<EmailSendDetail>());
            model.addAttribute("normalCount", normalCount);
            model.addAttribute("unLineCount", unLineCount);
            model.addAttribute("sendMailCount", sendMailCount);

            model.addAttribute("adminName", adminName);
            model.addAttribute("userName", ((User)this.getSession().getAttribute(Const.SESSION_USER)).getNickName());
            model.addAttribute("userPath", ((User)this.getSession().getAttribute(Const.SESSION_USER)).getPicPath());
            model.addAttribute("userStatus", "在线");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "index";
    }


    /**
     * 用户登录
     * @return
     */
    @RequestMapping(value={"/login"},method=RequestMethod.POST)
    @ResponseBody
    public Result login(LoginDTO dto) throws Exception {
        return loginService.login(dto, this.getSession());
    }

    /**
     * 用户注销
     * @return
     */
    @RequestMapping("/logout")
    public String logout() throws Exception {
        return loginService.logout(this.getSession());
    }

}
