package tech.chowyijiu.huhubot.utils;

/**
 * @author elastic chow
 * @date 22/6/2023
 */

public class ImageUtil {

    /**
     * 合并任数量的图片(128*128)成一张图片
     * @param imgs 待合并的图片数组
     *
     */
    //public static BufferedImage mergeImage(String mergePic, BufferedImage... imgs) throws IOException {
    //    // 生成新图片
    //    int wh = 128;
    //    //计算长宽
    //    int allW = wh * 11;
    //    int allH = (imgs.length / 10 + 2) * wh;
    //    // 创建新图片
    //    BufferedImage destImage = new BufferedImage(allW, allH, BufferedImage.TYPE_INT_RGB);
    //    // 合并所有子图片到新图片
    //    int wx = 0, wy = 0;
    //    for (int i = 1; i < imgs.length + 1; i++) {
    //        // 从图片中读取RGB
    //        BufferedImage img = imgs[i - 1];
    //        int[] ImageArrayOne = new int[wh * wh];
    //        ImageArrayOne = img.getRGB(0, 0, wh, wh, ImageArrayOne, 0, wh); // 逐行扫描图像中各个像素的RGB到数组中
    //        destImage.setRGB(wx, wy, wh, wh, ImageArrayOne, 0, wh);
    //        if (i % 10 == 0) {
    //            wx = 0;
    //            wy += 128;
    //        }
    //        wx += 128;
    //    }
    //    File outFile = new File(mergePic);
    //    // 写图片，输出到硬盘
    //    ImageIO.write(destImage, "png", outFile);
    //    return destImage;
    //}
}
