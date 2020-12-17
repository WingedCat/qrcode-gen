package edu.xpu.hcp.qrcode.test;

import edu.xpu.hcp.qrcode.utils.QRCodeUtil;

public class QRCodeTest {
    public static void main(String[] args) throws Exception{

        String hello = QRCodeUtil.genQRCodeFileWithLogo("hello", "D://head.jpeg", "D://", true,true);
        System.out.println(hello);
    }
}
