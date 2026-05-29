package cn.iocoder.yudao.module.crawler.source.runchina;

/**
 * 查询时遇到验证码，需要人工处理。
 */
public class CaptchaRequiredException extends RuntimeException {

    public CaptchaRequiredException(String message) {
        super(message);
    }
}
