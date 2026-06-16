package com.homeservice.notify.controller;

import com.homeservice.common.result.Result;
import com.homeservice.notify.service.NotifyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "通知服务（内部接口）")
@RestController
@RequestMapping("/internal/notify")
@RequiredArgsConstructor
public class NotifyController {

    private final NotifyService notifyService;

    @Operation(summary = "发送通知")
    @PostMapping("/send")
    public Result<Void> sendNotification(@RequestBody Map<String, Object> params) {
        String type = params.get("type") != null ? params.get("type").toString() : "sms";
        String target = params.get("target").toString();
        String template = params.get("template").toString();
        Map<String, String> templateParams = (Map<String, String>) params.get("params");
        
        notifyService.send(type, target, template, templateParams);
        return Result.success();
    }

    @Operation(summary = "发送短信验证码")
    @PostMapping("/sms-code")
    public Result<Void> sendSmsCode(@RequestBody Map<String, Object> params) {
        String phone = params.get("phone").toString();
        String code = params.get("code").toString();
        notifyService.sendSmsCode(phone, code);
        return Result.success();
    }
}