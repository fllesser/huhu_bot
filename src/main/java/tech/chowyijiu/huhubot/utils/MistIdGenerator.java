package tech.chowyijiu.huhubot.utils;

/**
 * @author flless
 * @date 20/8/2023
 */
import java.util.concurrent.atomic.AtomicLong;
import java.util.Random;

public class MistIdGenerator {

    // 自增数
    private final static AtomicLong increase = new AtomicLong(0);

    // 随机因子一
    private final static Random randomOne = new Random();

    // 随机因子二
    private final static Random randomTwo = new Random();

    //生成一个全局唯一的ID
    public static long nextId() {
        // 获取自增数，并对其取模，防止超出范围
        // 自增数占用的位数 47
        long i = increase.getAndIncrement() % (1L << 47L);
        // 获取随机因子一
        long r1 = randomOne.nextInt(256);
        // 获取随机因子二
        long r2 = randomTwo.nextInt(256);
        // 按位运算将自增数、随机因子一、随机因子二组合成一个64位的长整型数字
        return ((i << 16) | (r1 << 8) | r2);
    }
}
