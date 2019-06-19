package com.lrs.core.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lrs.core.monitor.entity.EmailSendDetail;
import com.lrs.core.monitor.entity.Server;
import com.lrs.core.monitor.service.IEmailSendDetailService;
import com.lrs.core.monitor.service.IServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class PageController {

	@Autowired
	private IServerService serverService;

	@Autowired
	private IEmailSendDetailService emailSendDetailService;


	@GetMapping("/include/{pageName}")
	public String include(Model model,@PathVariable("pageName") String pageName){
		System.out.println("/include/"+pageName);
		if(pageName.equalsIgnoreCase("main")){
			List<Server> serverList = serverService.list(new LambdaQueryWrapper<Server>().eq(Server::getIsDel, 0).orderByDesc(Server::getStatus).orderByDesc(Server::getCreateTime));
            int normalCount = serverService.count(new LambdaQueryWrapper<Server>().eq(Server::getIsDel, 0).eq(Server::getStatus,1));
            int unLineCount = serverService.count(new LambdaQueryWrapper<Server>().eq(Server::getIsDel, 0).eq(Server::getStatus,2));
            int sendMailCount = emailSendDetailService.count(new LambdaQueryWrapper<EmailSendDetail>().eq(EmailSendDetail::getIsDel,0));
            model.addAttribute("normalCount", normalCount);
            model.addAttribute("unLineCount", unLineCount);
            model.addAttribute("sendMailCount", sendMailCount);
            model.addAttribute("serverList", serverList);
		}
		return "include/"+pageName;
	}

	@GetMapping("/error/{pageNumber}")
	public String error(@PathVariable("pageNumber") String pageNumber){
		return "error/"+pageNumber;
	}
	
}
