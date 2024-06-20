package com.bigo.project.system.controller;

import com.bigo.common.utils.google.CodeImgUtil;
import com.google.zxing.WriterException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: dingzhiwei
 * @date: 17/12/27
 * @description:
 */
@Controller
public class CodeImgController {


    /**
     * 获取二维码图片流
     */
    @RequestMapping("/google_auth/qrcode_img_get")
    public void getQrCodeImg(HttpServletRequest request, HttpServletResponse response) throws IOException, WriterException {
        String url = request.getParameter( "url");
        String widthStr = request.getParameter( "width");
        String heightStr = request.getParameter( "height");
        Integer width = Integer.valueOf(widthStr);
        Integer height = Integer.valueOf(heightStr);
        CodeImgUtil.writeQrCode(response.getOutputStream(), url, width, height);
    }

}
