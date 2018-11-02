package com.cmb.o2o.hackson.nicetomeetyou.controller;

import com.alibaba.fastjson.JSON;
import com.cmb.o2o.hackson.nicetomeetyou.model.ClientMessage;
import com.cmb.o2o.hackson.nicetomeetyou.model.ServerMessage;
import com.cmb.o2o.hackson.nicetomeetyou.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sendTest/{userId}")
    @SendTo("/topic/subscribeTest/{userId}")
    public ServerMessage sendDemo(ClientMessage message) {
        System.out.println("接收到了信息" + JSON.toJSONString(message));
        return new ServerMessage(JSON.toJSONString(message));
    }

    @SubscribeMapping("/subscribeTest/{userId}")
    public ServerMessage sub() {
        System.out.println("用户订阅了我。。。");
        return new ServerMessage("感谢你订阅了我。。。");
    }

    @RequestMapping("/sendSingle")
    @ResponseBody
    public String sendSingle(@RequestParam("userId") String userId,@RequestParam("msg") String msg) {
        System.out.println("接收到了信息" + userId);
        messagingTemplate.convertAndSendToUser(userId, "/queue/message", new ServerMessage(msg));
        return "1";
    }

    @RequestMapping("/img/upload")
    public void uploadImg(@RequestParam("file") MultipartFile multipartFile)  {
        if (multipartFile.isEmpty() || multipartFile.getOriginalFilename()==null||multipartFile.getOriginalFilename()=="") {
            System.out.println("上传异常");
        }
        String contentType = multipartFile.getContentType();
        if (!contentType.contains("")) {
            System.out.print("上传异常"+JSON.toJSONString(multipartFile));
        }
        String root_fileName = multipartFile.getOriginalFilename();
        System.out.println("上传图片:name={},type={}"+ root_fileName+","+contentType);

        String filePath = "C:\\Users\\z675558\\Desktop\\html";
        String file_name = null;
        try {
            file_name = ImageUtil.saveImg(multipartFile, filePath);
            messagingTemplate.convertAndSendToUser("test2", "/queue/message", new ServerMessage("127.0.0.1:8080/"+file_name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
