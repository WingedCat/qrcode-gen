package edu.xpu.hcp.qrcode.utils;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Random;
/**                                                                                ____________________
      _                _                                                           < 神兽护体，永无bug! >
    | |__  _   _  ___| |__   ___ _ __   __ _ _ __   ___ _ __   __ _                --------------------
   | '_ \| | | |/ __| '_ \ / _ \ '_ \ / _` | '_ \ / _ \ '_ \ / _` |                       \   ^__^
  | | | | |_| | (__| | | |  __/ | | | (_| | |_) |  __/ | | | (_| |                        \  (oo)\_______
 |_| |_|\__,_|\___|_| |_|\___|_| |_|\__, | .__/ \___|_| |_|\__, |                           (__)\       )\/\
                                   |___/|_|                |___/                                ||----w |
                                                                                                ||     ||
 * @author huchengpeng
 * @date 2020/12/17 10:04
 * @version v1.0.0
 * @Description 二维码生成工具类
 */
@Slf4j
public class QRCodeUtil {

    private static final String CHARSET = "utf-8";
    private static final String FORMAT_NAME = "JPG";
    private static final String PERFIX = "data: image/jpeg;base64,";
    // 二维码尺寸
    private static final int QRCODE_SIZE = 300;
    // LOGO宽度
    private static final int WIDTH = 60;
    // LOGO高度
    private static final int HEIGHT = 60;

    /**
     * 创建二维码图片
     * @param content 内容
     * @param file LOGO文件
     * @param needCompress 是否需要压缩
     * @return BufferedImage 二维码图片
     * @throws Exception
     */
    private static BufferedImage createImage(String content, File file,
                                             boolean needCompress,boolean circle) throws Exception {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
                        : 0xFFFFFFFF);
            }
        }
        if (file == null || !file.exists()) {
            return image;
        }
        // 插入图片
        insertLogo(image, file, needCompress,circle);
        return image;
    }

    /**
     * 插入LOGO
     *
     * @param source
     *            二维码图片
     * @param file
     *            LOGO图片
     * @param needCompress
     *            是否压缩LOGO
     * @throws Exception
     */
    private static void insertLogo(BufferedImage source, File file,
                                    boolean needCompress,boolean circle) throws Exception {
        if (file == null) {
            System.err.println("该文件不存在！");
            return;
        }
        Image src = ImageIO.read(file);
        int width = src.getWidth(null);
        int height = src.getHeight(null);
        if (width > WIDTH) {
            width = WIDTH;
        }
        if (height > HEIGHT) {
            height = HEIGHT;
        }
        // 压缩LOGO
        if (needCompress) {
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_SMOOTH);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            // 绘制缩小后的图
            g.drawImage(image, 0, 0, null);
            g.dispose();
            src = image;
        }
        // 插入LOGO
        Graphics2D graph = source.createGraphics();
        int x = (QRCODE_SIZE - width) / 2;
        int y = (QRCODE_SIZE - height) / 2;
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
        if(circle){
            src = transferImgForRoundImgage(src);
            shape = new RoundRectangle2D.Float(x, y, width, width, 60, 60);
        }
        graph.drawImage(src, x, y, width, height, null);
        graph.setStroke(new BasicStroke(3f));
        graph.draw(shape);
        graph.dispose();
        FileUtil.deleteFile(file);
    }

    private static BufferedImage transferImgForRoundImgage(Image buffImg1){
        BufferedImage resultImg = null;

        if (buffImg1 == null) {
            return null;
        }
        resultImg = new BufferedImage(buffImg1.getWidth(null), buffImg1.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resultImg.createGraphics();
        Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, buffImg1.getWidth(null), buffImg1.getHeight(null));
        // 使用 setRenderingHint 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        resultImg = g.getDeviceConfiguration().createCompatibleImage(buffImg1.getWidth(null), buffImg1.getHeight(null),
                Transparency.TRANSLUCENT);
        //g.fill(new Rectangle(buffImg2.getWidth(), buffImg2.getHeight()));
        g = resultImg.createGraphics();
        // 使用 setRenderingHint 设置抗锯齿
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setClip(shape);
        g.drawImage(buffImg1, 0, 0, null);
        g.dispose();
        return resultImg;
    }

    /**
     * 生成二维码(内嵌LOGO)
     *
     * @param content
     *            内容
     * @param file
     *            LOGO文件
     * @param needCompress
     *            是否压缩LOGO
     * @throws Exception
     */
    private static byte[] genQRCode(String content, File file,boolean needCompress,boolean circle) throws Exception {
        BufferedImage image = createImage(content, file,needCompress,circle);

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ImageIO.write(image, FORMAT_NAME, byteArray);
        return byteArray.toByteArray();
    }

    public static String genQRCodeBase64(String content, File file,boolean needCompress,boolean circle) throws Exception{
        byte[] bytes = genQRCode(content, file, needCompress,circle);
        String base64str = Base64Util.encodeByte(bytes);
        return PERFIX + base64str;
    }

    public static String genQRCodeFileWithLogo(String content, String imgPath, String destPath,
                                boolean needCompress,boolean circle) throws Exception {
        BufferedImage image = createImage(content, new File(imgPath),
                needCompress,circle);
        mkdirs(destPath);
        String file = new Random().nextInt(99999999)+".jpg";
        ImageIO.write(image, FORMAT_NAME, new File(destPath+"/"+file));
        return file;
    }

    public static String genQRCodeFileNoLogo(String content, String destPath,boolean circle) throws Exception {
        BufferedImage image = createImage(content, null, false,circle);
        mkdirs(destPath);
        String file = new Random().nextInt(99999999)+".jpg";
        ImageIO.write(image, FORMAT_NAME, new File(destPath+"/"+file));
        return file;
    }


    /**
     * 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
     * @date 2013-12-11 上午10:16:36
     * @param destPath 存放目录
     */
    private static void mkdirs(String destPath) {
        File file =new File(destPath);
        //当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
        if (!file.exists() && !file.isDirectory()) {
            file.mkdirs();
        }
    }

    /**
     * 解析二维码
     *
     * @param file
     *            二维码图片
     * @return
     * @throws Exception
     */
    public static String decode(File file) throws Exception {
        BufferedImage image;
        image = ImageIO.read(file);
        if (image == null) {
            return null;
        }
        BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(
                image);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        Result result;
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
        hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
        result = new MultiFormatReader().decode(bitmap, hints);
        String resultStr = result.getText();
        FileUtil.deleteFile(file);
        return resultStr;
    }

    /**
     * 解析二维码
     *
     * @param path
     *            二维码图片地址
     * @return
     * @throws Exception
     */
    public static String decode(String path) throws Exception {
        return decode(new File(path));
    }

}
