package cn.iocoder.yudao.module.crawler.merge;

/**
 * 合并策略接口
 */
public interface MergeStrategy<T> {

    /**
     * 判断爬取数据与现有数据的关系。
     * @param crawled 爬取到的数据
     * @param existing DB 中已有的数据（null 表示不存在）
     * @return 合并动作
     */
    MergeAction decide(T crawled, T existing);
}
