package edu.xpu.hcp.qrcode.controller;

import edu.xpu.hcp.qrcode.utils.FileUtil;
import edu.xpu.hcp.qrcode.utils.QRCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/qrcode")
public class QRCodeGenController {

    @PostMapping("/base64")
    public Map<String,Object> genQrcode(String content, @RequestParam(required = false) MultipartFile logoFile, @RequestParam(defaultValue = "false",required = false) boolean circle) throws Exception{
        Map<String,Object> res = new HashMap<>();
        if(StringUtils.isBlank(content)){
            res.put("code",400);
            return res;
        }
        String base64 = QRCodeUtil.genQRCodeBase64(content, FileUtil.mult2file(logoFile), true,circle);
        if (StringUtils.isBlank(base64)){
            res.put("datauri","");
            res.put("code",400);
            res.put("circle",circle);
        }else{
            res.put("datauri",base64);
            res.put("code",200);
            res.put("circle",circle);
        }
        return res;
    }

    @PostMapping("/decode")
    public Map<String,Object> decodeQrcode(@RequestParam MultipartFile file) throws Exception{
        Map<String,Object> res = new HashMap<>();
        String decodeResult = QRCodeUtil.decode(FileUtil.mult2file(file));
        if (StringUtils.isBlank(decodeResult)){
            res.put("result","");
            res.put("code",400);
        }else{
            res.put("result",decodeResult);
            res.put("code",200);
        }
        return res;
    }

    


}
