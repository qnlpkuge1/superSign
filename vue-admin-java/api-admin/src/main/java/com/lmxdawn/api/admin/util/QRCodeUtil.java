package com.lmxdawn.api.admin.util;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Hashtable;

public class QRCodeUtil {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeUtil.class);

    public static String SaveQrCode(String url, String format, int width, int height, String destPath, String fileName) {

        BufferedImage image;
        try {
            image = QRCodeUtil.createImage(url, width, height);
            File file = new File(destPath);
            // 当文件夹不存在时，mkdirs会自动创建多层目录，区别于mkdir．(mkdir如果父目录不存在则会抛出异常)
            if (!file.exists() && !file.isDirectory()) {
                file.mkdirs();
            }
            String fileUrl = destPath + File.separator + fileName + "." + format;
            ImageIO.write(image, format, new File(fileUrl));
            return fileUrl;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 生成二维码图片私有方法
     */
    private static BufferedImage createImage(String url, int width, int height) throws Exception {
        Hashtable hints = new Hashtable();

        // 二维码纠错级别：由高到低 H、Q、M、L
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        // 二维码边界空白大小由大到小 4、3、2、1(默认为4)
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(url,
                BarcodeFormat.QR_CODE, width, height, hints);

        int H = bitMatrix.getHeight();
        int W = bitMatrix.getWidth();
        int L = getFinderPatternWidth(bitMatrix) + 3;
        int[] pixels = new int[W * H];
        // 二维码角颜色，依次为左上、左下、右上
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                Color color = new Color(10, 10, 10);
                int colorInt = color.getRGB();
                // 此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色；
                pixels[y * W + x] = bitMatrix.get(x, y) ? colorInt : 0xffffff;
            }
        }

        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        image.getRaster().setDataElements(0, 0, W, H, pixels);

        return image;
    }

    private static int getFinderPatternWidth(BitMatrix matrix) {
        int W = matrix.getWidth();
        int H = matrix.getHeight();
        int length = 0;
        boolean flag = false;
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                if (matrix.get(x, y) == true) {
                    flag = true;
                    length++;
                } else {
                    if (flag != false) {
                        return x;
                    }
                }
            }
        }
        return length;
    }

}